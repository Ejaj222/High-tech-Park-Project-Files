package com.lurkbd.agent.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.unit.dp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.AvatarCircle
import com.lurkbd.agent.ui.components.ListRowRich
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenBody
import com.lurkbd.agent.ui.components.SectionTitle
import com.lurkbd.agent.ui.components.SoftCard
import com.lurkbd.agent.ui.components.TealAppHeader
import com.lurkbd.agent.ui.theme.Danger
import com.lurkbd.agent.ui.theme.GoldAccent
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
        TealAppHeader(title = "দোকান")
        ScreenBody(Modifier.verticalScroll(rememberScrollState())) {
            SectionTitle("১. বাকি (গ্রাহকের পাওনা)", icon = Icons.Outlined.People)
            SoftCard {
                customers.forEach { c ->
                    ListRowRich(
                        leading = { AvatarCircle(c.name.take(1)) },
                        title = c.name,
                        subtitle = "বাকি",
                        trailing = formatTaka(c.balance),
                        trailingColor = GoldAccent,
                        highlight = c.balance > 0
                    )
                }
                if (customers.isEmpty()) Text("কোনো বাকি নেই", color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            LurkField(customerName, { customerName = it }, "ক্রেতার নাম")
            Spacer(Modifier.height(8.dp))
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

            SectionTitle("২. স্টক (মজুত)", icon = Icons.Outlined.Inventory2)
            SoftCard {
                products.forEach { p ->
                    val low = p.qty <= p.lowThreshold
                    ListRowRich(
                        leading = {
                            BoxIcon(
                                if (low) Icons.Outlined.WarningAmber else Icons.Outlined.Inventory2,
                                if (low) GoldAccent else TealPrimary
                            )
                        },
                        title = p.name,
                        subtitle = if (low) "সাবান শেষ হতে যাচ্ছে".takeIf { p.name.contains("সাবান") }
                            ?: "কম স্টক — অ্যালার্ট" else "(মজুত)",
                        trailing = "${p.qty.toInt()} ${p.unit}",
                        trailingColor = if (low) Danger else TealPrimary,
                        highlight = low
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            LurkField(productName, { productName = it }, "পণ্যের নাম")
            Spacer(Modifier.height(8.dp))
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

@Composable
private fun BoxIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    androidx.compose.foundation.layout.Box(
        Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint)
    }
}
