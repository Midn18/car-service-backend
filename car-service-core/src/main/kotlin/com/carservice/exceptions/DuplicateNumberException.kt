package com.carservice.exceptions

class DuplicateNumberException(phoneNumber: String) :
    RuntimeException("Profile with phone number $phoneNumber already exists")
