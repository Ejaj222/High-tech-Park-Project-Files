package com.lurkbd.agent.ui.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.HeroBand
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenScaffold
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.util.formatTaka

@Composable
fun HomeScreen(vm: LurkViewModel, onOpenVoice: () -> Unit) {
    val hisab by vm.hisab.collectAsState()
    val classes by vm.classPeriods.collectAsState()
    val products by vm.products.collectAsState()
    val lowStock = products.count { it.qty <= it.lowThreshold }

    ScreenScaffold(title = "LurkBD", subtitle = "Agent") {
        HeroBand {
            Text("আজকের হিসাব", style = MaterialTheme.typography.headlineMedium, color = SandSurface)
            Spacer(Modifier.height(8.dp))
            Text(
                "দৈনন্দিন টাকা, দোকান, পড়াশোনা ও রিমাইন্ডার — কণ্ঠে বলুন।",
                style = MaterialTheme.typography.bodyLarge,
                color = GoldSoft
            )
            Spacer(Modifier.height(18.dp))
            PrimaryButton(text = "ভয়েস দিয়ে বলুন", onClick = onOpenVoice)
        }
        Spacer(Modifier.height(24.dp))
        Text("আজ", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "আয় ${formatTaka(hisab.income)} · খরচ ${formatTaka(hisab.expense)}",
            style = MaterialTheme.typography.bodyLarge
        )
        if (classes.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                "ক্লাস: ${classes.take(2).joinToString { it.subjectName }}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (lowStock > 0) {
            Spacer(Modifier.height(8.dp))
            Text("${lowStock}টি পণ্যের স্টক কম", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
