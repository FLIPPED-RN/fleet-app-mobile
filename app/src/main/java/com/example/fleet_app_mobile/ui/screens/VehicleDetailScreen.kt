import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MaintenanceTab(vehicleId: Int, repo: FleetRepository, onChanged: () -> Unit) {
    var list by remember { mutableStateOf<List<Maintenance>>(emptyList()) }
    var date by remember { mutableStateOf("2025-10-09") }
    var type by remember { mutableStateOf("Замена масла") }
    var cost by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehicleId) { list = repo.getMaintenances(vehicleId) }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f)) {
            items(list, key = { it.id }) { m ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { value ->
                        if (value == DismissValue.DismissedToStart) {
                            scope.launch {
                                repo.deleteMaintenance(m.id)
                                list = repo.getMaintenances(vehicleId)
                                onChanged()
                            }
                        }
                        true
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text("Удалить", color = Color.White, modifier = Modifier.padding(16.dp))
                        }
                    },
                    dismissContent = {
                        ListItem(
                            headlineContent = { Text("${m.date.take(10)} • ${m.type}") },
                            supportingContent = { Text("Затраты: ${m.cost} ₽  ${m.notes}") }
                        )
                    }
                )
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Text("Добавить ТО", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(12.dp))
        Column(Modifier.padding(horizontal = 12.dp)) {
            OutlinedTextField(date, { date = it }, label = { Text("Дата (YYYY-MM-DD)")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(type, { type = it }, label = { Text("Тип") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                cost,
                { cost = it },
                label = { Text("Затраты") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(notes, { notes = it }, label = { Text("Примечание") })
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                scope.launch {
                    try {
                        repo.addMaintenance(
                            Maintenance(
                                id = 0,
                                vehicleId = vehicleId,
                                date = iso(date),
                                type = type,
                                cost = cost.toDoubleOrNull() ?: 0.0,
                                notes = notes
                            )
                        )
                        val updated = repo.getMaintenances(vehicleId)
                        list = updated
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }) {
                Text("Сохранить ТО")
            }

        }
        Spacer(Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FuelTab(vehicleId: Int, repo: FleetRepository, onChanged: () -> Unit) {
    var list by remember { mutableStateOf<List<FuelRecord>>(emptyList()) }
    var date by remember { mutableStateOf("2025-10-09") }
    var liters by remember { mutableStateOf("0") }
    var price by remember { mutableStateOf("0") }
    var odometer by remember { mutableStateOf("0") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehicleId) { list = repo.getFuelRecords(vehicleId) }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f)) {
            items(list, key = { it.id }) { f ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { value ->
                        if (value == DismissValue.DismissedToStart) {
                            scope.launch {
                                repo.deleteFuel(f.id)
                                list = repo.getFuelRecords(vehicleId)
                                onChanged()
                            }
                        }
                        true
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        Box(
                            Modifier.fillMaxSize().background(Color.Red),
                            contentAlignment = Alignment.CenterEnd
                        ) { Text("Удалить", color = Color.White, modifier = Modifier.padding(16.dp)) }
                    },
                    dismissContent = {
                        ListItem(
                            headlineContent = { Text("${f.date.take(10)}: ${f.liters} л × ${f.price} ₽/л") },
                            supportingContent = { Text("Одометр: ${f.odometer} км") }
                        )
                    }
                )
            }
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Text("Добавить заправку", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(12.dp))
        Column(Modifier.padding(horizontal = 12.dp)) {
            OutlinedTextField(date, { date = it }, label = { Text("Дата (YYYY-MM-DD)") })
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                liters, { liters = it },
                label = { Text("Литры") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                price, { price = it },
                label = { Text("Цена/л") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                odometer, { odometer = it },
                label = { Text("Одометр, км") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                scope.launch {
                    repo.addFuel(
                        FuelRecord(
                            id = 0,
                            vehicleId = vehicleId,
                            date = iso(date),
                            liters = liters.toDoubleOrNull() ?: 0.0,
                            price = price.toDoubleOrNull() ?: 0.0,
                            odometer = odometer.toIntOrNull() ?: 0
                        )
                    )
                    list = repo.getFuelRecords(vehicleId)
                    onChanged()
                }
            }) { Text("Сохранить заправку") }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DocumentsTab(vehicleId: Int, repo: FleetRepository, onChanged: () -> Unit) {
    var list by remember { mutableStateOf<List<Document>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehicleId) { list = repo.getDocuments(vehicleId) }

    if (list.isEmpty()) {
        Text("Документов нет", modifier = Modifier.padding(16.dp))
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        items(list, key = { it.id }) { d ->
            val dismissState = rememberDismissState(
                confirmStateChange = { value ->
                    if (value == DismissValue.DismissedToStart) {
                        scope.launch {
                            repo.deleteDocument(d.id)
                            list = repo.getDocuments(vehicleId)
                            onChanged()
                        }
                    }
                    true
                }
            )
            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    Box(
                        Modifier.fillMaxSize().background(Color.Red),
                        contentAlignment = Alignment.CenterEnd
                    ) { Text("Удалить", color = Color.White, modifier = Modifier.padding(16.dp)) }
                },
                dismissContent = {
                    ListItem(
                        headlineContent = { Text(d.name) },
                        supportingContent = { Text("Действует до: ${d.expirationDate.take(10)}") }
                    )
                }
            )
        }
    }
}
