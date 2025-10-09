package com.example.fleet_app_mobile.data.model

data class Maintenance(
    val id: Int = 0,
    val vehicleId: Int = 0,
    val date: String = "",
    val type: String = "",
    val cost: Double = 0.0,
    val notes: String = ""
)