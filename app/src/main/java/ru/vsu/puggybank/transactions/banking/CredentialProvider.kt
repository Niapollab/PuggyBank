package ru.vsu.puggybank.transactions.banking

interface CredentialProvider {
    fun getCredentials(): Credentials
}

