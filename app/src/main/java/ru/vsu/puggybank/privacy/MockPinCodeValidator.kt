package ru.vsu.puggybank.privacy

class MockPinCodeValidator(private val correctPin: String) : PinCodeValidator {
    override fun isValid(pin: String): Boolean {
        return pin == correctPin
    }
}