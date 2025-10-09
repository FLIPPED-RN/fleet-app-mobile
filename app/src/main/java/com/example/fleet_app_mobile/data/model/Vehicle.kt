package com.example.fleet_app_mobile.data.model

data class Vehicle(
    val id: Int = 0,
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val vin: String = "",
    val odometerKm: Int = 0,
    val condition: String = "",
    val maintenances: List<Maintenance> = emptyList(),
    val fuelRecords: List<FuelRecord> = emptyList(),
    val documents: List<Document> = emptyList()
)