package ru.vsu.puggybank.transactions.banking.gazprom

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

const val PROFILE_URL = "https://$DOMAIN/api/profile"

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

        return response.body<String>().contains("clientName")
    }
}