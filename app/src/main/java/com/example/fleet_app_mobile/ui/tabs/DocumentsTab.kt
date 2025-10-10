import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.unit.dp
import com.example.fleet_app_mobile.data.model.Document
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DocumentsTab(vehicleId: Int, repo: FleetRepository, onChanged: () -> Unit) {
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