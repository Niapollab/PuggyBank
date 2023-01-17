package ru.vsu.puggybank.transactions.banking.gazprom

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ru.vsu.puggybank.dto.gazprom.GazpromHistoryResponse
import ru.vsu.puggybank.dto.gazprom.mapGazpromHistoryResponseToTransactions
import ru.vsu.puggybank.dto.view.Transaction
import ru.vsu.puggybank.gazprom.GazpromCardsResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val PROFILE_URL = "https://$DOMAIN/api/profile"
const val HISTORY_URL = "https://$DOMAIN/api/card/history"
const val CARDS_URL = "https://$DOMAIN/api/card/cards"

class GazpromClient(private val session: GazpromSession) {
    private val client = HttpClient(CIO) {
        install(HttpCookies) {
            storage = ConstantCookiesStorage(
                Cookie("HSESSIONID", session.hSessionId, domain = DOMAIN),
                Cookie("session-cookie", session.sessionCookie, domain = DOMAIN),
                Cookie("WEBSESSIONID", session.webSession, domain = DOMAIN)
            )
        }
        BrowserUserAgent()
    }

    suspend fun isValid(): Boolean {
        val response: HttpResponse = client.post(PROFILE_URL) {
            headers {
                append("X-CSRF-TOKEN", session.csrf)
            }
        }


        val res = response.body<String>().contains("clientName")
        if (res) {
            val a = getTransactionsByDates(LocalDate.of(2022, 12, 1), LocalDate.of(2023, 1, 29))
            Log.d("123", a.toString())
        }
        return res
    }

    suspend fun getTransactionsByDates(from: LocalDate, to: LocalDate): List<Transaction> {
        val startDate = "${from.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}100811+03:00"
        val endDate = "${to.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}100811+03:00"

        val cardsResponse: HttpResponse = client.get(CARDS_URL) {
            headers {
                append("X-CSRF-TOKEN", session.csrf)
            }
        }

        val card = cardsResponse.body<String>()
//        val historyUri = "${HISTORY_URL}?card=${card.id}&page=1&countPage=100&startDate=${startDate}&endDate=${endDate}"
//        val historyResponse: HttpResponse = client.post(historyUri) {
//            headers {
//                append("X-CSRF-TOKEN", session.csrf)
//            }
//        }
//
//        val body = historyResponse.body<GazpromHistoryResponse>()
//        return mapGazpromHistoryResponseToTransactions(body)
        Log.d("12321", card.toString())
        return emptyList()
    }
}