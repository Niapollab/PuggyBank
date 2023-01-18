package ru.vsu.puggybank.transactions.banking.gazprom

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import ru.vsu.puggybank.R
import ru.vsu.puggybank.transactions.banking.AuthException
import ru.vsu.puggybank.transactions.banking.Credentials
import ru.vsu.puggybank.transactions.banking.DoubleFactorAuthRequiredException

const val DOMAIN = "online.gpb.ru"
const val LOGIN_URL = "https://$DOMAIN/login"
const val LOGIN_INIT_URL = "https://$DOMAIN/api/profile/login/init"
const val CONFIRM_LOGIN_URL = "https://$DOMAIN/api/profile/login/confirm"

class GazpromAuthProvider : Closeable {
    private val client = HttpClient(CIO) {
        install(HttpCookies)
        BrowserUserAgent()
    }
    private var csrf: String? = null

    suspend fun auth(credentials: Credentials): GazpromSession {
        csrf = csrf ?: getCsrfTokenAsync()

        val body = getLoginInitResponse(credentials)
        validateLoginInitResponse(body)

        return buildSession()
    }

    suspend fun auth(credentials: Credentials, doubleFactoryCode: String): GazpromSession {
        csrf = csrf ?: getCsrfTokenAsync()

        val body = getLoginInitResponse(credentials, doubleFactoryCode)
        validateLoginInitResponse(body)

        return buildSession()
    }

    private suspend fun getLoginInitResponse(credentials: Credentials, code: String? = null): String {
        val loginUrl = if (code == null) LOGIN_INIT_URL else CONFIRM_LOGIN_URL
        val response: HttpResponse = client.post(loginUrl) {
            headers {
                append("X-CSRF-TOKEN", csrf!!)
            }
            setBody(FormDataContent(Parameters.build {
                append("name", credentials.login)
                append("pwd", credentials.password)
                append("locale", "ru_RU")
                append("lang", "ru-RU")
                if (code != null) {
                    append("code", code)
                }
            }))
        }

        return response.body()
    }

    private fun validateLoginInitResponse(loginInitResponseBody: String) {
        if (loginInitResponseBody.contains("clientIdHash")) {
            return
        } else if (loginInitResponseBody.contains("needConfirmOtp")) {
            throw DoubleFactorAuthRequiredException(R.string.requires2faAuthentication)
        } else {
            throw AuthException(R.string.noClientId)
        }
    }

    private suspend fun getCsrfTokenAsync(): String {
        val response: HttpResponse = client.get(LOGIN_URL)
        val body = response.body<String>()

        val metaTag = Regex("<meta.*?_csrf.*?>").find(body)?.value ?: throw AuthException(R.string.noMetaTagCsrf)

        return Regex("content=\\\"(.*?)\\\"").find(metaTag)?.groups?.get(1)?.value
            ?: throw AuthException(R.string.noCsrfInMetaTag)
    }

    private suspend fun buildSession(): GazpromSession {
        val cookies = client.cookies("https://$DOMAIN/").associateBy { it.name }
        return GazpromSession(csrf!!, cookies["HSESSIONID"]!!.value, cookies["session-cookie"]!!.value, cookies["WEBSESSIONID"]!!.value)
    }

    override fun close() {
        client.close()
    }
}