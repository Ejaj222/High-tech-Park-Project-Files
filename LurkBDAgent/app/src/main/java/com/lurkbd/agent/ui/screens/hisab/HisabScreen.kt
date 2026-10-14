package com.lurkbd.agent.ui.screens.hisab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.ListRow
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenScaffold
import com.lurkbd.agent.ui.components.SectionLabel
import com.lurkbd.agent.ui.theme.Success
import com.lurkbd.agent.ui.theme.TealPrimary
import com.lurkbd.agent.util.formatTaka

@Composable
fun HisabScreen(vm: LurkViewModel) {
    val hisab by vm.hisab.collectAsState()
    val all by vm.allTransactions.collectAsState()
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("বাজার") }
    var channel by remember { mutableStateOf("নগদ") }
    var isIncome by remember { mutableStateOf(false) }

    ScreenScaffold(
        title = "হিসাব",
        subtitle = "আজকের আয় ও খরচ"
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("আয়", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        formatTaka(hisab.income),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Success
                    )
                }
                Column {
                    Text("খরচ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        formatTaka(hisab.expense),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TealPrimary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("নতুন এন্ট্রি")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isIncome,
                    onClick = { isIncome = false },
                    label = { Text("খরচ") }
                )
                FilterChip(
                    selected = isIncome,
                    onClick = { isIncome = true },
                    label = { Text("আয়") }
                )
            }
            Spacer(Modifier.height(8.dp))
            LurkField(
                amount,
                { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                "পরিমাণ (৳)"
            )
            Spacer(Modifier.height(8.dp))
            LurkField(category, { category = it }, "ক্যাটাগরি")
            Spacer(Modifier.height(8.dp))
            LurkField(channel, { channel = it }, "মাধ্যম")
            Spacer(Modifier.height(12.dp))
            PrimaryButton("সেভ করুন") {
                val value = amount.toDoubleOrNull() ?: return@PrimaryButton
                if (isIncome) {
                    vm.addIncome(value, category.ifBlank { "আয়" }, channel.ifBlank { "নগদ" })
                } else {
                    vm.addExpense(value, category.ifBlank { "খরচ" }, channel.ifBlank { "নগদ" })
                }
                amount = ""
            }

            SectionLabel("আজকের লেনদেন")
            if (hisab.transactions.isEmpty()) {
                Text("আজ এখনো কোনো এন্ট্রি নেই", style = MaterialTheme.typography.bodyMedium)
            } else {
                hisab.transactions.forEach { tx ->
                    ListRow(
                        title = tx.category,
                        subtitle = "${tx.channel} · ${if (tx.type == "INCOME") "আয়" else "খরচ"}",
                        trailing = formatTaka(tx.amount),
                        highlight = tx.type == "INCOME"
                    )
                }
            }

            if (hisab.loans.isNotEmpty()) {
                SectionLabel("লোন")
                hisab.loans.forEach { loan ->
                    ListRow(
                        title = loan.party,
                        subtitle = "পরিশোধ ${formatTaka(loan.paid)}",
                        trailing = formatTaka(loan.principal - loan.paid)
                    )
                }
            }

            SectionLabel("সাম্প্রতিক সব")
            all.take(12).forEach { tx ->
                ListRow(
                    title = tx.category,
                    subtitle = tx.channel,
                    trailing = formatTaka(tx.amount)
                )
            }
        }
    }
}
