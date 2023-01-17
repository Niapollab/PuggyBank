package ru.vsu.puggybank.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.runBlocking
import ru.vsu.puggybank.R
import ru.vsu.puggybank.databinding.FragmentLoginScreenBinding
import ru.vsu.puggybank.transactions.banking.AuthException
import ru.vsu.puggybank.transactions.banking.DoubleFactorAuthRequiredException
import ru.vsu.puggybank.transactions.banking.SharedPreferencesCredentialManager
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromAuthProvider

class LoginScreenFragment : Fragment() {
    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private var firstPhoneNumberFieldTouch = false
    private val authProvider = GazpromAuthProvider()

    private var _credentialManager: SharedPreferencesCredentialManager? = null
    private val credentialManager get() = _credentialManager!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        _credentialManager = SharedPreferencesCredentialManager(activity?.getSharedPreferences("credentials.xml", Context.MODE_PRIVATE)!!, "gazprombank")

        val creds = credentialManager.getCredentials()

        if (creds.login != "") {
            firstPhoneNumberFieldTouch = true
            binding.phoneNumberText.text.append(creds.login)
        }

        if (creds.password != "") {
            firstPhoneNumberFieldTouch = true
            binding.editPasswordText.text.append(creds.password)
        }

        binding.phoneNumberText.setOnFocusChangeListener(({ _, focus ->
            if (!firstPhoneNumberFieldTouch && focus) {
                firstPhoneNumberFieldTouch = true
                binding.phoneNumberText.text.append(getString(R.string.startPhoneNumber))
            }
        }))

        binding.tryLoginButton.setOnClickListener {
            val n = binding.phoneNumberText.text.toString()
            val pass = binding.editPasswordText.text.toString()

            runBlocking {
                try {
                    authProvider.auth(credentialManager.getCredentials())
                } catch (err: DoubleFactorAuthRequiredException) {
                    credentialManager.setLogin(n)
                    credentialManager.setPassword(pass)
                    showDoubleFactorCodeEnterDialog()
                } catch (err: Exception) {
                    Toast.makeText(context, "Не удалось войти", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return binding.root
    }

    private fun showDoubleFactorCodeEnterDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Введите код")

        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Принять") { _, _ ->
            runBlocking {
                val code = input.text.toString()
                try {
                    authProvider.auth(credentialManager.getCredentials(), code)
                    onLogin()
                } catch (err: AuthException) {
                    Toast.makeText(context, "Неверный код", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun onLogin() {
        findNavController().navigate(R.id.action_loginScreenFragment_to_mainScreenFragment)
    }
}