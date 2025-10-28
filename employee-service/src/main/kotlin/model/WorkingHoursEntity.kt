package com.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "working_hours")
class WorkingHoursEntity {
    @Id
    val id: String = ""

}