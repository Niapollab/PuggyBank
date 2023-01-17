package ru.vsu.puggybank.dto.gazprom
import kotlinx.serialization.*
import ru.vsu.puggybank.dto.view.Transaction
import java.time.LocalDate

@Serializable
data class GazpromResponse(
    val result: Int,
    val hasNextData: Boolean,
    val id: String,
    val page: Int,
    val history: Array<GazpromTransaction>,
    val techInfo: TechInfo
    )

fun mapGazpromResponseToTransactions(response: GazpromResponse): List<Transaction> {
    return response.history.map {t -> Transaction(t.tid, t.amount, t.description, t.currency, t.type, t.timestamp) }
}

fun convertTimestamp(t: String): LocalDate {
    val year = getSubstring(t, 0, 4)
    val month = getSubstring(t, 4, 6)
    val day = getSubstring(t, 6, 8)
    return LocalDate.of(year, month, day)
}

private fun getSubstring(s: String, from: Int, to: Int): Int {
    return s.subSequence(from, to).toString().toInt()
}