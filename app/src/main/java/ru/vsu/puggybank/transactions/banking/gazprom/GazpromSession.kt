package ru.vsu.puggybank.transactions.banking.gazprom

class GazpromSession(val csrf: String, val hSessionId: String, val sessionCookie: String, val webSession: String) {
    constructor(): this("", "", "", "") {
    }
}