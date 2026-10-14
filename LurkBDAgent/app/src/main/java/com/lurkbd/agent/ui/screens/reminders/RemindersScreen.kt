package com.lurkbd.agent.ui.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.ListRow
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenScaffold
import com.lurkbd.agent.ui.components.SectionLabel
import com.lurkbd.agent.util.formatMinutes

@Composable
fun RemindersScreen(vm: LurkViewModel) {
    val medicines by vm.medicines.collectAsState()
    val prayers by vm.prayerAlarms.collectAsState()
    val ramadan by vm.ramadanAlarms.collectAsState()
    var name by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("8:00") }
    var prayerEnabled by remember { mutableStateOf(prayers.associate { it.first to true }.toMutableMap()) }
    var ramadanOn by remember { mutableStateOf(ramadan.second) }

    ScreenScaffold(title = "রিমাইন্ডার", subtitle = "ওষুধ · নামাজ · রোজা") {
        androidx.compose.foundation.layout.Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            SectionLabel("ওষুধ")
            medicines.forEach { med ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                        ListRow(
                            title = med.name,
                            subtitle = "${med.dose.ifBlank { "ডোজ" }} · ${formatMinutes(med.timeMinutes)}"
                        )
                    }
                    Switch(
                        checked = med.active,
                        onCheckedChange = { vm.toggleMedicine(med, it) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            LurkField(name, { name = it }, "ওষুধের নাম")
            Spacer(Modifier.height(8.dp))
            LurkField(timeText, { timeText = it }, "সময় (HH:MM)")
            Spacer(Modifier.height(8.dp))
            PrimaryButton("ওষুধ অ্যালার্ম যোগ") {
                if (name.isBlank()) return@PrimaryButton
                val parts = timeText.split(":", ".", limit = 2)
                val minutes = (parts.getOrNull(0)?.toIntOrNull() ?: 8) * 60 +
                    (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                vm.addMedicine(name.trim(), minutes)
                name = ""
            }

            SectionLabel("নামাজ (ঢাকা)")
            prayers.forEach { (waqt, time) ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$waqt · $time", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = prayerEnabled[waqt] == true,
                        onCheckedChange = { prayerEnabled = prayerEnabled.toMutableMap().also { m -> m[waqt] = it } }
                    )
                }
            }

            SectionLabel("রোজা")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(ramadan.first, style = MaterialTheme.typography.titleMedium)
                Switch(checked = ramadanOn, onCheckedChange = {
                    ramadanOn = it
                    vm.ramadanAlarms.value = ramadan.first to it
                })
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "নামাজ/সেহেরি সময় লোকেশন অনুযায়ী আপডেট করা যাবে। ডিফল্ট: ঢাকা।",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
