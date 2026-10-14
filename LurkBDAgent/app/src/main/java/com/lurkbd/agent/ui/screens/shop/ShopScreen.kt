package com.lurkbd.agent.ui.screens.shop

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.lurkbd.agent.util.formatTaka

@Composable
fun ShopScreen(vm: LurkViewModel) {
    val customers by vm.customers.collectAsState()
    val products by vm.products.collectAsState()
    var customerName by remember { mutableStateOf("") }
    var creditAmount by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }

    ScreenScaffold(title = "দোকান", subtitle = "বাকি খাতা ও স্টক") {
        androidx.compose.foundation.layout.Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            SectionLabel("বাকি")
            customers.forEach { c ->
                ListRow(
                    title = c.name,
                    subtitle = if (c.phone.isBlank()) "ক্রেতা" else c.phone,
                    trailing = formatTaka(c.balance),
                    highlight = c.balance > 0
                )
            }
            Spacer(Modifier.height(8.dp))
            LurkField(customerName, { customerName = it }, "ক্রেতার নাম")
            Spacer(Modifier.height(8.dp))
            LurkField(creditAmount, { creditAmount = it.filter { ch -> ch.isDigit() || ch == '.' } }, "বাকি যোগ (৳)")
            Spacer(Modifier.height(8.dp))
            PrimaryButton("বাকি আপডেট") {
                val amount = creditAmount.toDoubleOrNull() ?: return@PrimaryButton
                if (customerName.isBlank()) return@PrimaryButton
                vm.addCustomerCredit(customerName.trim(), amount)
                customerName = ""
                creditAmount = ""
            }

            SectionLabel("স্টক")
            products.forEach { p ->
                val low = p.qty <= p.lowThreshold
                ListRow(
                    title = p.name,
                    subtitle = if (low) "কম স্টক — অ্যালার্ট" else p.unit,
                    trailing = "${p.qty} ${p.unit}",
                    highlight = low
                )
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
            Spacer(Modifier.height(12.dp))
            Text(
                "স্টক থ্রেশহোল্ডের নিচে গেলে হাইলাইট ও অ্যালার্ট দেখাবে।",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }
    }
}
