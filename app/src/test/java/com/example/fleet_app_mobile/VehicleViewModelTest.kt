import com.example.fleet_app_mobile.data.model.Vehicle
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleViewModelTest {

    @Test
    fun `addVehicle calls repository correctly`() = runTest {
        val repo = mock(FleetRepository::class.java)
        val vehicle = Vehicle(0, "Kia", "Rio", 2020, "Z1234VIN", 23000, "Отличное")

        repo.addVehicle(vehicle)

        verify(repo).addVehicle(vehicle)
    }
}
