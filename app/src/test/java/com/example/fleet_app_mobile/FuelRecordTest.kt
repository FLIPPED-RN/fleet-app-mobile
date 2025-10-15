import com.example.fleet_app_mobile.data.model.FuelRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FuelRecordTest {

    @Test
    fun `calculate total fuel cost`() {
        val records = listOf(
            FuelRecord(1, 1, LocalDate.now(), 40.0, 58.0, 120000),
            FuelRecord(2, 1, LocalDate.now(), 50.0, 60.0, 120500)
        )

        val totalCost = records.sumOf { it.liters * it.price }

        assertEquals(40.0 * 58.0 + 50.0 * 60.0, totalCost, 0.001)
    }
}
