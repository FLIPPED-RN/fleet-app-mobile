package com.example.fleet_app_mobile.data.network

import com.example.fleet_app_mobile.data.model.*
import retrofit2.http.*

interface ApiService {

    // === Vehicles ===
    @GET("Vehicles")
    suspend fun getVehicles(): List<Vehicle>

    @POST("Vehicles")
    suspend fun addVehicle(@Body body: Vehicle)

    @GET("Vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: Int): Vehicle

    @DELETE("Vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: Int)


    // === Maintenances ===
    @GET("Maintenances/Vehicle/{vehicleId}")
    suspend fun getMaintenances(@Path("vehicleId") vehicleId: Int): List<Maintenance>

    @POST("Maintenances")
    suspend fun addMaintenance(@Body body: Maintenance)

    @DELETE("Maintenances/{id}")
    suspend fun deleteMaintenance(@Path("id") id: Int)


    // === FuelRecords ===
    @GET("FuelRecords/Vehicle/{vehicleId}")
    suspend fun getFuel(@Path("vehicleId") vehicleId: Int): List<FuelRecord>

    @POST("FuelRecords")
    suspend fun addFuel(@Body body: FuelRecord)

    @DELETE("FuelRecords/{id}")
    suspend fun deleteFuel(@Path("id") id: Int)


    // === Documents ===
    @GET("Documents/Vehicle/{vehicleId}")
    suspend fun getDocuments(@Path("vehicleId") vehicleId: Int): List<Document>

    @POST("Documents")
    suspend fun addDocument(@Body body: Document)

    @DELETE("Documents/{id}")
    suspend fun deleteDocument(@Path("id") id: Int)
}
