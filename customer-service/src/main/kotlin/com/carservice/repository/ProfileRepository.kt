package com.carservice.repository

import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

interface ProfileRepositoryExt {
    fun findAllCustomersByFilters(
        pageable: Pageable,
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        carVin: String?
    ): Page<Customer>

    fun findAllEmployeesByFilters(
        pageable: Pageable,
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        role: String?
    ): Page<Employee>
}

interface ProfileRepository : MongoRepository<Profile, String>, ProfileRepositoryExt {
    fun findByEmail(email: String): Profile?
    fun existsByEmail(email: String): Boolean
    fun existsByPhoneNumber(phoneNumber: String): Boolean
}

@Repository
class ProfileRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : ProfileRepositoryExt {

    override fun findAllCustomersByFilters(
        pageable: Pageable,
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        carVin: String?
    ): Page<Customer> {

        val query = Query().with(pageable)
        val criteriaList = mutableListOf<Criteria>()

        criteriaList.add(Criteria.where("profileType").`is`("CUSTOMER"))

        firstName?.let { criteriaList.add(Criteria.where("firstName").regex(it, "i")) }
        lastName?.let { criteriaList.add(Criteria.where("lastName").regex(it, "i")) }
        email?.let { criteriaList.add(Criteria.where("email").regex(it, "i")) }
        phoneNumber?.let { criteriaList.add(Criteria.where("phoneNumber").regex(it, "i")) }
        carVin?.let { criteriaList.add(Criteria.where("vehiclesVin").regex(it, "i")) }

        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }

        val results = mongoTemplate.find(query, Customer::class.java)

        val countQuery = Query()
        if (criteriaList.isNotEmpty()) {
            countQuery.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }

        return PageableExecutionUtils.getPage(results, pageable) {
            mongoTemplate.count(countQuery, Customer::class.java)
        }
    }

    override fun findAllEmployeesByFilters(
        pageable: Pageable,
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        role: String?
    ): Page<Employee> {

        val query = Query().with(pageable)
        val criteriaList = mutableListOf<Criteria>()

        criteriaList.add(Criteria.where("profileType").`is`("EMPLOYEE"))

        firstName?.let { criteriaList.add(Criteria.where("firstName").regex(it, "i")) }
        lastName?.let { criteriaList.add(Criteria.where("lastName").regex(it, "i")) }
        email?.let { criteriaList.add(Criteria.where("email").regex(it, "i")) }
        phoneNumber?.let { criteriaList.add(Criteria.where("phoneNumber").regex(it, "i")) }
        role?.let { criteriaList.add(Criteria.where("role").regex(it, "i")) }

        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }

        val results = mongoTemplate.find(query, Employee::class.java)

        val countQuery = Query()
        if (criteriaList.isNotEmpty()) {
            countQuery.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }

        return PageableExecutionUtils.getPage(results, pageable) {
            mongoTemplate.count(countQuery, Employee::class.java)
        }
    }
}