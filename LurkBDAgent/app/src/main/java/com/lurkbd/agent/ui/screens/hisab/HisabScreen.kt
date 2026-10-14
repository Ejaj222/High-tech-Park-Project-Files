package com.lurkbd.agent.ui.screens.hisab

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.ListRowRich
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenBody
import com.lurkbd.agent.ui.components.SectionTitle
import com.lurkbd.agent.ui.components.SoftCard
import com.lurkbd.agent.ui.components.TealAppHeader
import com.lurkbd.agent.ui.theme.Danger
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.SandSurface
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

    Column(Modifier.fillMaxSize().background(SandSurface)) {
        TealAppHeader(title = "হিসাব")
        ScreenBody(Modifier.verticalScroll(rememberScrollState())) {
            SoftCard {
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Success.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.ArrowDownward, null, tint = Success, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.size(6.dp))
                            Text("আয়", color = Success, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            formatTaka(hisab.income),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text("আজ", color = InkMuted, fontSize = 12.sp)
                    }
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(64.dp)
                            .background(InkMuted.copy(alpha = 0.15f))
                    )
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Danger.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.ArrowUpward, null, tint = Danger, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.size(6.dp))
                            Text("খরচ", color = Danger, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            formatTaka(hisab.expense),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Danger,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text("আজ", color = InkMuted, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            SectionTitle("নতুন এন্ট্রি")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !isIncome, onClick = { isIncome = false }, label = { Text("খরচ") })
                FilterChip(selected = isIncome, onClick = { isIncome = true }, label = { Text("আয়") })
            }
            Spacer(Modifier.height(8.dp))
            LurkField(amount, { amount = it.filter { ch -> ch.isDigit() || ch == '.' } }, "পরিমাণ (৳)")
            Spacer(Modifier.height(8.dp))
            LurkField(category, { category = it }, "ক্যাটাগরি")
            Spacer(Modifier.height(8.dp))
            LurkField(channel, { channel = it }, "মাধ্যম")
            Spacer(Modifier.height(10.dp))
            PrimaryButton("সেভ করুন") {
                val value = amount.toDoubleOrNull() ?: return@PrimaryButton
                if (isIncome) vm.addIncome(value, category.ifBlank { "আয়" }, channel.ifBlank { "নগদ" })
                else vm.addExpense(value, category.ifBlank { "খরচ" }, channel.ifBlank { "নগদ" })
                amount = ""
            }

            SectionTitle("সাম্প্রতিক লেনদেন", action = "সব দেখুন >")
            SoftCard {
                val list = if (hisab.transactions.isNotEmpty()) hisab.transactions else all.take(6)
                if (list.isEmpty()) {
                    Text("এখনো কোনো লেনদেন নেই", color = InkMuted)
                } else {
                    list.forEach { tx ->
                        ListRowRich(
                            leading = { TxIcon(tx.category) },
                            title = tx.category,
                            subtitle = if (tx.type == "INCOME") "আয়" else "খরচ",
                            trailing = formatTaka(tx.amount),
                            trailingColor = if (tx.type == "INCOME") Success else Danger,
                            tag = tx.channel
                        )
                    }
                }
            }

            if (hisab.loans.isNotEmpty()) {
                SectionTitle("লোন")
                SoftCard {
                    hisab.loans.forEach { loan ->
                        ListRowRich(
                            leading = {
                                Box(
                                    Modifier.size(42.dp).clip(CircleShape).background(GoldAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.AccountBalance, null, tint = GoldAccent)
                                }
                            },
                            title = loan.party,
                            subtitle = "পরিশোধ ${formatTaka(loan.paid)}",
                            trailing = formatTaka(loan.principal - loan.paid),
                            trailingColor = TealPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TxIcon(category: String) {
    val (icon, bg) = when {
        category.contains("বাজার") || category.contains("খাবার") -> Icons.Outlined.ShoppingBasket to Color(0xFFE8D5C4)
        category.contains("বেতন") || category.contains("আয়") -> Icons.Outlined.WorkOutline to Color(0xFFE8D5A3)
        category.contains("রিচার্জ") || category.contains("মোবাইল") -> Icons.Outlined.PhoneAndroid to Color(0xFFD6E4F0)
        else -> Icons.Outlined.AccountBalance to Color(0xFFE0D6F0)
    }
    Box(
        Modifier.size(42.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
    }
}
