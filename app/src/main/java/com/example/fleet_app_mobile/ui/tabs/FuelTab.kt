import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fleet_app_mobile.data.model.FuelRecord
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch

private fun iso(date: String): String =
    if (date.contains("T")) date else "${date.trim()}T00:00:00Z"

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FuelTab(vehicleId: Int, repo: FleetRepository, onChanged: () -> Unit) {
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