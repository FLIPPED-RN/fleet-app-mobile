package com.example.fleet_app_mobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fleet_app_mobile.data.model.Vehicle
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VehicleListScreen(
    onOpenVehicle: (Int) -> Unit,
    onAddVehicle: () -> Unit,
    repo: FleetRepository = FleetRepository()
) {
    var items by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            runCatching { repo.getVehicles() }
                .onSuccess { items = it }
                .onFailure { error = it.message }
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            "Автопарк",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        if (error != null) {
            Text(
                "Ошибка: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(items, key = { it.id }) { v ->
                val dismissState = rememberDismissState(
                    confirmStateChange  = { value ->
                        if (value == DismissValue.DismissedToStart || value == DismissValue.DismissedToEnd) {
                            scope.launch {
                                runCatching { repo.deleteVehicle(v.id) }
                                    .onSuccess {
                                        items = items.filterNot { it.id == v.id }
                                    }
                                    .onFailure {
                                        error = it.message
                                    }
                            }
                            true
                        } else false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Удалить",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    dismissContent = {
                        Card(
                            Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .fillMaxWidth()
                                .clickable { onOpenVehicle(v.id) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("${v.make} ${v.model} (${v.year})", style = MaterialTheme.typography.titleMedium)
                                Text("VIN: ${v.vin}")
                                Text("Пробег: ${v.odometerKm} км • Сост.: ${v.condition}")
                            }
                        }
                    }
                )
            }
        }

        Button(
            onClick = onAddVehicle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Добавить автомобиль")
        }
    }
}
