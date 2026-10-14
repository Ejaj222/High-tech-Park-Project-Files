package com.lurkbd.agent

import android.app.Application
import android.content.Context
import com.lurkbd.agent.data.db.LurkDatabase
import com.lurkbd.agent.data.db.seedIfEmpty
import com.lurkbd.agent.data.repository.LurkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
        if (!prefs.getBoolean("seeded", false)) {
            appScope.launch {
                seedIfEmpty(database)
                prefs.edit().putBoolean("seeded", true).apply()
            }
        }
    }
}

fun Context.lurkApp(): LurkBDApp = applicationContext as LurkBDApp
