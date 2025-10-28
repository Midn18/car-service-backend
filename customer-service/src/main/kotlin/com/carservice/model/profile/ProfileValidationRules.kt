package com.carservice.model.profile

import io.konform.validation.ValidationBuilder
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern

const val MOLDOVA_PHONE_REGEX = "^\\+373(6\\d{7}|7\\d{7}|[2-5]\\d{7})$"
const val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"

val validateFirstName: ValidationBuilder<String>.() -> Unit = {
    minLength(2) hint "First name must be at least 2 characters long"
    maxLength(30) hint "First name must not exceed 30 characters"
    pattern("^[^0-9]*$".toRegex()) hint "First name must not contain digits"
}

val validateLastName: ValidationBuilder<String>.() -> Unit = {
    minLength(2) hint "Last name must be at least 2 characters long"
    maxLength(30) hint "Last name must not exceed 30 characters"
    pattern("^[^0-9]*$".toRegex()) hint "Last name must not contain digits"
}

val validatePhoneNumber: ValidationBuilder<String>.() -> Unit = {
    pattern(MOLDOVA_PHONE_REGEX) hint "Invalid phone number format"
}

val validateEmail: ValidationBuilder<String>.() -> Unit = {
    pattern(EMAIL_REGEX) hint "Invalid email address format"
}

val validatePassword: ValidationBuilder<String>.() -> Unit = {
    minLength(8) hint "Password must be at least 8 characters long"
    maxLength(50) hint "Password must not exceed 50 characters"
    pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$") hint "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
}

const val MIN_LENGTH = 2
const val MAX_LENGTH = 40
val validateAddress: ValidationBuilder<Address>.() -> Unit = {
    Address::street required {
        minLength(MIN_LENGTH) hint "Street must be at least 2 characters long"
        maxLength(MAX_LENGTH) hint "Street must not exceed 40 characters"
    }
    Address::city required {
        minLength(MIN_LENGTH) hint "City must be at least 2 characters long"
        maxLength(MAX_LENGTH) hint "City must not exceed 40 characters"
    }
    Address::postalCode required {
        pattern("^[A-Z0-9]{2,10}\$") hint "Postal code must be alphanumeric and between 2 to 10 characters long"
    }
    Address::country required {
        minLength(MIN_LENGTH) hint "Country must be at least 2 characters long"
        maxLength(MAX_LENGTH) hint "Country must not exceed 30 characters"
    }
}
