package com.carservice.validation


import com.carservice.dto.authorization.CustomerSignupRequest
import com.carservice.dto.authorization.EmployeeSignupRequest
import com.carservice.model.profile.*
import io.konform.validation.Validation

val customerSignupValidator = Validation {
    CustomerSignupRequest::firstName required validateFirstName
    CustomerSignupRequest::lastName required validateLastName
    CustomerSignupRequest::email required validateEmail
    CustomerSignupRequest::password required validatePassword
    CustomerSignupRequest::phoneNumber required validatePhoneNumber
    CustomerSignupRequest::address required validateAddress
}

val employeeSignUpValidator = Validation {
    EmployeeSignupRequest::firstName required validateFirstName
    EmployeeSignupRequest::lastName required validateLastName
    EmployeeSignupRequest::email required validateEmail
    EmployeeSignupRequest::password required validatePassword
    EmployeeSignupRequest::phoneNumber required validatePhoneNumber
    EmployeeSignupRequest::address required validateAddress
    EmployeeSignupRequest::role required {
        arrayOf<String>()
        constrain("At least one role must be specified") {
            it.isNotEmpty()
        }
    }
}