package com.lurkbd.agent.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.GoldMicButton
import com.lurkbd.agent.ui.components.GoldOutlinePill
import com.lurkbd.agent.ui.theme.Cream
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealPrimary
import com.lurkbd.agent.util.formatTaka

@Composable
fun HomeScreen(vm: LurkViewModel, onOpenVoice: () -> Unit) {
    val hisab by vm.hisab.collectAsState()

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to TealPrimary,
                        0.48f to TealDark,
                        0.72f to Color(0xFF3D5A45),
                        1.0f to Color(0xFFC4A574)
                    )
                )
            )
    ) {
        GeometricBackdrop()
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 56.dp, bottom = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "LurkBD",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp,
                color = Cream,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(28.dp).height(1.dp).background(GoldSoft.copy(alpha = 0.7f)))
                Text(
                    "  Agent  ",
                    color = GoldSoft,
                    fontSize = 14.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(Modifier.width(28.dp).height(1.dp).background(GoldSoft.copy(alpha = 0.7f)))
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "আজকের হিসাব",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "দৈনিক টাকা, দোকান, ওষুধ, আর নামাজ —\nসব এক অ্যাপে, সহজ হিসাবে।",
                color = Cream.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "আয় ${formatTaka(hisab.income)} · খরচ ${formatTaka(hisab.expense)}",
                color = GoldSoft,
                fontSize = 13.sp
            )
            Spacer(Modifier.weight(1f))
            GoldOutlinePill("ভয়েস দিয়ে বলুন") { onOpenVoice() }
            Spacer(Modifier.height(22.dp))
            GoldMicButton(onClick = onOpenVoice, large = true)
            Spacer(Modifier.height(10.dp))
            SoundWaveRow()
        }
    }
}

@Composable
private fun SoundWaveRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heights = listOf(8, 14, 22, 12, 18, 10, 16, 24, 14, 20, 12, 8)
        heights.forEach { h ->
            Box(
                Modifier
                    .width(3.dp)
                    .height(h.dp)
                    .background(GoldAccent.copy(alpha = 0.85f))
            )
        }
    }
}

@Composable
private fun GeometricBackdrop() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val teal = Color(0x22000000)
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.55f, 0f)
            lineTo(0f, h * 0.55f)
            close()
        }
        drawPath(path, teal)
        drawCircle(
            color = GoldAccent.copy(alpha = 0.08f),
            radius = w * 0.55f,
            center = Offset(w * 0.85f, h * 0.72f)
        )
        drawCircle(
            color = GoldAccent.copy(alpha = 0.06f),
            radius = w * 0.4f,
            center = Offset(w * 0.85f, h * 0.72f)
        )
    }
}
