package com.carservice.repository

import com.carservice.model.profile.Address
import com.carservice.model.profile.Profile
import com.carservice.model.profile.ProfileType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : MongoRepository<Profile, String> {
    fun findByEmail(email: String): Profile?
    fun findAllByProfileType(profileType: String): List<Profile>
    fun findByPhoneNumber(phoneNumber: String): Profile?
    fun findByFirstName(firstName: String): List<Profile>
    fun findByLastName(lastName: String): List<Profile>
    fun findByAddress(address: Address): List<Profile>
}