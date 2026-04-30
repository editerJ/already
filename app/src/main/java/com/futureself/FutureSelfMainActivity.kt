package com.futureself

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.futureself.core.i18n.FutureSelfLanguages
import com.futureself.core.notifications.FutureSelfReminderReceiver
import com.futureself.core.notifications.FutureSelfReminderScheduler
import com.futureself.data.preferences.FutureSelfRewardStore
import com.futureself.ui.FutureSelfApp
import com.futureself.ui.theme.FutureSelfTheme

/**
 * Future Self 전용 엔트리 Activity.
 */
class FutureSelfMainActivity : ComponentActivity() {
    private var launchQuickWin by mutableStateOf(false)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        val rewardStore = FutureSelfRewardStore(applicationContext)
        FutureSelfLanguages.apply(languageTag = rewardStore.load().preferredLanguageTag)
        FutureSelfReminderScheduler(applicationContext).createChannels()
        requestNotificationPermissionIfNeeded()
        launchQuickWin = intent?.getBooleanExtra(FutureSelfReminderReceiver.EXTRA_OPEN_QUICK_WIN, false) == true

        setContent {
            FutureSelfTheme {
                FutureSelfApp(
                    launchQuickWin = launchQuickWin,
                    onLaunchQuickWinConsumed = { launchQuickWin = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        launchQuickWin = intent.getBooleanExtra(FutureSelfReminderReceiver.EXTRA_OPEN_QUICK_WIN, false)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
