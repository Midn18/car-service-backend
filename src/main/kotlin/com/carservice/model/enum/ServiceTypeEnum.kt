package com.carservice.model.enum

enum class ServiceTypeEnum(val description: String) {
    OIL_CHANGE("Oil Change"),
    TIRE_ROTATION("Tire Rotation"),
    BRAKE_SERVICE("Brake Service"),
    ENGINE_TUNE_UP("Engine Tune-Up"),
    TRANSMISSION_SERVICE("Transmission Service"),
    AIR_FILTER_REPLACEMENT("Air Filter Replacement"),
    BATTERY_REPLACEMENT("Battery Replacement"),
    WHEEL_ALIGNMENT("Wheel Alignment"),
    COOLANT_FLUSH("Coolant Flush"),
    EXHAUST_SYSTEM_REPAIR("Exhaust System Repair"),
    SUSPENSION_REPAIR("Suspension Repair"),
    ELECTRICAL_DIAGNOSTICS("Electrical Diagnostics"),
    DETAILING("Detailing");

    override fun toString(): String {
        return description
    }
}

