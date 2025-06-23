package com.carservice.mapper

import com.carservice.model.profile.Address
import com.carservice.model.Address as ApiAddress
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.Customer as ApiCustomer
import com.carservice.model.Employee as ApiEmployee
import com.carservice.model.profile.Profile
import com.carservice.model.profile.UserRole
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProfileMapper {
    fun convertToDomainAddress(openApiAddress: ApiAddress): Address {
        return Address(
            street = openApiAddress.street,
            city = openApiAddress.city,
            postalCode = openApiAddress.postalCode,
            country = openApiAddress.country
        )
    }

    fun toApiProfile(profile: Profile): Any {
        return when (profile) {
            is Customer -> toApiCustomer(profile)
            is Employee -> toApiEmployee(profile)
            else -> throw IllegalArgumentException("Unknown profile type: ${profile.javaClass}")
        }
    }

    private fun toApiCustomer(customer: Customer): ApiCustomer {
        return ApiCustomer(
            id = UUID.fromString(customer.id),
            profileType = com.carservice.model.Customer.ProfileType.CUSTOMER,
            firstName = customer.firstName,
            lastName = customer.lastName,
            email = customer.email,
            password = customer.password,
            phoneNumber = customer.phoneNumber,
            dateOfBirth = customer.dateOfBirth,
            address = toApiAddress(customer.address),
            role = customer.role.map(UserRole::name),
            vehicles = emptyList(),
            serviceVisits = emptyList(),
            visitCounter = customer.visitCounter
        )
    }


    private fun toApiEmployee(employee: Employee): ApiEmployee {
        return ApiEmployee(
            id = UUID.fromString(employee.id),
            profileType = com.carservice.model.Employee.ProfileType.EMPLOYEE,
            firstName = employee.firstName,
            lastName = employee.lastName,
            email = employee.email,
            password = employee.password,
            phoneNumber = employee.phoneNumber,
            dateOfBirth = employee.dateOfBirth,
            address = toApiAddress(employee.address),
            role = employee.role.map(UserRole::name)
        )
    }

    private fun toApiAddress(address: com.carservice.model.profile.Address): ApiAddress {
        return ApiAddress(
            street = address.street,
            city = address.city,
            postalCode = address.postalCode,
            country = address.country
        )
    }
}
