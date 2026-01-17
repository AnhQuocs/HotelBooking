package com.example.hotelbooking.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.hotelbooking.R

fun showBiometricPrompt(
    activity: AppCompatActivity,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(R.string.biometric_title))
        .setSubtitle(activity.getString(R.string.biometric_subtitle))
        .setNegativeButtonText(activity.getString(R.string.cancel))
        .build()

    biometricPrompt.authenticate(promptInfo)
}