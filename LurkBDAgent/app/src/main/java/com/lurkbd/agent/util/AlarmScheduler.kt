package com.lurkbd.agent.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.lurkbd.agent.data.model.ClassPeriodEntity
import com.lurkbd.agent.data.model.MedicineEntity
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMedicine(med: MedicineEntity) {
        if (!med.active) {
            cancel(medicineCode(med.id))
            return
        }
        val trigger = nextDailyTrigger(med.timeMinutes)
        scheduleExact(
            requestCode = medicineCode(med.id),
            triggerAt = trigger,
            title = "ওষুধের সময়",
            body = "${med.name}${if (med.dose.isBlank()) "" else " · ${med.dose}"} — এখন খান",
            type = TYPE_MEDICINE,
            entityId = med.id,
            minutesOfDay = med.timeMinutes
        )
    }

    fun cancelMedicine(id: Long) = cancel(medicineCode(id))

    fun scheduleClass(period: ClassPeriodEntity) {
        if (!period.alarmEnabled) {
            cancel(classCode(period.id))
            return
        }
        val alarmMinutes = (period.startMinutes - period.alarmOffsetMinutes).coerceAtLeast(0)
        val trigger = nextWeeklyTrigger(period.dayOfWeek, alarmMinutes)
        scheduleExact(
            requestCode = classCode(period.id),
            triggerAt = trigger,
            title = "ক্লাস রিমাইন্ডার",
            body = "${period.subjectName} ক্লাস শুরু হতে যাচ্ছে",
            type = TYPE_CLASS,
            entityId = period.id,
            minutesOfDay = alarmMinutes,
            dayOfWeek = period.dayOfWeek
        )
    }

    fun cancelClass(id: Long) = cancel(classCode(id))

    fun schedulePrayer(name: String, timeHHmm: String, enabled: Boolean) {
        val code = prayerCode(name)
        if (!enabled) {
            cancel(code)
            return
        }
        val parts = timeHHmm.split(":")
        val minutes = (parts.getOrNull(0)?.toIntOrNull() ?: 0) * 60 +
            (parts.getOrNull(1)?.toIntOrNull() ?: 0)
        scheduleExact(
            requestCode = code,
            triggerAt = nextDailyTrigger(minutes),
            title = "নামাজের সময়",
            body = "$name — ${formatMinutes(minutes)}",
            type = TYPE_PRAYER,
            entityId = code.toLong(),
            minutesOfDay = minutes,
            label = name
        )
    }

    fun scheduleRamadan(sehriHHmm: String, iftarHHmm: String, enabled: Boolean) {
        if (!enabled) {
            cancel(RAMADAN_SEHRI)
            cancel(RAMADAN_IFTAR)
            return
        }
        fun parse(t: String): Int {
            val p = t.split(":")
            return (p.getOrNull(0)?.toIntOrNull() ?: 0) * 60 + (p.getOrNull(1)?.toIntOrNull() ?: 0)
        }
        val sehri = parse(sehriHHmm)
        val iftar = parse(iftarHHmm)
        scheduleExact(
            requestCode = RAMADAN_SEHRI,
            triggerAt = nextDailyTrigger(sehri),
            title = "সেহেরি",
            body = "সেহেরির সময় হয়ে গেছে · ${formatMinutes(sehri)}",
            type = TYPE_RAMADAN,
            entityId = RAMADAN_SEHRI.toLong(),
            minutesOfDay = sehri,
            label = "sehri"
        )
        scheduleExact(
            requestCode = RAMADAN_IFTAR,
            triggerAt = nextDailyTrigger(iftar),
            title = "ইফতার",
            body = "ইফতারের সময় হয়ে গেছে · ${formatMinutes(iftar)}",
            type = TYPE_RAMADAN,
            entityId = RAMADAN_IFTAR.toLong(),
            minutesOfDay = iftar,
            label = "iftar"
        )
    }

    fun rescheduleRepeating(intent: Intent) {
        val type = intent.getStringExtra(ReminderReceiver.EXTRA_TYPE) ?: return
        val minutes = intent.getIntExtra(ReminderReceiver.EXTRA_MINUTES, -1)
        if (minutes < 0) return
        when (type) {
            TYPE_MEDICINE, TYPE_PRAYER, TYPE_RAMADAN -> {
                val title = intent.getStringExtra(ReminderReceiver.EXTRA_TITLE) ?: return
                val body = intent.getStringExtra(ReminderReceiver.EXTRA_BODY) ?: return
                val code = intent.getIntExtra(ReminderReceiver.EXTRA_REQUEST_CODE, 0)
                val label = intent.getStringExtra(ReminderReceiver.EXTRA_LABEL)
                scheduleExact(
                    requestCode = code,
                    triggerAt = nextDailyTrigger(minutes),
                    title = title,
                    body = body,
                    type = type,
                    entityId = intent.getLongExtra(ReminderReceiver.EXTRA_ENTITY_ID, 0L),
                    minutesOfDay = minutes,
                    label = label
                )
            }
            TYPE_CLASS -> {
                val day = intent.getIntExtra(ReminderReceiver.EXTRA_DAY_OF_WEEK, -1)
                if (day < 0) return
                val title = intent.getStringExtra(ReminderReceiver.EXTRA_TITLE) ?: return
                val body = intent.getStringExtra(ReminderReceiver.EXTRA_BODY) ?: return
                val code = intent.getIntExtra(ReminderReceiver.EXTRA_REQUEST_CODE, 0)
                scheduleExact(
                    requestCode = code,
                    triggerAt = nextWeeklyTrigger(day, minutes),
                    title = title,
                    body = body,
                    type = type,
                    entityId = intent.getLongExtra(ReminderReceiver.EXTRA_ENTITY_ID, 0L),
                    minutesOfDay = minutes,
                    dayOfWeek = day
                )
            }
        }
    }

    private fun scheduleExact(
        requestCode: Int,
        triggerAt: Long,
        title: String,
        body: String,
        type: String,
        entityId: Long,
        minutesOfDay: Int,
        dayOfWeek: Int? = null,
        label: String? = null
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_BODY, body)
            putExtra(ReminderReceiver.EXTRA_TYPE, type)
            putExtra(ReminderReceiver.EXTRA_ENTITY_ID, entityId)
            putExtra(ReminderReceiver.EXTRA_MINUTES, minutesOfDay)
            putExtra(ReminderReceiver.EXTRA_REQUEST_CODE, requestCode)
            dayOfWeek?.let { putExtra(ReminderReceiver.EXTRA_DAY_OF_WEEK, it) }
            label?.let { putExtra(ReminderReceiver.EXTRA_LABEL, it) }
        }
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            return
        }
        val show = PendingIntent.getActivity(
            context,
            requestCode + 50_000,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAt, show), pending)
    }

    private fun cancel(requestCode: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply { action = ACTION_REMINDER }
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }

    companion object {
        const val ACTION_REMINDER = "com.lurkbd.agent.ACTION_REMINDER"
        const val TYPE_MEDICINE = "medicine"
        const val TYPE_CLASS = "class"
        const val TYPE_PRAYER = "prayer"
        const val TYPE_RAMADAN = "ramadan"
        const val RAMADAN_SEHRI = 400_001
        const val RAMADAN_IFTAR = 400_002

        fun medicineCode(id: Long) = (100_000 + id).toInt()
        fun classCode(id: Long) = (200_000 + id).toInt()
        fun prayerCode(name: String) = 300_000 + (name.hashCode() and 0xFFFF)

        fun nextDailyTrigger(minutesOfDay: Int): Long {
            val cal = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
                set(Calendar.MINUTE, minutesOfDay % 60)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            return cal.timeInMillis
        }

        fun nextWeeklyTrigger(dayOfWeek: Int, minutesOfDay: Int): Long {
            val cal = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
                set(Calendar.MINUTE, minutesOfDay % 60)
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.WEEK_OF_YEAR, 1)
            }
            return cal.timeInMillis
        }
    }
}
