package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardPlayer
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.components.ClashXTopBar
import com.example.ui.theme.*

data class PastWinner(
    val ign: String,
    val uid: String,
    val prize: String,
    val tournament: String,
    val date: String,
    val method: String
)

@Composable
fun LeaderboardScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val topPlayers = listOf(
        LeaderboardPlayer(1, "ALPHA_PRO_99", 78, 142, "WILL WIN ₹1,000", "👑", 0xFFFFD700),
        LeaderboardPlayer(2, "RAISTAR_GOD", 65, 128, "WILL WIN ₹500", "🥈", 0xFFC0C0C0),
        LeaderboardPlayer(3, "KILLER_CHAMP", 59, 114, "WILL WIN ₹250", "🥉", 0xFFCD7F32),
        LeaderboardPlayer(4, "GOKU_FF_WAR", 48, 98, "WILL WIN ₹100", "⚡", 0xFF00E5FF),
        LeaderboardPlayer(5, "VIPER_DEADLY", 44, 91, "WILL WIN ₹100", "🔥", 0xFFFF5722),
        LeaderboardPlayer(6, "NINJA_SHADOW", 39, 84, "WILL WIN ₹100", "🎯", 0xFF00E676),
        LeaderboardPlayer(7, "PRO_HEADSHOT", 36, 76, "WILL WIN ₹100", "💀", 0xFFE040FB),
        LeaderboardPlayer(8, "BLADE_RUNNER", 33, 71, "WILL WIN ₹100", "🛡️", 0xFFFF9800),
        LeaderboardPlayer(9, "STORM_SURVIVOR", 29, 65, "WILL WIN ₹100", "⚡", 0xFF29B6F6),
        LeaderboardPlayer(10, "CYBER_SNIPER", 25, 58, "WILL WIN ₹100", "🎯", 0xFFFF4081)
    )

    val pastWinners = listOf(
        PastWinner("RAISTAR_X", "2948192847", "₹1,000", "🔥 Bermuda Squad Championship", "Yesterday", "Transferred to Vault"),
        PastWinner("KILLER_BOY", "1948291048", "₹500", "⚡ Clash Squad Knockout Finale", "2 Days Ago", "Paid via FamPay UPI"),
        PastWinner("ALPHA_PRO_99", "2849102847", "₹350", "🏆 Kalahari Solo Cash Battle", "3 Days Ago", "Transferred to Vault"),
        PastWinner("VIPER_99", "3948192039", "₹250", "🛡️ Purgatory Duo Scrims #8", "4 Days Ago", "Paid via UPI")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
    ) {
        ClashXTopBar(
            title = "Leaderboard & Prize Winners",
            onBackClick = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Prize Distribution Card (Who Will Win Prizes)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(NeonGold, NeonFireOrange))
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏆", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "WHO WILL WIN PRIZES",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        "Total Weekly Cash Pool: ₹2,500",
                                        color = NeonGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Surface(
                                color = NeonFireOrange,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "ENDS IN 2D 14H",
                                    color = TextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Podium preview (1st, 2nd, 3rd)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RewardTierBox(
                                rank = "1st Place",
                                reward = "₹1,000",
                                badge = "👑 Crown + Trophy",
                                color = NeonGold,
                                modifier = Modifier.weight(1f)
                            )
                            RewardTierBox(
                                rank = "2nd Place",
                                reward = "₹500",
                                badge = "🥈 Silver Cup",
                                color = Color(0xFFD0D0D0),
                                modifier = Modifier.weight(1f)
                            )
                            RewardTierBox(
                                rank = "3rd Place",
                                reward = "₹250",
                                badge = "🥉 Bronze Shield",
                                color = Color(0xFFCD7F32),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4th to 10th Place & Fragger Bonus
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141726))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Rank 4th - 10th Place:",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                "Each Wins ₹100 Cash",
                                color = ElectricGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141726))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Top Fragger (Most Kills):",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                "Wins ₹300 Bonus Prize",
                                color = NeonFireOrangeLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "All prizes automatically transferred to player's in-app vault on Sunday 10 PM IST!",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Section: Current Players & Who Will Win Prizes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Live Leaderboard Standings",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            "Players currently winning weekly cash prizes",
                            color = ElectricGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text("Points = Kills + Rank", color = TextTertiary, fontSize = 11.sp)
                }
            }

            // Leaderboard entries
            items(topPlayers) { player ->
                val isTop3 = player.rank <= 3
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard),
                    border = if (isTop3) CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(player.avatarColor).copy(alpha = 0.6f),
                                Color(0xFF141724)
                            )
                        )
                    ) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(player.avatarColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${player.rank}",
                                color = GamingDarkBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Player details & Badge
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = player.ign,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(player.badgeEmoji, fontSize = 13.sp)
                            }
                            Text(
                                text = "${player.kills} Kills • ${player.points} Total Points",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Prize Pill ("WILL WIN ₹1,000")
                        Surface(
                            color = if (isTop3) NeonGold.copy(alpha = 0.15f) else Color(0xFF151827),
                            shape = RoundedCornerShape(8.dp),
                            border = if (isTop3) CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(NeonGold, NeonFireOrange))
                            ) else null
                        ) {
                            Text(
                                text = player.prize,
                                color = if (player.rank == 1) NeonGold else if (player.rank == 2) Color(0xFFE0E0E0) else if (player.rank == 3) Color(0xFFCD7F32) else ElectricGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Section: Past Winners & Distributed Prizes
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Recent Winners & Payouts Distributed",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Past champions with verified cash payouts",
                            color = NeonGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            items(pastWinners) { winner ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121422))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🥇", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(winner.ign, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = Color(0xFF1D2032), shape = RoundedCornerShape(4.dp)) {
                                        Text(winner.date, color = TextTertiary, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Text(winner.tournament, color = TextSecondary, fontSize = 11.sp)
                                Text("UID: ${winner.uid} • ${winner.method}", color = ElectricGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Surface(
                            color = ElectricGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                winner.prize,
                                color = ElectricGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardTierBox(
    rank: String,
    reward: String,
    badge: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF151824),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.6f), Color.Transparent)))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(rank, color = color, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(reward, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(badge, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
