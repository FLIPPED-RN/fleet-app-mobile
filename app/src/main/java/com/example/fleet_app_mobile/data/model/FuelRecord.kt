package com.example.fleet_app_mobile.data.model

data class FuelRecord(
    val id: Int = 0,
    val vehicleId: Int = 0,
    val date: String = "",
    val liters: Double = 0.0,
    val price: Double = 0.0,
    val odometer: Int = 0
)