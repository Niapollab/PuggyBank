package ru.vsu.puggybank.transactions.banking.gazprom

import android.content.SharedPreferences

class GazpromSharedPreferencesSessionManager(private val sharedPreferences: SharedPreferences) {
    var session: GazpromSession
        get() {
            val csrf = sharedPreferences.getString("gazprom_csrf", "")!!
            val hSessionId = sharedPreferences.getString("gazprom_hSessionId", "")!!
            val sessionCookie = sharedPreferences.getString("gazprom_sessionCookie", "")!!
            val webSession = sharedPreferences.getString("gazprom_webSession", "")!!

            return GazpromSession(csrf, hSessionId, sessionCookie, webSession)
        }
        set(value) {
            val editor = sharedPreferences.edit()

            editor.putString("gazprom_csrf", value.csrf)
            editor.putString("gazprom_hSessionId", value.hSessionId)
            editor.putString("gazprom_sessionCookie", value.sessionCookie)
            editor.putString("gazprom_webSession", value.webSession)

            editor.commit()
        }
}