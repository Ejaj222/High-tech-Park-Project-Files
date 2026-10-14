package com.lurkbd.agent.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lurkbd.agent.data.db.LurkDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Restores medicine / class / prayer / Ramadan alarms after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED &&
            intent?.action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            intent?.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = LurkDatabase.get(context)
                val scheduler = AlarmScheduler(context.applicationContext)
                db.medicines().observeAll().first().forEach { scheduler.scheduleMedicine(it) }
                db.classPeriods().observeAll().first().forEach { scheduler.scheduleClass(it) }
                val prefs = context.getSharedPreferences("lurkbd_alarms", Context.MODE_PRIVATE)
                if (prefs.getBoolean("prayer_enabled", true)) {
                    listOf(
                        "ফজর" to "05:05",
                        "যোহর" to "12:10",
                        "আসর" to "15:45",
                        "মাগরিব" to "17:55",
                        "এশা" to "19:20"
                    ).forEach { (n, t) -> scheduler.schedulePrayer(n, t, true) }
                }
                if (prefs.getBoolean("ramadan_enabled", true)) {
                    scheduler.scheduleRamadan(
                        prefs.getString("sehri", "04:20") ?: "04:20",
                        prefs.getString("iftar", "17:55") ?: "17:55",
                        true
                    )
                }
            } finally {
                pending.finish()
            }
        }
    }
}
