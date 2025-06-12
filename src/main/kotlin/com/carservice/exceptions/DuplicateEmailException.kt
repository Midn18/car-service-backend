package com.carservice.exceptions

class DuplicateEmailException(email: String) : RuntimeException("Profile with email $email already exists")