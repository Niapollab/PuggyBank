package ru.vsu.puggybank.transactions.banking

import android.content.SharedPreferences

class SharedPreferencesCredentialManager(private val sharedPreferences: SharedPreferences, private val preffix: String = "") :
    CredentialProvider {
    override fun getCredentials(): Credentials {
        val login = sharedPreferences.getString("${preffix}_login", "")!!
        val password = sharedPreferences.getString("${preffix}_password", "")!!

        return Credentials(login, password)
    }

    fun setLogin(login: String) {
        val editor = sharedPreferences.edit()

        editor.putString("${preffix}_login", login)

        editor.commit()
    }

    fun setPassword(password: String) {
        val editor = sharedPreferences.edit()

        editor.putString("${preffix}_password", password)

        editor.commit()
    }
}