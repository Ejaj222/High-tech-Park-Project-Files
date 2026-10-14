package com.lurkbd.agent.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.lurkbd.agent.ui.components.TealMicWithRings
import com.lurkbd.agent.ui.components.VoicePill
import com.lurkbd.agent.ui.components.WaveBars
import com.lurkbd.agent.ui.theme.Cream
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealDeep
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
                    0.0f to TealPrimary,
                    0.42f to TealDark,
                    0.62f to Color(0xFF3A5648),
                    0.82f to Color(0xFFB8955E),
                    1.0f to Color(0xFFD4B888)
                )
            )
    ) {
        HomeBackdrop()
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 58.dp, bottom = 108.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "LurkBD",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(26.dp).height(1.dp).background(GoldSoft.copy(0.75f)))
                Text(
                    "  Agent  ",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 3.sp
                )
                Box(Modifier.width(26.dp).height(1.dp).background(GoldSoft.copy(0.75f)))
            }
            Spacer(Modifier.height(30.dp))
            Text(
                "আজকের হিসাব",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "দৈনিক টাকা, দোকান, ওষুধ, আর নামাজ —\nসব এক অ্যাপে, সহজ হিসাবে।",
                color = Cream.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "আয় ${formatTaka(hisab.income)} · খরচ ${formatTaka(hisab.expense)}",
                color = GoldSoft,
                fontSize = 12.sp
            )
            Spacer(Modifier.weight(1f))
            VoicePill("ভয়েস দিয়ে বলুন", onOpenVoice)
            Spacer(Modifier.height(22.dp))
            TealMicWithRings(onClick = onOpenVoice, size = 96.dp)
            Spacer(Modifier.height(14.dp))
            WaveBars(color = GoldAccent, tall = true)
        }
    }
}

@Composable
private fun HomeBackdrop() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawPath(
            Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.62f, 0f)
                lineTo(0f, h * 0.52f)
                close()
            },
            Color(0x22000000)
        )
        // soft skyline suggestion
        val baseY = h * 0.78f
        drawCircle(GoldAccent.copy(0.08f), w * 0.5f, Offset(w * 0.82f, baseY))
        drawCircle(GoldAccent.copy(0.05f), w * 0.36f, Offset(w * 0.82f, baseY))
        // simple building silhouettes
        val buildingColor = Color(0x33FFFFFF)
        drawRect(buildingColor, Offset(w * 0.62f, baseY - 40.dp.toPx()), androidx.compose.ui.geometry.Size(18.dp.toPx(), 40.dp.toPx()))
        drawRect(buildingColor, Offset(w * 0.70f, baseY - 70.dp.toPx()), androidx.compose.ui.geometry.Size(14.dp.toPx(), 70.dp.toPx()))
        drawRect(buildingColor, Offset(w * 0.78f, baseY - 52.dp.toPx()), androidx.compose.ui.geometry.Size(22.dp.toPx(), 52.dp.toPx()))
        drawRect(buildingColor, Offset(w * 0.88f, baseY - 34.dp.toPx()), androidx.compose.ui.geometry.Size(16.dp.toPx(), 34.dp.toPx()))
    }
}
