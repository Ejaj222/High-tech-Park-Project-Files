package com.lurkbd.agent.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.AvatarBadge
import com.lurkbd.agent.ui.components.IconBadge
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.MockCard
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenPad
import com.lurkbd.agent.ui.components.SectionHead
import com.lurkbd.agent.ui.components.WavyTealHeader
import com.lurkbd.agent.ui.theme.Danger
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.Ink
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.ui.theme.TealPrimary
import com.lurkbd.agent.util.formatTaka

@Composable
fun ShopScreen(vm: LurkViewModel) {
    val customers by vm.customers.collectAsState()
    val products by vm.products.collectAsState()
    var customerName by remember { mutableStateOf("") }
    var creditAmount by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(SandSurface)) {
        WavyTealHeader(title = "দোকান", titleColor = GoldAccent, wavy = true)
        ScreenPad {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                SectionHead("১. বাকি (গ্রাহকের পাওনা)", leadingIcon = Icons.Outlined.Person)
                MockCard {
                    customers.forEachIndexed { i, c ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarBadge(c.name.take(1))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                c.name,
                                modifier = Modifier.weight(1f),
                                color = Ink,
                                fontWeight = FontWeight.SemiBold
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatTaka(c.balance), color = Ink, fontWeight = FontWeight.Bold)
                                Text("বাকি", color = InkMuted, fontSize = 12.sp)
                            }
                        }
                        if (i != customers.lastIndex) {
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFE8DFD0))
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                LurkField(customerName, { customerName = it }, "ক্রেতার নাম")
                Spacer(Modifier.height(6.dp))
                LurkField(
                    creditAmount,
                    { creditAmount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    "বাকি যোগ (৳)"
                )
                Spacer(Modifier.height(8.dp))
                PrimaryButton("বাকি আপডেট") {
                    val amount = creditAmount.toDoubleOrNull() ?: return@PrimaryButton
                    if (customerName.isBlank()) return@PrimaryButton
                    vm.addCustomerCredit(customerName.trim(), amount)
                    customerName = ""
                    creditAmount = ""
                }

                SectionHead("২. স্টক (মজুত)", leadingIcon = Icons.Outlined.Inventory2)
                products.forEach { p ->
                    val low = p.qty <= p.lowThreshold
                    Spacer(Modifier.height(8.dp))
                    MockCard(highlight = low) {
                        StockRow(
                            name = p.name,
                            qty = "${p.qty.toInt()} ${p.unit}",
                            low = low
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                LurkField(productName, { productName = it }, "পণ্যের নাম")
                Spacer(Modifier.height(6.dp))
                LurkField(qty, { qty = it.filter { ch -> ch.isDigit() || ch == '.' } }, "পরিমাণ")
                Spacer(Modifier.height(8.dp))
                PrimaryButton("স্টক সেট") {
                    val q = qty.toDoubleOrNull() ?: return@PrimaryButton
                    if (productName.isBlank()) return@PrimaryButton
                    vm.setStock(productName.trim(), q)
                    productName = ""
                    qty = ""
                }
            }
        }
    }
}

@Composable
private fun StockRow(name: String, qty: String, low: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconBadge(
            icon = if (low) Icons.Outlined.WarningAmber else Icons.Outlined.Inventory2,
            bg = if (low) GoldAccent else Color(0xFFD7EBE7),
            tint = if (low) Color.White else TealPrimary
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = Ink, fontWeight = FontWeight.SemiBold)
            Text(
                if (low) {
                    if (name.contains("সাবান")) "⚠ সাবান শেষ হতে যাচ্ছে" else "⚠ কম স্টক — অ্যালার্ট"
                } else {
                    "(মজুত)"
                },
                color = if (low) Danger else InkMuted,
                fontSize = 12.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(qty, color = if (low) Danger else Ink, fontWeight = FontWeight.Bold)
            if (!low) Text("(মজুত)", color = InkMuted, fontSize = 11.sp)
        }
    }
}
