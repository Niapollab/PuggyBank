package ru.vsu.puggybank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.vsu.puggybank.PinCodeBuilder
import ru.vsu.puggybank.R
import ru.vsu.puggybank.databinding.FragmentLockScreenBinding
import ru.vsu.puggybank.privacy.BiometryManager
import ru.vsu.puggybank.privacy.MockPinCodeValidator
import ru.vsu.puggybank.privacy.PinCodeValidator

class LockScreenFragment : Fragment() {
    private var _binding: FragmentLockScreenBinding? = null
    private var _pinSymbols: Array<TextView>? = null
    private val binding get() = _binding!!
    private val pinSymbols: Array<TextView> get() = _pinSymbols!!
    private val pinCodeBuilder = PinCodeBuilder(4)
    private val pinValidator: PinCodeValidator = MockPinCodeValidator("1234")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockScreenBinding.inflate(inflater, container, false)

        _pinSymbols = arrayOf(binding.pinText0,
            binding.pinText1,
            binding.pinText2,
            binding.pinText3)

        val buttons = arrayOf(binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9)

        for (button in buttons) {
            button.setOnClickListener(::onPinCodeButtonClick)
        }
        binding.deleteButton.setOnClickListener(::onDeleteSymbol)

        val biometryManager = BiometryManager(::onUnlock, this)
        biometryManager.activateBiometry()

        return binding.root
    }

    private fun onPinCodeButtonClick(view: View?) {
        pinCodeBuilder.addNextSymbol {
            pinSymbols[it].text = (view as Button).text
        }

        pinCodeBuilder.builtPin(::handlePin)
    }

    private fun handlePin() {
        val pin = pinSymbols.asSequence()
            .map { v -> v.text }
            .joinToString("")

        if (pinValidator.isValid(pin))
        {
            onUnlock()
            return
        }

        handeIncorrectPin()
    }

    private fun onUnlock() {
        findNavController().navigate(R.id.action_lockScreenFragment_to_loginScreenFragment)
    }

    private fun handeIncorrectPin() {
        Toast.makeText(activity, "Incorrect PIN", Toast.LENGTH_SHORT).show()

        pinCodeBuilder.dropPin()
        for (symbol in pinSymbols)
            symbol.text = ""
    }

    private fun onDeleteSymbol(@Suppress("UNUSED_PARAMETER") view: View) {
        pinCodeBuilder.removeSymbol {
            pinSymbols[it].text = ""
        }
    }
}