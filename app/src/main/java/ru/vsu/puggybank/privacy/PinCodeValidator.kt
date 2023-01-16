package ru.vsu.puggybank.privacy

interface PinCodeValidator {
    fun isValid(pin: String): Boolean;
}