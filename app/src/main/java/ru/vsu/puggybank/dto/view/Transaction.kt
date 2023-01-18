package ru.vsu.puggybank.dto.view

data class Transaction (
    val id: String,
    val amount: Double,
    val description: String,
    val currency: String,
    val type: String,
    val timestamp: String
)