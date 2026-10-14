package com.lurkbd.agent.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lurkbd.agent.MainActivity
import com.lurkbd.agent.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AlarmScheduler.ACTION_REMINDER &&
            intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            // Still allow explicit reminder broadcasts without action match from older pending intents
            if (intent.getStringExtra(EXTRA_TITLE).isNullOrBlank()) return
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            // BootReceiver handles full restore; ignore here if duplicated
            return
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "LurkBD Agent"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "রিমাইন্ডার"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "lurkbd_reminders"
        manager.createNotificationChannel(
            NotificationChannel(channelId, "রিমাইন্ডার", NotificationManager.IMPORTANCE_HIGH)
        )
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val id = intent.getIntExtra(EXTRA_REQUEST_CODE, (System.currentTimeMillis() % Int.MAX_VALUE).toInt())
        manager.notify(id, notification)

        // Reschedule next occurrence for repeating alarms
        AlarmScheduler(context.applicationContext).rescheduleRepeating(intent)
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_BODY = "body"
        const val EXTRA_TYPE = "type"
        const val EXTRA_ENTITY_ID = "entity_id"
        const val EXTRA_MINUTES = "minutes"
        const val EXTRA_REQUEST_CODE = "request_code"
        const val EXTRA_DAY_OF_WEEK = "day_of_week"
        const val EXTRA_LABEL = "label"
    }
}
