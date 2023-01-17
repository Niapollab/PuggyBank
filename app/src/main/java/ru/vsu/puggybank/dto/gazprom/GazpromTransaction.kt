package ru.vsu.puggybank.dto.gazprom
import kotlinx.serialization.*

@Serializable
data class GazpromTransaction(
    val amount: Double,
    val feeCurrency: String,
    val description: String,
    val type: String,
    val tid: String,
    val hold: Int,
    val feeAmount: Double,
    val isRepeatable: Boolean,
    val transRef: String?,
    val hasReceipt: Boolean,
    val currency: String,
    val hasTemplate: Boolean,
    val timestamp: String,
)