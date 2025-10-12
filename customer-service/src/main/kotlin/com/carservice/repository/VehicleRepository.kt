package com.carservice.repository

import com.carservice.model.vehicle.Vehicle
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

interface VehicleRepositoryExt {
    fun findAllVehiclesByFilters(
        pageable: Pageable,
        registrationNumber: String? = null,
        carVin: String? = null,
        ownerId: String? = null,
        make: String? = null,
        model: String? = null
    ): Page<Vehicle>
}

interface VehicleRepository : MongoRepository<Vehicle, String>, VehicleRepositoryExt {
    fun findByRegistrationNumber(registrationNumber: String): Vehicle?
}

@Repository
class VehicleRepositoryImpl(private val mongoTemplate: MongoTemplate) : VehicleRepositoryExt {

    override fun findAllVehiclesByFilters(
        pageable: Pageable,
        registrationNumber: String?,
        carVin: String?,
        ownerId: String?,
        make: String?,
        model: String?
    ): Page<Vehicle> {
        val query = Query().with(pageable)
        val criteriaList = mutableListOf<Criteria>()

        registrationNumber?.let { criteriaList.add(Criteria.where("registrationNumber").regex(it, "i")) }
        carVin?.let { criteriaList.add(Criteria.where("vin").regex(it, "i")) }
        ownerId?.let { criteriaList.add(Criteria.where("owner.id").`is`(it)) }
        make?.let { criteriaList.add(Criteria.where("make").regex(it, "i")) }
        model?.let { criteriaList.add(Criteria.where("model").regex(it, "i")) }

        if (criteriaList.isNotEmpty()) {
            val combinedCriteria = Criteria().andOperator(*criteriaList.toTypedArray())
            query.addCriteria(combinedCriteria)
        }

        val results = mongoTemplate.find(query, Vehicle::class.java)

        return PageableExecutionUtils.getPage(results, pageable) {
            mongoTemplate.count(query.skip(-1).limit(-1), Vehicle::class.java)
        }
    }
}