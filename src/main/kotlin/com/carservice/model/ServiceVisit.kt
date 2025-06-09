package com.carservice.model

import com.carservice.model.enum.ServiceTypeEnum
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ServiceVisit(
    @Id val visitId: String,
    val vehicleVin: String = "",
    val serviceDate: String = "",
    val serviceType: ServiceTypeEnum,
    val employeeId: String = "",
    val customerId: String = "",
)
