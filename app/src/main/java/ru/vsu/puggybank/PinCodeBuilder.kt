package ru.vsu.puggybank

class PinCodeBuilder(private val pinLength: Int) {
    private var currentSymbolIndex = 0

    public fun addNextSymbol(onSuccess: (index: Int) -> Unit): Unit {
        if (currentSymbolIndex >= pinLength)
            return;

        onSuccess(currentSymbolIndex++)
    }

    public fun removeSymbol(onSuccess: (index: Int) -> Unit): Unit {
        if (currentSymbolIndex <= 0)
            return;

        onSuccess(--currentSymbolIndex);
    }

    public fun builtPin(onSuccess: () -> Unit) {
        if (currentSymbolIndex != pinLength)
            return

        onSuccess()
    }

    public fun dropPin() {
        currentSymbolIndex = 0
    }
}