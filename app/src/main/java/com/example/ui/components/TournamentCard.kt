package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.ui.theme.*

@Composable
fun TournamentCard(
    tournament: Tournament,
    isBooked: Boolean,
    bookedSlotNumber: Int?,
    vaultBalance: Int,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (tournament.bookedSlots.toFloat() / tournament.maxSlots.toFloat()).coerceIn(0f, 1f)
    val isFull = tournament.bookedSlots >= tournament.maxSlots
    var showRulesDialog by remember { mutableStateOf(false) }

    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            containerColor = GamingCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📜", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Tournament Rules",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                        Text(
                            "${tournament.gameMode.displayName} • ${tournament.mapType.displayName}",
                            color = NeonFireOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 380.dp)) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Surface(
                                color = NeonFireOrange.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "⚡ Please read all tournament regulations before entering the custom room!",
                                    color = NeonFireOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        items(tournament.rules) { rule ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("• ", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(rule, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRulesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange, contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = NeonFireOrange.copy(alpha = 0.3f))
            .clickable { onCardClick() }
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GamingCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    if (isBooked) ElectricGreen.copy(alpha = 0.6f) else BorderDark,
                    Color(0xFF13141F)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Badges & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Game Mode Badge
                    Surface(
                        color = when (tournament.gameMode) {
                            GameMode.LONE_WOLF_1V1_BODY, GameMode.LONE_WOLF_1V1_HEAD -> NeonFireOrange.copy(alpha = 0.2f)
                            GameMode.CS_1V1_HEAD_UNLIMITED, GameMode.CS_2V2_BODY, GameMode.CS_1V1_BODY, GameMode.CLASH_SQUAD -> ElectricCyan.copy(alpha = 0.2f)
                            GameMode.SOLO_PER_KILL, GameMode.DUO_PER_KILL -> DangerRed.copy(alpha = 0.2f)
                            GameMode.LONE_WOLF_LOSS_TO_WIN -> NeonGold.copy(alpha = 0.2f)
                            GameMode.SQUAD -> ElectricGreen.copy(alpha = 0.2f)
                            GameMode.DUO -> NeonGold.copy(alpha = 0.2f)
                            GameMode.SOLO -> Color(0xFF7E57C2).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tournament.gameMode.displayName,
                            color = when (tournament.gameMode) {
                                GameMode.LONE_WOLF_1V1_BODY, GameMode.LONE_WOLF_1V1_HEAD -> NeonFireOrange
                                GameMode.CS_1V1_HEAD_UNLIMITED, GameMode.CS_2V2_BODY, GameMode.CS_1V1_BODY, GameMode.CLASH_SQUAD -> ElectricCyan
                                GameMode.SOLO_PER_KILL, GameMode.DUO_PER_KILL -> DangerRed
                                GameMode.LONE_WOLF_LOSS_TO_WIN -> NeonGold
                                GameMode.SQUAD -> ElectricGreen
                                GameMode.DUO -> NeonGold
                                GameMode.SOLO -> Color(0xFFB388FF)
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Map Badge
                    Surface(
                        color = Color(0xFF23263A),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🗺️ ${tournament.mapType.displayName}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Rules Quick Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showRulesDialog = true },
                        color = Color(0xFF281F38),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📜 Rules", color = NeonGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Match Status Pill
                val statusColor = when (tournament.status) {
                    TournamentStatus.LIVE -> DangerRed
                    TournamentStatus.OPEN -> ElectricGreen
                    TournamentStatus.UPCOMING -> NeonGold
                    TournamentStatus.COMPLETED -> TextSecondary
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = tournament.status.label.uppercase(),
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tournament Title
            Text(
                text = tournament.title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Match Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tournament.matchTime,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Column Prize & Entry Metric Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF13141F))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Total Prize Pool
                Column(horizontalAlignment = Alignment.Start) {
                    Text("PRIZE POOL", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("₹${tournament.prizePool}", color = NeonGold, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }

                // Per Kill
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PER KILL", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("₹${tournament.perKillPrize}", color = NeonFireOrangeLight, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }

                // Entry Fee
                Column(horizontalAlignment = Alignment.End) {
                    Text("ENTRY FEE", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("₹${tournament.entryFee}", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slots Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "👥 ${tournament.bookedSlots}/${tournament.maxSlots} Booked",
                        color = NeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• View Players",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "${tournament.maxSlots - tournament.bookedSlots} Slots Left",
                    color = if (isFull) DangerRed else ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isFull) DangerRed else NeonFireOrange,
                trackColor = Color(0xFF23263A)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Booking / Action Button
            if (isBooked) {
                // User has booked!
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ElectricGreen.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ElectricGreen, ElectricCyan)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "SLOT #${bookedSlotNumber ?: 1} BOOKED",
                                color = ElectricGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            "View Room ID ➔",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else if (tournament.status == TournamentStatus.COMPLETED) {
                Button(
                    onClick = { onCardClick() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23263A))
                ) {
                    Text("View Results & Scoreboard", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            } else if (isFull) {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23263A))
                ) {
                    Text("MATCH FULL", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { onBookClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_button_${tournament.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonFireOrange,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "BOOK SLOT (₹${tournament.entryFee})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
