package com.example.fleet_app_mobile.data.repository

import com.example.fleet_app_mobile.data.model.*
import com.example.fleet_app_mobile.data.network.RetrofitInstance

class FleetRepository {
    private val api = RetrofitInstance.api

    // === Vehicles ===
    suspend fun getVehicles(): List<Vehicle> = api.getVehicles()
    suspend fun getVehicle(id: Int): Vehicle = api.getVehicle(id)
    suspend fun addVehicle(v: Vehicle) = api.addVehicle(v)
    suspend fun deleteVehicle(id: Int) = api.deleteVehicle(id)


    // === Maintenance ===
    suspend fun getMaintenances(vehicleId: Int): List<Maintenance> = api.getMaintenances(vehicleId)
    suspend fun addMaintenance(m: Maintenance) = api.addMaintenance(m)
    suspend fun deleteMaintenance(id: Int) = api.deleteMaintenance(id)

    // === Fuel ===
    suspend fun getFuelRecords(vehicleId: Int): List<FuelRecord> = api.getFuel(vehicleId)
    suspend fun addFuel(fr: FuelRecord) = api.addFuel(fr)
    suspend fun deleteFuel(id: Int) = api.deleteFuel(id)

    // === Documents ===
    suspend fun getDocuments(vehicleId: Int): List<Document> = api.getDocuments(vehicleId)
    suspend fun addDocument(d: Document) = api.addDocument(d)
    suspend fun deleteDocument(id: Int) = api.deleteDocument(id)
}
