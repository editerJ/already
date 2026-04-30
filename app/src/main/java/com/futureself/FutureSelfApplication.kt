package com.futureself

import android.app.Application
import android.content.Context
import com.futureself.core.notifications.FutureSelfReminderScheduler
import com.google.android.gms.ads.MobileAds

class FutureSelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        MobileAds.initialize(this)
        FutureSelfReminderScheduler(applicationContext).createChannels()
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}