package ru.vsu.puggybank.transactions.banking.gazprom

import android.content.SharedPreferences
import ru.vsu.puggybank.fragments.GAZPROM_PREFIX

class GazpromSharedPreferencesSessionManager(private val sharedPreferences: SharedPreferences) {
    var session: GazpromSession
        get() {
            val csrf = sharedPreferences.getString("${GAZPROM_PREFIX}_csrf", "")!!
            val hSessionId = sharedPreferences.getString("${GAZPROM_PREFIX}_hSessionId", "")!!
            val sessionCookie = sharedPreferences.getString("${GAZPROM_PREFIX}_sessionCookie", "")!!
            val webSession = sharedPreferences.getString("${GAZPROM_PREFIX}_webSession", "")!!

            return GazpromSession(csrf, hSessionId, sessionCookie, webSession)
        }
        set(value) {
            val editor = sharedPreferences.edit()

            editor.putString("${GAZPROM_PREFIX}_csrf", value.csrf)
            editor.putString("${GAZPROM_PREFIX}_hSessionId", value.hSessionId)
            editor.putString("${GAZPROM_PREFIX}_sessionCookie", value.sessionCookie)
            editor.putString("${GAZPROM_PREFIX}_webSession", value.webSession)

            editor.apply()
        }
}