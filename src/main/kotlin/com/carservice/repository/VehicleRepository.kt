package com.carservice.repository

import com.carservice.model.Vehicle
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository : MongoRepository<Vehicle, String>