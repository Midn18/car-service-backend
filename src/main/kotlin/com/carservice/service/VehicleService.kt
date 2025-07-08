package com.carservice.service

import com.carservice.model.vehicle.Vehicle
import com.carservice.model.profile.Customer
import com.carservice.repository.ProfileRepository
import com.carservice.repository.VehicleRepository
import com.carservice.security.AuthorizationHelper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val profileRepository: ProfileRepository,
    private val authorizationHelper: AuthorizationHelper
) {

    fun getAllVehicles(
        pageNumber: Int? = null,
        pageSize: Int? = null,
        registrationNumber: String? = null,
        carVin: String? = null,
        ownerId: String? = null,
        make: String? = null,
        model: String? = null
    ): List<Vehicle> {
        authorizationHelper.checkEmployeePrivileges()

        val pageable = if (pageNumber != null && pageSize != null) {
            PageRequest.of(pageNumber - 1, pageSize)
        } else {
            Pageable.unpaged()
        }

        val filteredVehicles = vehicleRepository.findAllVehiclesByFilters(
            pageable, registrationNumber, carVin, ownerId, make, model
        )

        return filteredVehicles.content
    }

    fun addNewVehicle(vehicle: Vehicle): Vehicle {
        val requester = authorizationHelper.getRequester()

        if (!authorizationHelper.hasEmployeePrivileges(requester) && vehicle.owner.id != requester.id) {
            throw AccessDeniedException("You are not allowed to add vehicles to this profile.")
        }

        val ownerProfile = profileRepository.findById(vehicle.owner.id)
            .orElseThrow { NoSuchElementException("Owner with ID ${vehicle.owner.id} not found") }

        if (ownerProfile !is Customer) {
            throw IllegalArgumentException("Only Customer profiles can have vehicles.")
        }

        if (vehicleRepository.existsById(vehicle.vin)) {
            throw IllegalArgumentException("A vehicle with VIN '${vehicle.vin}' already exists.")
        }

        if (vehicle.registrationNumber.isNotBlank()) {
            val existingWithRegNumber = vehicleRepository.findByRegistrationNumber(vehicle.registrationNumber)
            if (existingWithRegNumber != null) {
                throw IllegalArgumentException("A vehicle with registration number '${vehicle.registrationNumber}' already exists.")
            }
        }

        val vehicleToSave = vehicle.copy(serviceHistory = emptyList())

        val savedVehicle = vehicleRepository.save(vehicleToSave)

        val updatedCustomer = ownerProfile.copy(
            vehiclesVin = ownerProfile.vehiclesVin + savedVehicle.vin
        )
        profileRepository.save(updatedCustomer)

        return savedVehicle
    }

    fun deleteVehicle(vin: String) {
        val requester = authorizationHelper.getRequester()
        val vehicle = vehicleRepository.findById(vin)
            .orElseThrow { NoSuchElementException("Vehicle with VIN $vin not found") }

        if (!authorizationHelper.hasEmployeePrivileges(requester) && vehicle.owner.id != requester.id) {
            throw AccessDeniedException("You are not allowed to delete this vehicle.")
        }

        vehicleRepository.delete(vehicle)

        val ownerProfile = profileRepository.findById(vehicle.owner.id)
            .orElseThrow { NoSuchElementException("Owner with ID ${vehicle.owner.id} not found") }

        if (ownerProfile is Customer) {
            val updatedCustomer = ownerProfile.copy(
                vehiclesVin = ownerProfile.vehiclesVin.filterNot { it == vin }
            )
            profileRepository.save(updatedCustomer)
        }
    }
}