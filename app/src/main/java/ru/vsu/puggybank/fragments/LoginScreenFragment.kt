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
import kotlinx.coroutines.*
import ru.vsu.puggybank.R
import ru.vsu.puggybank.databinding.FragmentLoginScreenBinding
import ru.vsu.puggybank.transactions.banking.AuthException
import ru.vsu.puggybank.transactions.banking.Credentials
import ru.vsu.puggybank.transactions.banking.DoubleFactorAuthRequiredException
import ru.vsu.puggybank.transactions.banking.SharedPreferencesCredentialManager
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromAuthProvider
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromSession
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromSharedPreferencesSessionManager

class LoginScreenFragment : Fragment() {
    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    private var firstPhoneNumberFieldTouch = false
    private val authProvider = GazpromAuthProvider()

    private var _credentialManager: SharedPreferencesCredentialManager? = null
    private val credentialManager get() = _credentialManager!!

    private var _sessionManager: GazpromSharedPreferencesSessionManager? = null
    private val sessionManager get() = _sessionManager!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginScreenBinding.inflate(inflater, container, false)

        _credentialManager = SharedPreferencesCredentialManager(activity?.getSharedPreferences("credentials.xml", Context.MODE_PRIVATE)!!, "gazprombank")
        _sessionManager = GazpromSharedPreferencesSessionManager(activity?.getSharedPreferences("gazprom_session.xml", Context.MODE_PRIVATE)!!)

        if (isValidSession(sessionManager.session)) {
            onLogin()
        }

        if (credentialManager.credentials.login != "") {
            firstPhoneNumberFieldTouch = true
            binding.phoneNumberText.text.append(credentialManager.credentials.login)
        }

        if (credentialManager.credentials.password != "") {
            firstPhoneNumberFieldTouch = true
            binding.editPasswordText.text.append(credentialManager.credentials.password)
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

            GlobalScope.launch (Dispatchers.Main) {
                try {
                    coroutineScope {
                        sessionManager.session = authProvider.auth(Credentials(n, pass))
                    }
                } catch (err: DoubleFactorAuthRequiredException) {
                    credentialManager.credentials = Credentials(n, pass)
                    showDoubleFactorCodeEnterDialog()
                } catch (err: Exception) {
                    Toast.makeText(context, "Не удалось войти", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private suspend fun showDoubleFactorCodeEnterDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Введите код")

        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Принять") { _, _ ->
            val code = input.text.toString()
            GlobalScope.launch (Dispatchers.Main) {
                try {
                    coroutineScope {
                        sessionManager.session = authProvider.auth(credentialManager.credentials, code)
                        onLogin()
                    }
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

    private fun isValidSession(session: GazpromSession): Boolean {
        //TODO: Implement validation
        return false;
    }
}