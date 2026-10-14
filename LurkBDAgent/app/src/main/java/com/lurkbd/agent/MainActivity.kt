package com.lurkbd.agent

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.GoldMicFab
import com.lurkbd.agent.ui.navigation.LurkDest
import com.lurkbd.agent.ui.screens.hisab.HisabScreen
import com.lurkbd.agent.ui.screens.home.HomeScreen
import com.lurkbd.agent.ui.screens.reminders.RemindersScreen
import com.lurkbd.agent.ui.screens.shop.ShopScreen
import com.lurkbd.agent.ui.screens.study.StudyScreen
import com.lurkbd.agent.ui.screens.voice.VoiceOverlay
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.LurkBDTheme
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealPrimary

class MainActivity : ComponentActivity() {
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermissions()
        enableEdgeToEdge()
        val app = application as LurkBDApp
        setContent {
            LurkBDTheme {
                val vm: LurkViewModel = viewModel(factory = LurkViewModel.factory(app))
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val current = backStack?.destination?.route
                val darkNav = current == LurkDest.Home.route || current == LurkDest.Reminders.route

                Box(Modifier.fillMaxSize()) {
                    Scaffold(
                        containerColor = if (darkNav) TealDark else SandSurface,
                        bottomBar = {
                            NavigationBar(
                                containerColor = if (darkNav) TealPrimary else Color.White,
                                tonalElevation = 0.dp
                            ) {
                                LurkDest.entries.forEach { dest ->
                                    val selected = current == dest.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(dest.route) {
                                                popUpTo(LurkDest.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                                        label = { Text(dest.label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = GoldAccent,
                                            selectedTextColor = GoldAccent,
                                            indicatorColor = GoldAccent.copy(alpha = 0.18f),
                                            unselectedIconColor = if (darkNav) Color.White.copy(0.75f) else TealPrimary.copy(0.7f),
                                            unselectedTextColor = if (darkNav) Color.White.copy(0.75f) else InkMuted
                                        )
                                    )
                                }
                            }
                        },
                        floatingActionButton = {
                            GoldMicFab(onClick = { vm.openVoice() })
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = LurkDest.Home.route,
                            modifier = Modifier.padding(padding)
                        ) {
                            composable(LurkDest.Home.route) { HomeScreen(vm) { vm.openVoice() } }
                            composable(LurkDest.Hisab.route) { HisabScreen(vm) }
                            composable(LurkDest.Shop.route) { ShopScreen(vm) }
                            composable(LurkDest.Study.route) { StudyScreen(vm) }
                            composable(LurkDest.Reminders.route) { RemindersScreen(vm) }
                        }
                    }
                    VoiceOverlay(vm)
                }
            }
        }
    }

    private fun requestRuntimePermissions() {
        val needed = buildList {
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.RECORD_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.CAMERA)
            }
        }
        if (needed.isNotEmpty()) permissionLauncher.launch(needed.toTypedArray())
    }
}
