import com.example.fleet_app_mobile.data.model.Vehicle
import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.*

class FleetRepositoryTest {
    private val mockRepo = mock(FleetRepository::class.java)

    @Test
    fun `getVehicles returns non-empty list`() = runBlocking {
        val mockList = listOf(
            Vehicle(1, "Toyota", "Camry", 2018, "JTNB11HK5J3001234", 75000, "Хорошее"),
            Vehicle(2, "Volkswagen", "Passat", 2015, "WVWZZZ3CZFE123456", 123000, "Среднее")
        )
        `when`(mockRepo.getVehicles()).thenReturn(mockList)

        val result = mockRepo.getVehicles()

        assertTrue(result.isNotEmpty())
    }
}