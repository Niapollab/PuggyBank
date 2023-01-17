package ru.vsu.puggybank

class PinCodeBuilder(private val pinLength: Int) {
    private var currentSymbolIndex = 0

    fun addNextSymbol(onSuccess: (index: Int) -> Unit): Unit {
        if (currentSymbolIndex >= pinLength)
            return;

        onSuccess(currentSymbolIndex++)
    }

    fun removeSymbol(onSuccess: (index: Int) -> Unit): Unit {
        if (currentSymbolIndex <= 0)
            return;

        onSuccess(--currentSymbolIndex);
    }

    fun builtPin(onSuccess: () -> Unit) {
        if (currentSymbolIndex != pinLength)
            return

        onSuccess()
    }

    fun dropPin() {
        currentSymbolIndex = 0
    }
}