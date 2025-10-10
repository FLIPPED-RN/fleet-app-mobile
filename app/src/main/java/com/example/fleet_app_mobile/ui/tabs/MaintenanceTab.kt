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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
//noinspection UsingMaterialAndMaterial3Libraries
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
import com.example.fleet_app_mobile.data.model.Maintenance
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch

private fun iso(date: String): String =
    if (date.contains("T")) date else "${date.trim()}T00:00:00Z"

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceTab(vehicleId: Int, repo: FleetRepository, onChanged: () -> Unit) {
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