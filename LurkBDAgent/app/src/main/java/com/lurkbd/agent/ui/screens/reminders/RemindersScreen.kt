package com.lurkbd.agent.ui.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Brightness2
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.util.formatMinutes

@Composable
fun RemindersScreen(vm: LurkViewModel) {
    val medicines by vm.medicines.collectAsState()
    val prayers by vm.prayerAlarms.collectAsState()
    val ramadan by vm.ramadanAlarms.collectAsState()
    var name by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("8:00") }
    var ramadanOn by remember { mutableStateOf(ramadan.second) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TealDark, Color(0xFF0A2A28), Color(0xFF163A32))))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 48.dp, bottom = 110.dp)
        ) {
            Text(
                "রিমাইন্ডার",
                color = GoldAccent,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(20.dp))

            GoldPanel {
                PanelHead(Icons.Outlined.LocalPharmacy, "ওষুধ", "মেডিসিন শিডিউল")
                medicines.forEach { med ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.LocalPharmacy, null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(med.name, color = GoldSoft, fontWeight = FontWeight.SemiBold)
                            Text(formatMinutes(med.timeMinutes), color = GoldSoft.copy(0.75f), fontSize = 13.sp)
                        }
                        Switch(
                            checked = med.active,
                            onCheckedChange = { vm.toggleMedicine(med, it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = GoldAccent, checkedThumbColor = Color.White)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                LurkField(name, { name = it }, "ওষুধের নাম")
                Spacer(Modifier.height(6.dp))
                LurkField(timeText, { timeText = it }, "সময় (HH:MM)")
                Spacer(Modifier.height(8.dp))
                PrimaryButton("ওষুধ অ্যালার্ম যোগ") {
                    if (name.isBlank()) return@PrimaryButton
                    val parts = timeText.split(":", ".", limit = 2)
                    val minutes = (parts.getOrNull(0)?.toIntOrNull() ?: 8) * 60 + (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                    vm.addMedicine(name.trim(), minutes)
                    name = ""
                }
            }

            Spacer(Modifier.height(14.dp))
            GoldPanel {
                PanelHead(Icons.Outlined.Place, "নামাজ", "পাঁচ ওয়াক্ত নামাজ (ঢাকা)")
                prayers.forEach { (waqt, time) ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Place, null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(waqt, color = GoldSoft)
                        }
                        Text(time, color = GoldAccent, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            GoldPanel {
                PanelHead(Icons.Outlined.Brightness2, "রোজা", "রমজানের সময়সূচি")
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Alarm, null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(ramadan.first, color = GoldSoft)
                    }
                    Switch(
                        checked = ramadanOn,
                        onCheckedChange = {
                            ramadanOn = it
                            vm.ramadanAlarms.value = ramadan.first to it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = GoldAccent, checkedThumbColor = Color.White)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoldPanel(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xCC0B2E2B))
            .border(1.dp, GoldAccent.copy(0.45f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) { content() }
}

@Composable
private fun PanelHead(icon: ImageVector, title: String, subtitle: String) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(34.dp).clip(CircleShape).background(GoldAccent.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = GoldAccent, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(subtitle, color = GoldSoft.copy(0.7f), fontSize = 12.sp)
            }
        }
        Icon(Icons.Outlined.Notifications, null, tint = GoldAccent)
    }
}
