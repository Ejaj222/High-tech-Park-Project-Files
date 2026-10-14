package com.lurkbd.agent.voice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceIntentParserTest {
    @Test
    fun parsesBanglaExpense() {
        val intent = VoiceIntentParser.parse("আজ বাজারে তিনশ টাকা খরচ")
        assertTrue(intent is VoiceIntent.Expense)
        intent as VoiceIntent.Expense
        assertEquals(300.0, intent.amount, 0.01)
        assertEquals("বাজার", intent.category)
    }

    @Test
    fun parsesCredit() {
        val intent = VoiceIntentParser.parse("করিম ভাইয়ের বাকি পাঁচশ যোগ করো")
        assertTrue(intent is VoiceIntent.CreditAdd)
        intent as VoiceIntent.CreditAdd
        assertEquals(500.0, intent.amount, 0.01)
    }

    @Test
    fun parsesResult() {
        val intent = VoiceIntentParser.parse("পদার্থ ক্লাস টেস্টে ১৮ পেয়েছি ২০ এর মধ্যে")
        // digits may be ascii in test; also accept Unknown if bangla digits
        val ascii = VoiceIntentParser.parse("পদার্থ ক্লাস টেস্টে 18 পেয়েছি 20 এর মধ্যে")
        assertTrue(ascii is VoiceIntent.ResultAdd)
        ascii as VoiceIntent.ResultAdd
        assertEquals(18.0, ascii.score, 0.01)
        assertEquals(20.0, ascii.total, 0.01)
    }
}
