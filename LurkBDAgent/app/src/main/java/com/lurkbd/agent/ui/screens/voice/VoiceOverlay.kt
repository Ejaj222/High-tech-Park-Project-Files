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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.SandSurface
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
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        recognizer?.startListening(intent)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { vm.closeVoice() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SandSurface)
                .clickable(enabled = false) {}
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LurkBD", style = MaterialTheme.typography.titleMedium, color = TealPrimary)
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(TealPrimary)
                    .clickable { startListening() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = SandSurface
                )
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(state.message.ifBlank { "শুনছি…" }, style = MaterialTheme.typography.headlineMedium)
            if (state.transcript.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("“${state.transcript}”", style = MaterialTheme.typography.bodyLarge)
            }
            if (state.preview.isNotBlank() && state.intent !is VoiceIntent.Unknown) {
                Spacer(Modifier.height(8.dp))
                Text(state.preview, style = MaterialTheme.typography.titleMedium, color = TealPrimary)
            }
            Spacer(Modifier.height(16.dp))
            Text("উদাহরণ", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            val samples = listOf(
                "আজ বাজারে তিনশ টাকা খরচ",
                "করিম ভাইয়ের বাকি পাঁচশ যোগ করো",
                "সাবান স্টক পাঁচ",
                "সকাল আটটায় মেটফরমিন মনে করাও",
                "পদার্থ ক্লাস টেস্টে ১৮ পেয়েছি ২০ এর মধ্যে"
            )
            samples.chunked(1).forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEach { sample ->
                        AssistChip(
                            onClick = { vm.onTranscript(sample) },
                            label = { Text(sample, maxLines = 1) }
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(12.dp))
            if (state.intent != null && state.intent !is VoiceIntent.Unknown) {
                PrimaryButton("নিশ্চিত করে সেভ") { vm.confirmVoice() }
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("আবার শুনুন") { startListening() }
            TextButton(onClick = { vm.closeVoice() }) {
                Text("বন্ধ")
            }
        }
    }
}
