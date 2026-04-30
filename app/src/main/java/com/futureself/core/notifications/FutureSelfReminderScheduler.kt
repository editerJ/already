package com.futureself.core.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.futureself.core.model.NotificationChannels
import com.already.app.R
import java.time.LocalDateTime
import java.time.ZoneId

class FutureSelfReminderScheduler(
    private val context: Context
) {
    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                NotificationChannels.DREAM_STEP,
                context.getString(R.string.future_self_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.future_self_notification_channel_description)
            }
        )
    }

    fun scheduleDailyDreamReminder() {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerAt = nextReminderTimeMillis()
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            AlarmManager.INTERVAL_DAY,
            reminderPendingIntent()
        )
    }

    fun cancelDailyDreamReminder() {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(reminderPendingIntent())
    }

    private fun reminderPendingIntent(): PendingIntent {
        val intent = Intent(context, FutureSelfReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            DREAM_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextReminderTimeMillis(): Long {
        val now = LocalDateTime.now()
        val scheduled = now
            .withHour(20)
            .withMinute(30)
            .withSecond(0)
            .withNano(0)
            .let { candidate ->
                if (candidate.isAfter(now)) candidate else candidate.plusDays(1)
            }
        return scheduled.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private companion object {
        const val DREAM_REMINDER_REQUEST_CODE = 4401
    }
}
