package com.lurkbd.agent.voice

import java.util.Calendar

sealed class VoiceIntent {
    data class Expense(val amount: Double, val category: String, val channel: String = "নগদ") : VoiceIntent()
    data class Income(val amount: Double, val category: String, val channel: String = "নগদ") : VoiceIntent()
    data class CreditAdd(val name: String, val amount: Double) : VoiceIntent()
    data class StockSet(val name: String, val qty: Double) : VoiceIntent()
    data class MedicineAlarm(val name: String, val timeMinutes: Int) : VoiceIntent()
    data class ClassAdd(val subject: String, val dayOfWeek: Int, val startMinutes: Int) : VoiceIntent()
    data class StudyRoutine(val subject: String, val timeMinutes: Int) : VoiceIntent()
    data class ResultAdd(val subject: String, val score: Double, val total: Double) : VoiceIntent()
    data class Unknown(val reason: String) : VoiceIntent()
}

object VoiceIntentParser {
    fun parse(raw: String): VoiceIntent {
        val text = raw.trim().lowercase()
            .replace("৳", "")
            .replace(",", "")

        val amount = extractAmount(text)
        val channel = when {
            text.contains("বিকাশ") || text.contains("bkash") -> "bKash"
            text.contains("নগদ") && text.contains("মাধ্যম").not() && text.contains("nagad") -> "Nagad"
            text.contains("nagad") -> "Nagad"
            text.contains("ব্যাংক") || text.contains("bank") -> "ব্যাংক"
            else -> "নগদ"
        }

        when {
            text.contains("বাকি") && amount != null -> {
                val name = extractPerson(text) ?: "ক্রেতা"
                return VoiceIntent.CreditAdd(name, amount)
            }
            (text.contains("স্টক") || text.contains("stock")) && amount != null -> {
                val product = extractProduct(text) ?: "প্রোডাক্ট"
                return VoiceIntent.StockSet(product, amount)
            }
            text.contains("ক্লাস টেস্ট") || (text.contains("পেয়েছি") && amount != null) -> {
                val subject = extractSubject(text) ?: "সাবজেক্ট"
                val total = Regex("""(\d+(?:\.\d+)?)\s*(?:এর মধ্যে|out of|/)\s*(\d+(?:\.\d+)?)""")
                    .find(text)
                return if (total != null) {
                    VoiceIntent.ResultAdd(
                        subject,
                        total.groupValues[1].toDouble(),
                        total.groupValues[2].toDouble()
                    )
                } else if (amount != null) {
                    VoiceIntent.ResultAdd(subject, amount, 20.0)
                } else VoiceIntent.Unknown("রেজাল্ট বুঝতে পারিনি")
            }
            text.contains("ওষুধ") || text.contains("মেটফরমিন") || text.contains("মনে করাও") && text.contains("টায়") -> {
                val time = extractTimeMinutes(text) ?: (8 * 60)
                val name = when {
                    text.contains("মেটফরমিন") || text.contains("metformin") -> "Metformin"
                    text.contains("vitamin") || text.contains("ভিটামিন") -> "Vitamin D"
                    else -> "ওষুধ"
                }
                return VoiceIntent.MedicineAlarm(name, time)
            }
            text.contains("ক্লাস") && (text.contains("যোগ") || text.contains("রাখো") || text.contains("সেট")) -> {
                val subject = extractSubject(text) ?: "ক্লাস"
                val time = extractTimeMinutes(text) ?: (9 * 60)
                val day = extractDay(text) ?: Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                return VoiceIntent.ClassAdd(subject, day, time)
            }
            text.contains("পড়ার রুটিন") || (text.contains("পড়া") && text.contains("রুটিন")) -> {
                val subject = extractSubject(text) ?: "পড়া"
                val time = extractTimeMinutes(text) ?: (21 * 60)
                return VoiceIntent.StudyRoutine(subject, time)
            }
            text.contains("আয়") || text.contains("পেলাম") || text.contains("এসেছে") || text.contains("এসেছ") -> {
                if (amount == null) return VoiceIntent.Unknown("পরিমাণ পাইনি")
                val category = extractCategory(text, income = true)
                return VoiceIntent.Income(amount, category, channel)
            }
            text.contains("খরচ") || text.contains("দিয়েছি") || text.contains("কিনেছি") -> {
                if (amount == null) return VoiceIntent.Unknown("পরিমাণ পাইনি")
                val category = extractCategory(text, income = false)
                return VoiceIntent.Expense(amount, category, channel)
            }
            else -> return VoiceIntent.Unknown("কমান্ড বুঝতে পারিনি — আবার বলুন")
        }
    }

