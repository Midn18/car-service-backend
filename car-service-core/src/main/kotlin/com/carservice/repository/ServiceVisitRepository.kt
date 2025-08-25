package com.carservice.repository

import com.carservice.model.appointment.ServiceVisit
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceVisitRepository : MongoRepository<ServiceVisit, String> {
}