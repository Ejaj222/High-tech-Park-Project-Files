package com.lurkbd.agent.ui.screens.voice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.theme.Cream
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealPrimary
import com.lurkbd.agent.voice.VoiceIntent

@Composable
fun VoiceOverlay(vm: LurkViewModel) {
    val state by vm.voice.collectAsState()
    if (!state.open) return

    val context = LocalContext.current
    val activity = context as? Activity
    val recognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onError(error: Int) = Unit
            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                if (text.isNotBlank()) vm.onTranscript(text)
            }
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
        recognizer?.setRecognitionListener(listener)
        onDispose { recognizer?.destroy() }
    }

    fun startListening() {
        if (activity != null &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
        }
        recognizer?.startListening(intent)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xEE1A1008), Color(0xF20B4F4A), Color(0xF2062E2B))
                )
            )
            .clickable { vm.closeVoice() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clickable(enabled = false) {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "LurkBD",
                fontFamily = FontFamily.Serif,
                color = GoldAccent,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text("শুনুন। বলুন। গুছিয়ে রাখুন।", color = GoldSoft, fontSize = 13.sp)
            Spacer(Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                WaveMini()
                Spacer(Modifier.width(8.dp))
                Text(
                    state.message.ifBlank { "শুনছি…" },
                    color = GoldAccent,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(22.dp))

            Box(contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(2.dp, GoldSoft.copy(alpha = 0.35f), CircleShape)
                )
                Box(
                    Modifier
                        .size(118.dp)
                        .clip(CircleShape)
                        .border(2.dp, GoldAccent.copy(alpha = 0.7f), CircleShape)
                )
                Box(
                    Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(TealPrimary)
                        .clickable { startListening() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, null, tint = GoldAccent, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(Modifier.height(18.dp))
            if (state.transcript.isNotBlank()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        "“${state.transcript}”",
                        color = Cream,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (state.preview.isNotBlank() && state.intent !is VoiceIntent.Unknown) {
                Spacer(Modifier.height(8.dp))
                Text(state.preview, color = GoldAccent, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))
            val chips = listOf("খরচ যোগ", "বাকি আপডেট", "ওষুধ অ্যালার্ম", "সেহেরি")
            val samples = listOf(
                "আজ বাজারে তিনশ টাকা খরচ",
                "করিম ভাইয়ের বাকি পাঁচশ যোগ করো",
                "সকাল আটটায় মেটফরমিন মনে করাও",
                "রোজায় সেহেরি ও ইফতারের অ্যালার্ম দাও"
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chips.chunked(2).forEachIndexed { rowIndex, row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEachIndexed { i, label ->
                            val sample = samples.getOrElse(rowIndex * 2 + i) { samples.first() }
                            Box(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, GoldAccent.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                                    .clickable { vm.onTranscript(sample) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = GoldSoft, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("কমান্ড বলুন অথবা বন্ধ করতে ট্যাপ করুন", color = GoldSoft.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            if (state.intent != null && state.intent !is VoiceIntent.Unknown) {
                PrimaryButton("নিশ্চিত করে সেভ") { vm.confirmVoice() }
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("আবার শুনুন") { startListening() }
            TextButton(onClick = { vm.closeVoice() }) {
                Text("বন্ধ", color = GoldSoft)
            }
        }
    }
}

@Composable
private fun WaveMini() {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        listOf(6, 12, 8, 14, 7).forEach {
            Box(Modifier.width(2.dp).height(it.dp).background(GoldAccent))
        }
    }
}
