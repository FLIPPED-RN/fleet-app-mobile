package com.example.fleet_app_mobile

import VehicleDetailScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.fleet_app_mobile.data.repository.FleetRepository
import com.example.fleet_app_mobile.ui.screens.AddVehicleScreen

import com.example.fleet_app_mobile.ui.screens.ReportsScreen
import com.example.fleet_app_mobile.ui.screens.VehicleListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()
                var currentBottom by remember { mutableStateOf("vehicles") }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentBottom == "vehicles",
                                onClick = {
                                    currentBottom = "vehicles"
                                    nav.navigate("vehicles") {
                                        popUpTo(nav.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.DirectionsCar, null) },
                                label = { Text("Автопарк") }
                            )
                            NavigationBarItem(
                                selected = currentBottom == "reports",
                                onClick = {
                                    currentBottom = "reports"
                                    nav.navigate("reports") {
                                        popUpTo(nav.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Assessment, null) },
                                label = { Text("Отчёты") }
                            )
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = nav,
                        startDestination = "vehicles",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("vehicles") {
                            VehicleListScreen(
                                onOpenVehicle = { id -> nav.navigate("vehicle/$id") },
                                onAddVehicle = { nav.navigate("add_vehicle") }
                            )
                        }
                        composable("vehicle/{id}") { backStack ->
                            val id = backStack.arguments?.getString("id")?.toIntOrNull() ?: 0
                            VehicleDetailScreen(
                                vehicleId = id,
                                onBack = { nav.popBackStack() }
                            )
                        }
                        composable("add_vehicle") {
                            AddVehicleScreen(
                                onBack = { nav.popBackStack() },
                                repository = FleetRepository()
                            )
                        }
                        composable("reports") { ReportsScreen() }
                    }
                }
            }
        }
    }
}
