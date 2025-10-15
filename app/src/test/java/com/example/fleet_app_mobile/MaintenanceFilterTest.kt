import com.example.fleet_app_mobile.data.model.Maintenance
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MaintenanceFilterTest {

    @Test
    fun `filter maintenances by year`() {
        val list = listOf(
            Maintenance(1, 1, LocalDate.parse("2024-06-01"), "Замена масла", 3000, "OK"),
            Maintenance(2, 1, LocalDate.parse("2025-04-10"), "ТО", 7000, "Проверка")
        )

        val filtered = list.filter { it.date.year == 2025 }

        assertEquals(1, filtered.size)
        assertEquals("ТО", filtered.first().type)
    }
}
