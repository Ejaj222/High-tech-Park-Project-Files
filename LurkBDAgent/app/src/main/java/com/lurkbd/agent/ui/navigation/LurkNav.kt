package com.lurkbd.agent.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

enum class LurkDest(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Home("home", "হোম", Icons.Outlined.Home),
    Hisab("hisab", "হিসাব", Icons.Outlined.AccountBalanceWallet),
    Shop("shop", "দোকান", Icons.Outlined.Storefront),
    Study("study", "পড়াশোনা", Icons.Outlined.MenuBook),
    Reminders("reminders", "রিমাইন্ডার", Icons.Outlined.Alarm)
}
