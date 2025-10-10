import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fleet_app_mobile.data.model.*
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch


private fun iso(date: String): String =
    if (date.contains("T")) date else "${date.trim()}T00:00:00Z"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicleId: Int,
    onBack: () -> Unit,
    repo: FleetRepository = FleetRepository()
) {
    var vehicle by remember { mutableStateOf<Vehicle?>(null) }
    var tab by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun reload() {
        scope.launch {
            runCatching { repo.getVehicle(vehicleId) }
                .onSuccess { vehicle = it }
                .onFailure { error = it.message }
        }
    }

    LaunchedEffect(Unit) { reload() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle?.let { "${it.make} ${it.model}" } ?: "Загрузка...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (error != null) {
                Text(
                    "Ошибка: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            vehicle?.let { v ->
                Card(
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("VIN: ${v.vin}")
                        Text("Год: ${v.year}")
                        Text("Пробег: ${v.odometerKm} км")
                        Text("Состояние: ${v.condition}")
                    }
                }

                TabRow(selectedTabIndex = tab) {
                    Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("ТО") })
                    Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Топливо") })
                    Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Документы") })
                }

                when (tab) {
                    0 -> MaintenanceTab(vehicleId, repo, onChanged = { reload() })
                    1 -> FuelTab(vehicleId, repo, onChanged = { reload() })
                    2 -> DocumentsTab(vehicleId, repo, onChanged = { reload() })
                }
            } ?: LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
