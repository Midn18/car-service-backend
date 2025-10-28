package com.carservice.controller

import com.carservice.dto.vehicle.VehicleUpdateRequest
import com.carservice.model.vehicle.Vehicle
import com.carservice.service.VehicleService
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/vehicles")
class VehicleController(
    private val vehicleService: VehicleService
) {

    @PreAuthorize("@security.isEmployee(authentication)")
    @GetMapping("/")
    fun getVehicles(
        @RequestParam(name = "page_number", required = false)
        @Min(1, message = "Page number must be greater than 0")
        pageNumber: Int?,
        @RequestParam(name = "page_size", required = false)
        @Min(1, message = "Page size must be at least 1")
        @Max(100, message = "Page size cannot be more than 100")
        pageSize: Int?,
        @RequestParam(name = "registration_number", required = false)
        registrationNumber: String?,
        @RequestParam(name = "car_vin", required = false)
        @Size(min = 17, message = "VIN must be at least 17 characters")
        carVin: String?,
        @RequestParam(name = "owner_id", required = false)
        ownerId: String?,
        @RequestParam(name = "make", required = false)
        make: String?,
        @RequestParam(name = "model", required = false)
        model: String?
    ): ResponseEntity<List<Vehicle>> {
        val vehicles = vehicleService.getAllVehicles(
            pageNumber = pageNumber,
            pageSize = pageSize,
            registrationNumber = registrationNumber,
            carVin = carVin,
            ownerId = ownerId,
            make = make,
            model = model
        )
        return ResponseEntity.ok(vehicles)
    }

    @PreAuthorize("@security.isEmployee(authentication)")
    @PostMapping("/add")
    fun addVehicleToProfile(@RequestBody vehicleRequest: Vehicle): ResponseEntity<String> {
        val vehicle = vehicleService.addNewVehicle(vehicleRequest)
        return ResponseEntity.ok("Vehicle added successfully: ${vehicle.vin}, ${vehicle.make} ${vehicle.model}")
    }

    @PreAuthorize("@security.isEmployee(authentication)")
    @PutMapping("/{vin}")
    fun updateVehicle(
        @PathVariable vin: String,
        @RequestBody vehicleRequest: VehicleUpdateRequest
    ): ResponseEntity<String> {
        val updatedVehicle = vehicleService.updateVehicle(vin, vehicleRequest)
        return ResponseEntity.ok("Vehicle updated successfully: ${updatedVehicle.vin}, ${updatedVehicle.make} ${updatedVehicle.model}")
    }

    @PreAuthorize("@security.isEmployee(authentication)")
    @DeleteMapping("/{vin}")
    fun deleteVehicle(@PathVariable vin: String): ResponseEntity<String> {
        vehicleService.deleteVehicle(vin)
        return ResponseEntity.ok("Vehicle with VIN $vin deleted successfully.")
    }
}