package com.lurkbd.agent.util

import java.text.NumberFormat
import java.util.Locale

private val bnLocale = Locale("bn", "BD")

fun formatTaka(amount: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    nf.maximumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
    return "৳${nf.format(amount)}"
}

fun formatMinutes(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}

fun dayNameBn(dayOfWeek: Int): String = when (dayOfWeek) {
    java.util.Calendar.SUNDAY -> "রবি"
    java.util.Calendar.MONDAY -> "সোম"
    java.util.Calendar.TUESDAY -> "মঙ্গল"
    java.util.Calendar.WEDNESDAY -> "বুধ"
    java.util.Calendar.THURSDAY -> "বৃহস্পতি"
    java.util.Calendar.FRIDAY -> "শুক্র"
    java.util.Calendar.SATURDAY -> "শনি"
    else -> "?"
}