    fun describe(intent: VoiceIntent): String = when (intent) {
        is VoiceIntent.Expense -> "খরচ ৳${intent.amount.toInt()} · ${intent.category} · ${intent.channel}"
        is VoiceIntent.Income -> "আয় ৳${intent.amount.toInt()} · ${intent.category} · ${intent.channel}"
        is VoiceIntent.CreditAdd -> "${intent.name} এর বাকি +৳${intent.amount.toInt()}"
        is VoiceIntent.StockSet -> "${intent.name} স্টক = ${intent.qty}"
        is VoiceIntent.MedicineAlarm -> "${intent.name} অ্যালার্ম ${formatTime(intent.timeMinutes)}"
        is VoiceIntent.ClassAdd -> "${intent.subject} ক্লাস ${formatTime(intent.startMinutes)}"
        is VoiceIntent.StudyRoutine -> "${intent.subject} পড়ার রুটিন ${formatTime(intent.timeMinutes)}"
        is VoiceIntent.ResultAdd -> "${intent.subject}: ${intent.score}/${intent.total}"
        is VoiceIntent.Unknown -> intent.reason
    }

    private fun extractAmount(text: String): Double? {
        val banglaMap = mapOf(
            "একশ" to 100.0, "দুইশ" to 200.0, "দুইশো" to 200.0, "তিনশ" to 300.0, "তিনশো" to 300.0,
            "চারশ" to 400.0, "পাঁচশ" to 500.0, "পাচশ" to 500.0, "ছয়শ" to 600.0,
            "সাতশ" to 700.0, "আটশ" to 800.0, "নয়শ" to 900.0,
            "হাজার" to 1000.0, "দুই হাজার" to 2000.0, "পাঁচ হাজার" to 5000.0
        )
        banglaMap.entries.sortedByDescending { it.key.length }.forEach { (k, v) ->
            if (text.contains(k)) return v
        }
        return Regex("""(\d+(?:\.\d+)?)""").find(text)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun extractCategory(text: String, income: Boolean): String {
        val expenseCats = listOf("বাজার", "খাবার", "ভাড়া", "রিচার্জ", "মোবাইল", "লোন", "কিস্তি", "যাতায়াত")
        val incomeCats = listOf("বেতন", "বিক্রি", "ব্যবসা", "গিফট")
        (if (income) incomeCats else expenseCats).forEach { if (text.contains(it.lowercase()) || text.contains(it)) return it }
        return if (income) "আয়" else "খরচ"
    }

    private fun extractPerson(text: String): String? {
        Regex("""([অ-হa-zA-Z]+(?:\sভাই|\sবেগম|\sসাহেব)?)(?:\s*এর)?\s*বাকি""").find(text)?.groupValues?.get(1)
            ?.trim()?.takeIf { it.isNotBlank() }?.let { return it }
        return Regex("""(?:করিম|রহিমা|সালাম)[^\s]*""").find(text)?.value
    }

    private fun extractProduct(text: String): String? {
        listOf("সাবান", "চাল", "তেল", "ডাল", "চিনি", "লবণ").forEach {
            if (text.contains(it)) return it
        }
        return null
    }

    private fun extractSubject(text: String): String? {
        listOf("গণিত", "ইংরেজি", "পদার্থ", "রসায়ন", "বাংলা", "জীববিজ্ঞান", "ইতিহাস").forEach {
            if (text.contains(it)) return it
        }
        return null
    }

    private fun extractTimeMinutes(text: String): Int? {
        Regex("""(\d{1,2})[:\.](\d{2})""").find(text)?.let {
            return it.groupValues[1].toInt() * 60 + it.groupValues[2].toInt()
        }
        Regex("""(\d{1,2})\s*টা""").find(text)?.let {
            var h = it.groupValues[1].toInt()
            if (text.contains("রাত") && h < 12) h += if (h == 12) 0 else 12
            if (text.contains("সকাল") && h == 12) h = 0
            return h * 60
        }
        when {
            text.contains("সকাল আট") || text.contains("সকাল ৮") -> return 8 * 60
            text.contains("রাত নয়") || text.contains("রাত ৯") -> return 21 * 60
            text.contains("সকাল দশ") || text.contains("সকাল ১০") -> return 10 * 60
        }
        return null
    }

    private fun extractDay(text: String): Int? = when {
        text.contains("রবি") -> Calendar.SUNDAY
        text.contains("সোম") -> Calendar.MONDAY
        text.contains("মঙ্গল") -> Calendar.TUESDAY
        text.contains("বুধ") -> Calendar.WEDNESDAY
        text.contains("বৃহ") -> Calendar.THURSDAY
        text.contains("শুক্র") -> Calendar.FRIDAY
        text.contains("শনি") -> Calendar.SATURDAY
        else -> null
    }

    private fun formatTime(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return "%02d:%02d".format(h, m)
    }
}
