package ru.vsu.puggybank.dto.gazprom
import android.util.Log
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
    val year = getSubstring(t, 0, 4).toInt()
    val month = getSubstring(t, 4, 6).toInt()
    val day = getSubstring(t, 6, 8).toInt()
    return LocalDate.of(year, month, day)
}

fun formatTimestamp(t: String): String {
    val year = getSubstring(t, 0, 4)
    val month = formatDateNumber(getSubstring(t, 4, 6))
    val day = formatDateNumber(getSubstring(t, 6, 8))
    val hour = getSubstring(t, 8, 10)
    val minute = getSubstring(t, 10, 12)
    return "${hour}:${minute} ${day}.${month}.${year}"
}

fun formatDateNumber(d: String): String {
    if (d.length == 1) {
        return "0${d}"
    }
    return d
}

private fun getSubstring(s: String, from: Int, to: Int): String {
    return s.subSequence(from, to).toString()
}