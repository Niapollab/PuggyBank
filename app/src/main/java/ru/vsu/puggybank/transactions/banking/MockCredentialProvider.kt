package ru.vsu.puggybank.transactions.banking

class MockCredentialProvider(private val login: String, private val password: String) :
    CredentialProvider {
    override fun getCredentials(): Credentials {
        return Credentials(login, password)
    }
}