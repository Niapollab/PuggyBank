package ru.vsu.puggybank.transactions.banking.interfaces

import java.time.LocalDate

interface TransactionsProvider {
    fun getTransactionsJSONString(from: LocalDate, to: LocalDate): String { return "{}" }
    fun getTransactionsJSONString(): String { return "{}" }
    suspend fun isValid(): Boolean
}