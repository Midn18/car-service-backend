package com.carservice.validation


import com.carservice.model.profile.*
import io.konform.validation.Validation

val customerSignupValidator = Validation {
    Customer::firstName required validateFirstName
    Customer::lastName required validateLastName
    Customer::email required validateEmail
    Customer::password required validatePassword
    Customer::phoneNumber required validatePhoneNumber
    Customer::address required validateAddress
}

val employeeSignUpValidator = Validation {
    Employee::firstName required validateFirstName
    Employee::lastName required validateLastName
    Employee::email required validateEmail
    Employee::password required validatePassword
    Employee::phoneNumber required validatePhoneNumber
    Employee::address required validateAddress
    Employee::role required {
        arrayOf<String>()
        constrain("At least one role must be specified") {
            it.isNotEmpty()
        }
    }
}