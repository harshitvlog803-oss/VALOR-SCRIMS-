package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScoreboardEntry
import com.example.ui.theme.*

@Composable
fun ScoreboardTable(
    entries: List<ScoreboardEntry>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GamingCard)
        ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    "Scoreboard will update live once the match starts.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GamingCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF131520))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                Text("PLAYER / TEAM", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("KILLS", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp))
                Text("PTS", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                Text("PRIZE", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(54.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            entries.forEachIndexed { index, entry ->
                val isWinner = entry.rank == 1
                val isTop3 = entry.rank in 2..3

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isWinner -> NeonGold.copy(alpha = 0.15f)
                                isTop3 -> ElectricCyan.copy(alpha = 0.08f)
                                index % 2 == 0 -> Color(0xFF161826)
                                else -> Color.Transparent
                            }
                        )
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank badge
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (entry.rank) {
                                    1 -> NeonGold
                                    2 -> Color(0xFFC0C0C0)
                                    3 -> Color(0xFFCD7F32)
                                    else -> Color(0xFF26293D)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${entry.rank}",
                            color = if (entry.rank <= 3) GamingDarkBackground else TextSecondary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Player IGN & UID
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isWinner) {
                                Text("👑 ", fontSize = 12.sp)
                            }
                            Text(
                                text = entry.playerIgn,
                                color = if (isWinner) NeonGold else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "UID: ${entry.playerUid}",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    // Kills
                    Text(
                        text = "${entry.kills}",
                        color = NeonFireOrangeLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.width(42.dp)
                    )

                    // Total Points
                    Text(
                        text = "${entry.totalPoints}",
                        color = ElectricCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        modifier = Modifier.width(36.dp)
                    )

                    // Prize won
                    Text(
                        text = if (entry.prizeWon > 0) "₹${entry.prizeWon}" else "-",
                        color = if (entry.prizeWon > 0) ElectricGreen else TextTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.width(54.dp)
                    )
                }
            }
        }
    }
}
