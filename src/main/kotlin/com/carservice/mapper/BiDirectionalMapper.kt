package com.carservice.mapper

interface BiDirectionalMapper<DTO, ENTITY> : DtoMapper<DTO, ENTITY>, EntityMapper<DTO, ENTITY>