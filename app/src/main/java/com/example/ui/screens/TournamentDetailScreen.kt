package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.TournamentStatus
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.components.BracketView
import com.example.ui.components.ScoreboardTable
import com.example.ui.theme.*

import com.example.ui.components.ClashXTopBar
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val tournaments by viewModel.filteredTournaments.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val profile by viewModel.profile.collectAsState()

    val tournament = tournaments.find { it.id == tournamentId }

    if (tournament == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tournament not found", color = TextSecondary)
        }
        return
    }

    val isUserBooked = bookings.any { it.tournamentId == tournament.id && it.playerUid == profile.uid }
    val userBooking = bookings.find { it.tournamentId == tournament.id && it.playerUid == profile.uid }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = buildList {
        add("Overview")
        add("Rules & Fair Play")
        add("Scoreboard")
        if (tournament.gameMode == GameMode.CLASH_SQUAD || tournament.bracketMatches.isNotEmpty()) {
            add("Bracket")
        }
        add("Slots (${tournament.bookedSlots}/${tournament.maxSlots})")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
    ) {
        // Top App Bar
        ClashXTopBar(
            title = "Tournament Details",
            onBackClick = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(listOf(NeonFireOrange.copy(alpha = 0.5f), BorderDark))
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = NeonFireOrange.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    tournament.gameMode.displayName,
                                    color = NeonFireOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                "MAP: ${tournament.mapType.displayName.uppercase()}",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            tournament.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = NeonGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(tournament.matchTime, color = NeonGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF13141F))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOTAL PRIZE", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("₹${tournament.prizePool}", color = NeonGold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PER KILL", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("₹${tournament.perKillPrize}", color = NeonFireOrangeLight, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ENTRY FEE", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("₹${tournament.entryFee}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            // CRITICAL REQUIREMENT: Room ID & Password Protection
            // "Tournament id pass only for booking players"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUserBooked) Color(0xFF13201C) else Color(0xFF1D1418)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (isUserBooked) listOf(ElectricGreen, ElectricCyan) else listOf(DangerRed, BorderDark)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isUserBooked) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isUserBooked) ElectricGreen else DangerRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Custom Room Credentials",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            if (isUserBooked) {
                                Surface(
                                    color = ElectricGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "SLOT #${userBooking?.slotNumber ?: 1} BOOKED",
                                        color = ElectricGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isUserBooked) {
                            // Room ID & Pass ONLY for booked players!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Room ID Box
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Room ID", tournament.roomId))
                                            Toast.makeText(context, "Room ID copied: ${tournament.roomId}", Toast.LENGTH_SHORT).show()
                                        },
                                    color = Color(0xFF1B2A24)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("ROOM ID", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                tournament.roomId.ifBlank { "8920147" },
                                                color = ElectricGreen,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp
                                            )
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElectricGreen, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                // Password Box
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Room Password", tournament.roomPassword))
                                            Toast.makeText(context, "Password copied: ${tournament.roomPassword}", Toast.LENGTH_SHORT).show()
                                        },
                                    color = Color(0xFF1B2A24)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("PASSWORD", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                tournament.roomPassword.ifBlank { "cx99" },
                                                color = NeonGold,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp
                                            )
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonGold, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Launch Free Fire Button
                            Button(
                                onClick = {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage("com.dts.freefireth")
                                        ?: context.packageManager.getLaunchIntentForPackage("com.dts.freefiremax")
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    } else {
                                        Toast.makeText(context, "Free Fire not installed on device. Copy credentials and enter in game!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GamingDarkBackground),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Free Fire & Enter Custom Room", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // User is NOT booked!
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "🔒 Room ID & Password are encrypted.",
                                    color = DangerRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Tournament ID & Pass are only visible to booking players!\nPlease book your slot to unlock room access.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Tab Selector
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = GamingDarkBackground,
                    contentColor = NeonFireOrange,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = NeonFireOrange,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    tabTitle,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) TextPrimary else TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (tabs[selectedTabIndex]) {
                "Overview" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = GamingCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Official Tournament Rules", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                tournament.rules.forEachIndexed { idx, rule ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text("${idx + 1}. ", color = NeonFireOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(rule, color = TextSecondary, fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = BorderDark)
                                Spacer(modifier = Modifier.height(16.dp))

                                Text("Scoring System (Official Esports)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("• 1st Place (Booyah): 12 Points", color = TextSecondary, fontSize = 13.sp)
                                Text("• 2nd Place: 9 Points", color = TextSecondary, fontSize = 13.sp)
                                Text("• 3rd Place: 8 Points", color = TextSecondary, fontSize = 13.sp)
                                Text("• 4th - 10th Place: 7 to 1 Points", color = TextSecondary, fontSize = 13.sp)
                                Text("• Each Kill: 1 Point (Plus cash prize ₹${tournament.perKillPrize} per kill)", color = NeonGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                "Rules & Fair Play" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = GamingCard),
                            shape = RoundedCornerShape(16.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(NeonFireOrange, NeonGold))
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📜", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            "Official Tournament Regulations",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            "${tournament.gameMode.displayName} • Map: ${tournament.mapType.displayName}",
                                            color = NeonFireOrange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    color = NeonFireOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "⚠️ Violating any tournament rule will result in immediate disqualification, forfeiture of entry fee, and zero prize payout.",
                                        color = NeonFireOrange,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("All Match Rules & Regulations:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(10.dp))

                                tournament.rules.forEachIndexed { idx, rule ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF131524))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            color = NeonGold.copy(alpha = 0.2f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("${idx + 1}", color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            rule,
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = BorderDark)
                                Spacer(modifier = Modifier.height(16.dp))

                                Text("Anti-Cheat & Dispute Policy", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "• Any use of third-party tools, mod APKs, configs, emulators, or antenna hacks leads to permanent UID ban.\n• Players must submit screenshot proof to Admin within 15 minutes of match conclusion if there is any dispute.\n• Match referee decision is 100% final and binding.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                "Scoreboard" -> {
                    item {
                        ScoreboardTable(entries = tournament.scoreboards)
                    }
                }

                "Bracket" -> {
                    item {
                        BracketView(matches = tournament.bracketMatches)
                    }
                }

                else -> {
                    // Slots List
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = GamingCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "Registered Players (${tournament.bookedSlots}/${tournament.maxSlots})",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                for (slot in 1..tournament.maxSlots) {
                                    val isFilled = slot <= tournament.bookedSlots
                                    val isThisUserSlot = userBooking?.slotNumber == slot
                                    val bookedEntry = tournament.bookedPlayers.find { it.slotNumber == slot }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isThisUserSlot) ElectricGreen.copy(alpha = 0.2f)
                                                else if (isFilled) Color(0xFF141724)
                                                else Color(0xFF0F1018)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = if (isThisUserSlot) ElectricGreen else if (isFilled) NeonGold.copy(alpha = 0.2f) else Color(0xFF222538),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "Slot #$slot",
                                                    color = if (isThisUserSlot) GamingDarkBackground else if (isFilled) NeonGold else TextTertiary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            if (isThisUserSlot) {
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text("👑 YOU (${profile.ign})", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                                    }
                                                    Text("UID: ${profile.uid} • Booked", color = ElectricGreen.copy(alpha = 0.8f), fontSize = 10.sp)
                                                }
                                            } else if (bookedEntry != null) {
                                                Column {
                                                    Text(bookedEntry.playerIgn, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    Text("UID: ${bookedEntry.playerUid}", color = TextTertiary, fontSize = 10.sp)
                                                }
                                            } else if (isFilled) {
                                                Text("Registered Player #$slot", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            } else {
                                                Text("Available Slot", color = TextTertiary, fontSize = 12.sp)
                                            }
                                        }

                                        if (isThisUserSlot) {
                                            Surface(
                                                color = ElectricGreen.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("CONFIRMED", color = ElectricGreen, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(4.dp))
                                            }
                                        } else if (isFilled) {
                                            Text("Booked", color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        } else {
                                            Text("Open", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
