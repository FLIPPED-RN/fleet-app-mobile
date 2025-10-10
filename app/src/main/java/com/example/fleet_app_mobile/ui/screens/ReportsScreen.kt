package com.example.fleet_app_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch

@Preview
@Composable
fun ReportsScreen(repo: FleetRepository = FleetRepository()) {
    var totalFuel by remember { mutableStateOf(0.0) }
    var totalMaint by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val vehicles = repo.getVehicles()
            var f = 0.0
            var m = 0.0
            for (v in vehicles) {
                f += repo.getFuelRecords(v.id).sumOf { it.price * it.liters }
                m += repo.getMaintenances(v.id).sumOf { it.cost }
            }
            totalFuel = f
            totalMaint = m
            isLoading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Отчёты", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        if (isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        } else {
            Text("Затраты на топливо: ${"%,.2f".format(totalFuel)} ₽")
            Text("Затраты на ТО: ${"%,.2f".format(totalMaint)} ₽")
            Divider(Modifier.padding(vertical = 8.dp))
            Text("Всего: ${"%,.2f".format(totalFuel + totalMaint)} ₽", style = MaterialTheme.typography.titleLarge)
        }
    }
}
