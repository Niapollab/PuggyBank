package ru.vsu.puggybank.transactions.banking

import android.content.SharedPreferences

class SharedPreferencesCredentialManager(private val sharedPreferences: SharedPreferences, private val prefix: String = "") :
    CredentialProvider {
    override var credentials: Credentials
        get() {
            val login = sharedPreferences.getString("${prefix}_login", "")!!
            val password = sharedPreferences.getString("${prefix}_password", "")!!

            return Credentials(login, password)
        }
        set(value) {
            val editor = sharedPreferences.edit()

            editor.putString("${prefix}_login", value.login)
            editor.putString("${prefix}_password", value.password)

            editor.apply()
        }
}