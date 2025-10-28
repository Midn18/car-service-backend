package com.carservice.exceptions

class ProfileNotFoundException(
    profileId: String
) : RuntimeException("Profile with ID $profileId not found")
