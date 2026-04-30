package com.futureself.core.privacy

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

data class AdConsentUiState(
    val isSyncInProgress: Boolean = true,
    val canRequestAds: Boolean = false,
    val isPrivacyOptionsRequired: Boolean = false,
    val lastErrorMessage: String? = null
)

class FutureSelfConsentManager(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    fun requestConsent(
        activity: Activity,
        onResult: (AdConsentUiState) -> Unit
    ) {
        onResult(currentState(isSyncInProgress = true))
        val parameters = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            parameters,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    onResult(
                        currentState(
                            isSyncInProgress = false,
                            lastErrorMessage = formError?.message
                        )
                    )
                }
            },
            { requestError ->
                onResult(
                    currentState(
                        isSyncInProgress = false,
                        lastErrorMessage = requestError.message
                    )
                )
            }
        )
    }

    fun showPrivacyOptions(
        activity: Activity,
        onResult: (AdConsentUiState) -> Unit
    ) {
        if (!isPrivacyOptionsRequired()) {
            onResult(currentState(isSyncInProgress = false))
            return
        }

        onResult(currentState(isSyncInProgress = true))
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            onResult(
                currentState(
                    isSyncInProgress = false,
                    lastErrorMessage = formError?.message
                )
            )
        }
    }

    private fun currentState(
        isSyncInProgress: Boolean,
        lastErrorMessage: String? = null
    ): AdConsentUiState {
        return AdConsentUiState(
            isSyncInProgress = isSyncInProgress,
            canRequestAds = consentInformation.canRequestAds(),
            isPrivacyOptionsRequired = isPrivacyOptionsRequired(),
            lastErrorMessage = lastErrorMessage
        )
    }

    private fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
}
