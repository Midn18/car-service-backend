package com.carservice.model.enum

enum class VehicleTypeEnum(val type: String) {
    CAR("Car"),
    TRUCK("Truck"),
    MOTORCYCLE("Motorcycle"),
    BUS("Bus"),
    VAN("Van");

    companion object {
        fun fromString(type: String): VehicleTypeEnum? {
            return entries.find { it.type.equals(type, ignoreCase = true) }
        }
    }
}