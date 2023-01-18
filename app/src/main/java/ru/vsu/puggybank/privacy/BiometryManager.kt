package ru.vsu.puggybank.privacy

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import ru.vsu.puggybank.R
import ru.vsu.puggybank.fragments.LockScreenFragment

class BiometryManager(private val cb: () -> Unit, private val fragment: LockScreenFragment) {
    fun activateBiometry() {
        val ctx = fragment.requireActivity().applicationContext
        val executor = ContextCompat.getMainExecutor(ctx)
        val biometricPrompt = BiometricPrompt(fragment, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        cb()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(ctx, R.string.unableToAuth,
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        val context = fragment.requireContext()

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.resources.getString(R.string.fingerprintLogin))
            .setNegativeButtonText(context.resources.getString(R.string.usePIN))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

}