package com.carservice.model.profile

import com.carservice.model.ServiceVisit
import com.carservice.model.Vehicle
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.util.UUID
import kotlin.reflect.KClass

enum class ProfileType(val profileClass: KClass<out Profile>) {
    CUSTOMER(Customer::class),
    EMPLOYEE(Employee::class),
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "profileType",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Customer::class, name = "CUSTOMER"),
    JsonSubTypes.Type(value = Employee::class, name = "EMPLOYEE")
)
interface Profile {
    val profileType: ProfileType
    val id: UUID
    val firstName: String
    val lastName: String
    val email: String
    val password: String
    val phoneNumber: String
    val dateOfBirth: LocalDate
    val address: Address
    val role: Set<UserRole>
}

@Document(collection = "profile")
data class Customer(
    @Id
    override val id: UUID = UUID.randomUUID(),
    override val firstName: String = "",
    override val lastName: String = "",
    override val email: String = "",
    override val password: String = "",
    override val phoneNumber: String = "",
    override val dateOfBirth: LocalDate,
    override val address: Address,
    override val role: Set<UserRole>,
    val vehicles: List<Vehicle> = emptyList(),
    val serviceVisits: List<ServiceVisit> = emptyList(),
    val visitCounter: Int = 0,
) : Profile {
    override val profileType: ProfileType
        get() = ProfileType.CUSTOMER
}

@Document(collection = "profile")
data class Employee(
    @Id
    override val id: UUID = UUID.randomUUID(),
    override val firstName: String = "",
    override val lastName: String = "",
    override val email: String = "",
    override val password: String = "",
    override val phoneNumber: String = "",
    override val dateOfBirth: LocalDate,
    override val address: Address,
    override val role: Set<UserRole>,
) : Profile {
    override val profileType: ProfileType
        get() = ProfileType.EMPLOYEE
}

data class Address(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = ""
)

enum class UserRole {
    ADMIN,
    MECHANIC,
    CAR_DETAILER,
    CAR_PAINTER,
    ELECTRICIAN,
    GUEST,
    REGULAR,
    LOYAL,
}

fun UserRole.isEmployeeRole() = this in listOf(
    UserRole.MECHANIC,
    UserRole.CAR_DETAILER,
    UserRole.CAR_PAINTER,
    UserRole.ELECTRICIAN
)

fun UserRole.isAdmin() = this == UserRole.ADMIN