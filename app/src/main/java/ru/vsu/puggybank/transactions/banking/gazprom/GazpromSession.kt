package ru.vsu.puggybank.transactions.banking.gazprom

data class GazpromSession(val csrf: String, val hSessionId: String, val sessionCookie: String, val webSession: String)