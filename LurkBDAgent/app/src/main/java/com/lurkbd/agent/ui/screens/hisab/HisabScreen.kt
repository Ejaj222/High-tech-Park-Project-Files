package com.lurkbd.agent.ui.screens.hisab

import androidx.compose.foundation.background
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
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.MockCard
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenPad
import com.lurkbd.agent.ui.components.SectionHead
import com.lurkbd.agent.ui.components.TxRow
import com.lurkbd.agent.ui.components.WavyTealHeader
import com.lurkbd.agent.ui.theme.Danger
import com.lurkbd.agent.ui.theme.GoldMuted
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
        WavyTealHeader(title = "হিসাব", titleColor = Color.White, wavy = false)
        ScreenPad {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                MockCard {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(26.dp).clip(CircleShape).background(Success.copy(0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.ArrowDownward, null, tint = Success, modifier = Modifier.size(14.dp))
                                }
                                Spacer(Modifier.width(6.dp))
                                Text("আয়", color = Success, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(formatTaka(hisab.income), color = TealPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Text("—  আজ  —", color = InkMuted, fontSize = 11.sp)
                        }
                        Box(Modifier.width(1.dp).height(70.dp).background(InkMuted.copy(0.18f)))
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(26.dp).clip(CircleShape).background(Danger.copy(0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.ArrowUpward, null, tint = Danger, modifier = Modifier.size(14.dp))
                                }
                                Spacer(Modifier.width(6.dp))
                                Text("খরচ", color = Danger, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(formatTaka(hisab.expense), color = Danger, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Text("—  আজ  —", color = InkMuted, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                SectionHead("নতুন এন্ট্রি")
                Row {
                    FilterChip(!isIncome, { isIncome = false }, { Text("খরচ") })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(isIncome, { isIncome = true }, { Text("আয়") })
                }
                Spacer(Modifier.height(6.dp))
                LurkField(amount, { amount = it.filter { c -> c.isDigit() || c == '.' } }, "পরিমাণ (৳)")
                Spacer(Modifier.height(6.dp))
                LurkField(category, { category = it }, "ক্যাটাগরি")
                Spacer(Modifier.height(6.dp))
                LurkField(channel, { channel = it }, "মাধ্যম")
                Spacer(Modifier.height(8.dp))
                PrimaryButton("সেভ করুন") {
                    val v = amount.toDoubleOrNull() ?: return@PrimaryButton
                    if (isIncome) vm.addIncome(v, category.ifBlank { "আয়" }, channel.ifBlank { "নগদ" })
                    else vm.addExpense(v, category.ifBlank { "খরচ" }, channel.ifBlank { "নগদ" })
                    amount = ""
                }

                SectionHead("সাম্প্রতিক লেনদেন", action = "সব দেখুন >")
                MockCard {
                    val list = (if (hisab.transactions.isNotEmpty()) hisab.transactions else all).take(6)
                    if (list.isEmpty()) {
                        Text("এখনো কোনো লেনদেন নেই", color = InkMuted)
                    } else {
                        list.forEachIndexed { index, tx ->
                            val (icon, bg) = when {
                                tx.category.contains("বাজার") -> Icons.Outlined.ShoppingBasket to Color(0xFFE8D5C4)
                                tx.category.contains("বেতন") || tx.type == "INCOME" -> Icons.Outlined.WorkOutline to Color(0xFFE8D5A3)
                                tx.category.contains("রিচার্জ") || tx.category.contains("মোবাইল") -> Icons.Outlined.PhoneAndroid to Color(0xFFD6E4F0)
                                else -> Icons.Outlined.AccountBalance to Color(0xFFE0D6F0)
                            }
                            val chipColor = when {
                                tx.channel.contains("bKash", true) || tx.channel.contains("বিকাশ") -> Success
                                tx.channel.contains("Nagad", true) || tx.channel.contains("নগদ") && tx.channel.length > 2 -> Color(0xFF7B5EA7)
                                else -> GoldMuted
                            }
                            TxRow(
                                icon = icon,
                                iconBg = bg,
                                title = tx.category,
                                subtitle = if (tx.type == "INCOME") "আয়" else "খরচ",
                                amount = formatTaka(tx.amount),
                                amountColor = if (tx.type == "INCOME") Success else Danger,
                                channel = tx.channel,
                                channelColor = chipColor,
                                showDivider = index != list.lastIndex
                            )
                        }
                    }
                }
            }
        }
    }
}
