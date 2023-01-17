package ru.vsu.puggybank.gazprom

@kotlinx.serialization.Serializable
data class Card (
    val additional: Int,
    val type: Int,
    val branch: String,
    val payInOperation: Boolean,
    val expDate: String,
    val number: String,
    val balance: Double,
    val currency: String,
    val id: String,
    val extId: String,
    val brand: String?,
    val scid: String,
    val payOutOperation: Boolean,
    val pinNotSet: Int,
    val isDigital: Boolean,
    val applePaySupport: Boolean,
    val kind: String,
    val active: Int,
    val cardHolder: String,
    val isVisible: Int,
    val creditOverdraftApplsActive: Boolean,
    val payOutPrivate: Boolean,
    val name: String,
    val linkedAccounts: Array<String>,
    val category: String,
    val status: Int
)