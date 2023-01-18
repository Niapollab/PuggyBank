package ru.vsu.puggybank.utils.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ResponseMapper {
    companion object {
        inline fun <reified T, G>mapResponse(json: String, cb: (T) -> G): G {
            val format = Json { isLenient = true }
            val transactions = format.decodeFromString<T>(json)
            return cb(transactions)
        }
    }
}