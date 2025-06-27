package com.carservice.mapper

interface DtoMapper<DTO, ENTITY> {
    fun mapDto(dto: DTO): ENTITY
}