package com.carservice.mapper

interface EntityMapper<DTO, ENTITY> {
    fun mapEntity(entity: ENTITY): DTO
    val entityClass: Class<ENTITY>
}