package com.example.fleet_app_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fleet_app_mobile.data.model.Vehicle
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch

@Composable
fun AddVehicleScreen(
    onBack: () -> Unit,
    repository: FleetRepository = FleetRepository(),
    onSave: () -> Unit
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Добавить автомобиль", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = make, onValueChange = { make = it }, label = { Text("Марка") })
        OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Модель") })
        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Год выпуска") })
        OutlinedTextField(value = vin, onValueChange = { vin = it }, label = { Text("VIN") })
        OutlinedTextField(value = odometer, onValueChange = { odometer = it }, label = { Text("Пробег (км)") })
        OutlinedTextField(value = condition, onValueChange = { condition = it }, label = { Text("Состояние") })

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    repository.addVehicle(
                        Vehicle(
                            id = 0,
                            make = make,
                            model = model,
                            year = year.toIntOrNull() ?: 0,
                            vin = vin,
                            odometerKm = odometer.toIntOrNull() ?: 0,
                            condition = condition,
                            maintenances = emptyList(),
                            fuelRecords = emptyList(),
                            documents = emptyList()
                        )
                    )
                    onBack()
                } catch (e: Exception) {
                    error = e.message
                }
            }
        }) {
            Text("Сохранить")
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text("Ошибка: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

