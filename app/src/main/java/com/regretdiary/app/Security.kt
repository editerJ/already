package com.regretdiary.app

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

fun deviceSecurityAvailable(context: Context): Boolean {
    return BiometricManager.from(context)
        .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
}

fun showAppUnlockPrompt(
    activity: FragmentActivity,
    language: AppLanguage,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val executor: Executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                onFailure()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(loc(language, "마음정리 일기 잠금 해제", "Unlock Mind Journal"))
        .setSubtitle(loc(language, "기록을 열기 전에 본인 확인을 해주세요.", "Please verify before opening your journal."))
        .setNegativeButtonText(loc(language, "취소", "Cancel"))
        .build()

    prompt.authenticate(promptInfo)
}
