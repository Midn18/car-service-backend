package com.carservice.model.profile

import com.fasterxml.jackson.annotation.JsonProperty
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
    val id: String
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
    override val id: String = UUID.randomUUID().toString(),
    override val firstName: String = "",
    override val lastName: String = "",
    override val email: String = "",
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override val password: String = "",
    override val phoneNumber: String = "",
    override val dateOfBirth: LocalDate,
    override val address: Address,
    override val role: Set<UserRole>,
    override val profileType: ProfileType = ProfileType.CUSTOMER,
    val vehiclesVin: List<String> = emptyList(),
    val serviceVisitIds: List<String> = emptyList(),
    val visitCounter: Int = 0,
) : Profile

@Document(collection = "profile")
data class Employee(
    @Id
    override val id: String = UUID.randomUUID().toString(),
    override val firstName: String = "",
    override val lastName: String = "",
    override val email: String = "",
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    override val password: String = "",
    override val phoneNumber: String = "",
    override val dateOfBirth: LocalDate,
    override val address: Address,
    override val role: Set<UserRole>,
    override val profileType: ProfileType = ProfileType.EMPLOYEE,
) : Profile

data class Address(
    val street: String = "",
    val city: String = "",
    val postalCode: String = "",
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

fun Profile.toSkinnyProfile(): SkinnyProfile {
    return SkinnyProfile(
        id = this.id,
        profileType = this.profileType,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        role = this.role
    )
}