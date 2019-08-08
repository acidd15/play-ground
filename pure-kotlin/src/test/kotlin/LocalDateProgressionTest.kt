import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateIterator(
    startDate: LocalDate,
    private val endDateInclusive: LocalDate,
    private val stepDays: Long
) : Iterator<LocalDate> {
    private var currentDate = startDate

    override fun hasNext(): Boolean {
        return currentDate <= endDateInclusive
    }

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.plusDays(stepDays)
        return next
    }
}

class DateProgression(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
    private val stepDays: Long = 1
) : Iterable<LocalDate>, ClosedRange<LocalDate> {
    override fun iterator(): Iterator<LocalDate> {
        return DateIterator(start, endInclusive, stepDays)
    }

    infix fun step(days: Long) = DateProgression(start, endInclusive, days)
}

operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

class LocalDateProgressionTest {

    @Test
    fun `Should get an item per 7 steps`() {
        val startDate = LocalDate.parse("2019-08-07")
        val endDate = LocalDate.parse("2019-10-11")

        val result = (startDate..endDate step 7).toList().joinToString(separator = ",")

        assertEquals("2019-08-07,2019-08-14,2019-08-21,2019-08-28,2019-09-04," +
                "2019-09-11,2019-09-18,2019-09-25,2019-10-02,2019-10-09", result)
    }

}