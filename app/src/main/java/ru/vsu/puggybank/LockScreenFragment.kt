package ru.vsu.puggybank

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.vsu.puggybank.databinding.FragmentLockScreenBinding

class LockScreenFragment : Fragment() {
    private var currentPinSymb = 0
    private var pinSymbols: Array<TextView?> = arrayOfNulls<TextView?>(4)
    private var _binding: FragmentLockScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLockScreenBinding.inflate(inflater, container, false)
        pinSymbols[0] = binding.pinText0
        pinSymbols[1] = binding.pinText1
        pinSymbols[2] = binding.pinText2
        pinSymbols[3] = binding.pinText3

        binding.button1.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button0.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button2.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button3.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button4.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button5.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button6.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button7.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button8.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.button9.setOnClickListener {
            onPinCodeButtonClick(it)
        }
        binding.deleteButton.setOnClickListener {
            onDeleteSymbol(it)
        }

        return binding.root
    }

    private fun onPinCodeButtonClick(view: View?) {
        if (view is Button && currentPinSymb < 4) {
            val button: Button = view
            pinSymbols[currentPinSymb]?.text = button.text
            currentPinSymb++
        }

        if (currentPinSymb  == 4) {
            handlePin()
        }
    }

    private fun handlePin() {
        var pin = ""

        for (pinSymb in pinSymbols) {
            pin += pinSymb?.text
        }

        if (isPinValid(pin)) {
            Log.d("puggybank_tag","ok")
            this.login()
        } else {
            Log.d("puggybank_tag","ne ok")
        }
    }

    private fun isPinValid(pin: String): Boolean {
        return pin == "1234"
    }

    private fun login() {
        findNavController().navigate(R.id.action_lockScreenFragment_to_mainScreenFragment)
    }

    private fun onDeleteSymbol(view: View) {
        currentPinSymb--
        pinSymbols[currentPinSymb]?.text = ""
    }
}