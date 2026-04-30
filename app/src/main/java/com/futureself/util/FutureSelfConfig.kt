package com.futureself.util

import com.already.app.BuildConfig

/**
 * 운영 중 자주 바뀌는 값들의 중앙 진입점.
 * 상품 ID, 정책 URL, 지원 이메일이 여기저기 흩어지지 않도록 모읍니다.
 */
object FutureSelfConfig {
    const val appName = "Already"
    const val databaseName = "already.db"

    val supportEmail: String = BuildConfig.FUTURE_SELF_SUPPORT_EMAIL
    val privacyPolicyUrl: String = BuildConfig.FUTURE_SELF_PRIVACY_URL
    val termsOfServiceUrl: String = BuildConfig.FUTURE_SELF_TERMS_URL
    val rewardedAdUnitId: String = BuildConfig.FUTURE_SELF_REWARDED_AD_UNIT_ID

    const val admobAppIdResKey = "admob_app_id"

    object Billing {
        val plusMonthlyId: String = BuildConfig.FUTURE_SELF_PLUS_MONTHLY_ID
        val plusYearlyId: String = BuildConfig.FUTURE_SELF_PLUS_YEARLY_ID
        val lifetimeId: String = BuildConfig.FUTURE_SELF_LIFETIME_ID
    }
}
