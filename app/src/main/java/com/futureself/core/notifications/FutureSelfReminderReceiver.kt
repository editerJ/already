package com.futureself.core.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.futureself.FutureSelfMainActivity
import com.futureself.core.model.NotificationChannels
import com.futureself.data.preferences.FutureSelfRewardStore
import com.already.app.R

class FutureSelfReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val rewardState = FutureSelfRewardStore(context.applicationContext).load()
        if (rewardState.coachingRewardEndsAt <= System.currentTimeMillis()) return

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openIntent = Intent(context, FutureSelfMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_QUICK_WIN, true)
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            9921,
            openIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.DREAM_STEP)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.future_self_notification_title))
            .setContentText(
                rewardState.quickWins.firstOrNull()?.let {
                    context.getString(R.string.future_self_notification_body_with_win, it.text)
                } ?: context.getString(R.string.future_self_notification_body)
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        rewardState.quickWins.firstOrNull()?.let {
                            context.getString(R.string.future_self_notification_body_with_win, it.text)
                        } ?: context.getString(R.string.future_self_notification_body)
                    )
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                0,
                context.getString(R.string.future_self_notification_action),
                pendingIntent
            )
            .build()

        NotificationManagerCompat.from(context).notify(DREAM_STEP_NOTIFICATION_ID, notification)
    }

    companion object {
        const val EXTRA_OPEN_QUICK_WIN = "future_self_open_quick_win"
        private const val DREAM_STEP_NOTIFICATION_ID = 2204
    }
}
