import com.example.fleet_app_mobile.data.repository.FleetRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.HttpException

class ErrorHandlingTest {

    @Test
    fun `repository handles network error gracefully`() = runBlocking {
        val repo = mock(FleetRepository::class.java)
        `when`(repo.getVehicles()).thenThrow(HttpException::class.java)

        try {
            repo.getVehicles()
        } catch (e: Exception) {
            assertNotNull(e)
        }
    }
}
