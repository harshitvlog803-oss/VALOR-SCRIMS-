package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TournamentViewModel = viewModel()
            val isWhiteTheme by viewModel.isWhiteBackground.collectAsState()
            MyApplicationTheme(isWhiteTheme = isWhiteTheme) {
                ValorScrimsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ValorScrimsApp(
    viewModel: TournamentViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isWhiteTheme by viewModel.isWhiteBackground.collectAsState()

    // Listen to toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Determine active bottom nav tab
    val selectedNavIndex = when (currentScreen) {
        is Screen.Home -> 0
        is Screen.Wallet -> 1
        is Screen.Leaderboard -> 2
        is Screen.Profile -> 3
        is Screen.Support -> 4
        else -> -1
    }

    val showBottomBar = currentScreen !is Screen.TournamentDetail &&
            currentScreen !is Screen.Admin &&
            currentScreen !is Screen.Login &&
            currentScreen !is Screen.OrganiseTournament

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = appBackground(isWhiteTheme),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = if (isWhiteTheme) GamingWhiteCard else Color(0xFF10121D),
                    contentColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = selectedNavIndex == 0,
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        icon = {
                            Icon(Icons.Default.SportsEsports, contentDescription = "Tournaments")
                        },
                        label = { Text("Matches", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GamingDarkBackground,
                            selectedTextColor = NeonFireOrange,
                            indicatorColor = NeonFireOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_matches")
                    )

                    NavigationBarItem(
                        selected = selectedNavIndex == 1,
                        onClick = { viewModel.navigateTo(Screen.Wallet) },
                        icon = {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Vault")
                        },
                        label = { Text("Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GamingDarkBackground,
                            selectedTextColor = NeonGold,
                            indicatorColor = NeonGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_vault")
                    )

                    NavigationBarItem(
                        selected = selectedNavIndex == 2,
                        onClick = { viewModel.navigateTo(Screen.Leaderboard) },
                        icon = {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Leaderboard")
                        },
                        label = { Text("Rewards", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GamingDarkBackground,
                            selectedTextColor = NeonFireOrange,
                            indicatorColor = NeonFireOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_rewards")
                    )

                    NavigationBarItem(
                        selected = selectedNavIndex == 3,
                        onClick = { viewModel.navigateTo(Screen.Profile) },
                        icon = {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GamingDarkBackground,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )

                    NavigationBarItem(
                        selected = selectedNavIndex == 4,
                        onClick = { viewModel.navigateTo(Screen.Support) },
                        icon = {
                            Icon(Icons.Default.HeadsetMic, contentDescription = "Support")
                        },
                        label = { Text("Support", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GamingDarkBackground,
                            selectedTextColor = ElectricGreen,
                            indicatorColor = ElectricGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_support")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GamingDarkBackground)
        ) {
            when (val screen = currentScreen) {
                is Screen.Login -> LoginScreen(viewModel = viewModel)
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.TournamentDetail -> TournamentDetailScreen(tournamentId = screen.tournamentId, viewModel = viewModel)
                is Screen.Wallet -> WalletScreen(viewModel = viewModel)
                is Screen.Leaderboard -> LeaderboardScreen(viewModel = viewModel)
                is Screen.Profile -> ProfileScreen(viewModel = viewModel)
                is Screen.Support -> SupportScreen(viewModel = viewModel)
                is Screen.Admin -> AdminPanelScreen(viewModel = viewModel)
                is Screen.OrganiseTournament -> OrganiseTournamentScreen(viewModel = viewModel)
            }
        }
    }
}
