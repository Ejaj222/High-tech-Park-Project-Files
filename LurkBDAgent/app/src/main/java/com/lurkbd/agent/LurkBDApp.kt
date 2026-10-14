package com.lurkbd.agent

import android.app.Application
import android.content.Context
import com.lurkbd.agent.data.db.LurkDatabase
import com.lurkbd.agent.data.db.seedIfEmpty
import com.lurkbd.agent.data.repository.LurkRepository
import com.lurkbd.agent.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LurkBDApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: LurkDatabase
        private set
    lateinit var repository: LurkRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = LurkDatabase.get(this)
        repository = LurkRepository(database)
        val prefs = getSharedPreferences("lurkbd", Context.MODE_PRIVATE)
        appScope.launch {
            if (!prefs.getBoolean("seeded", false)) {
                seedIfEmpty(database)
                prefs.edit().putBoolean("seeded", true).apply()
            }
            rescheduleAllAlarms()
        }
    }

    private suspend fun rescheduleAllAlarms() {
        val scheduler = AlarmScheduler(this)
        repository.medicines().first().forEach { scheduler.scheduleMedicine(it) }
        repository.classPeriods().first().forEach { scheduler.scheduleClass(it) }
        val alarmPrefs = getSharedPreferences("lurkbd_alarms", Context.MODE_PRIVATE)
        if (alarmPrefs.getBoolean("prayer_enabled", true)) {
            listOf(
                "ফজর" to "05:05",
                "যোহর" to "12:10",
                "আসর" to "15:45",
                "মাগরিব" to "17:55",
                "এশা" to "19:20"
            ).forEach { (n, t) -> scheduler.schedulePrayer(n, t, true) }
        }
        scheduler.scheduleRamadan(
            alarmPrefs.getString("sehri", "04:20") ?: "04:20",
            alarmPrefs.getString("iftar", "17:55") ?: "17:55",
            alarmPrefs.getBoolean("ramadan_enabled", true)
        )
    }
}

fun Context.lurkApp(): LurkBDApp = applicationContext as LurkBDApp
