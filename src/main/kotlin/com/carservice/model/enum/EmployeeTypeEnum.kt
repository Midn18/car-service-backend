package com.carservice.model.enum

enum class EmployeeTypeEnum(private val type: String) {
    ADMIN("Administrator"),
    MECHANIC("Mechanic"),
    CAR_DETAILER("Car Detailer"),
    CAR_PAINTER("Car Painter"),
    ELECTRICIAN("Electrician");

    override fun toString(): String {
        return type
    }
}