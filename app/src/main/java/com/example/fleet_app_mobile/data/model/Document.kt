package com.example.fleet_app_mobile.data.model

data class Document(
    val id: Int = 0,
    val vehicleId: Int = 0,
    val name: String = "",
    val fileUrl: String = "",
    val expirationDate: String = ""
)