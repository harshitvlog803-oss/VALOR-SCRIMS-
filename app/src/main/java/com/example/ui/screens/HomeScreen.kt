package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BookingResult
import com.example.data.OwnerRole
import com.example.model.GameMode
import com.example.model.Tournament
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.components.TournamentCard
import com.example.ui.components.FamPayTopUpDialog
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.filteredTournaments.collectAsState()
    val allTournamentsList by viewModel.repository.tournaments.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val selectedFilter by viewModel.selectedModeFilter.collectAsState()
    val activeRole by viewModel.activeOwnerRole.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isWhiteTheme by viewModel.isWhiteBackground.collectAsState()

    var bookingDialogTournament by remember { mutableStateOf<Tournament?>(null) }
    var topUpDialogTournament by remember { mutableStateOf<Tournament?>(null) }
    var showGeneralTopUpDialog by remember { mutableStateOf(false) }
    var insufficientBalanceDialogData by remember { mutableStateOf<Pair<Int, Int>?>(null) } // Pair(balance, required)
    var inputIgn by remember { mutableStateOf(profile.ign) }
    var inputUid by remember { mutableStateOf(profile.uid) }

    var showOnlyMyBooked by remember { mutableStateOf(false) }
    var showAllBookedPlayersDialog by remember { mutableStateOf(false) }
    var showAllTournamentRulesDialog by remember { mutableStateOf(false) }
    var rulesSelectedMode by remember { mutableStateOf(GameMode.LONE_WOLF_1V1_BODY) }
    var showOrganiseAuthDialog by remember { mutableStateOf(false) }
    var authSelectedRole by remember { mutableStateOf(OwnerRole.OWNER_1) }
    var authPinInput by remember { mutableStateOf("") }

    val displayedTournaments = remember(tournaments, showOnlyMyBooked, bookings, profile.uid) {
        if (showOnlyMyBooked) {
            tournaments.filter { tourn ->
                bookings.any { it.tournamentId == tourn.id && it.playerUid == profile.uid }
            }
        } else {
            tournaments
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appBackground(isWhiteTheme))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 84.dp)
        ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand logo & title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonFireOrange, NeonGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "VALOR ",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "SCRIMS",
                                color = NeonFireOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            "Free Fire Esports Tournaments",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Header Action: Wallet Chip & Admin Link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Vault Balance Chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.navigateTo(Screen.Wallet) }
                            .testTag("header_wallet_chip"),
                        color = Color(0xFF1B1D2C),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(NeonGold, NeonFireOrange))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = "Vault",
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "₹${profile.vaultBalance}",
                                color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "+",
                                color = NeonFireOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // App Background White / Dark Theme Toggle
                    IconButton(
                        onClick = { viewModel.toggleAppTheme() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isWhiteTheme) Color(0xFFE2E8F0) else Color(0xFF1E2132))
                            .testTag("theme_toggle_button")
                    ) {
                        Text(if (isWhiteTheme) "🌙" else "⚪", fontSize = 16.sp)
                    }
                }
            }
        }

        // Hero Championship Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GamingCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    // Try to load hero image or render gaming gradient
                    val heroResId = try {
                        R.drawable.ic_tournament_hero
                    } catch (e: Exception) {
                        0
                    }

                    if (heroResId != 0) {
                        Image(
                            painter = painterResource(id = heroResId),
                            contentDescription = "Hero Championship Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Dark gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xCC0C0D14),
                                        Color(0xFF0C0D14)
                                    )
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            color = NeonFireOrange,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "DAILY ESPORTS LEAGUE",
                                color = TextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Join Free Fire Tournaments & Win Real Cash!",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            "Instant Vault Crediting • FamPay QR Support • Secure Payouts",
                            color = NeonGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Quick Mode Filter Chips
        item {
            val filters = listOf(
                null to "All Modes",
                GameMode.LONE_WOLF_1V1_BODY to "🐺 LW 1v1 Body",
                GameMode.LONE_WOLF_1V1_HEAD to "🎯 LW 1v1 Head",
                GameMode.CS_1V1_HEAD_UNLIMITED to "💥 CS 1v1 Head",
                GameMode.CS_2V2_BODY to "⚔️ CS 2v2 Body",
                GameMode.CS_1V1_BODY to "🛡️ CS 1v1 Body",
                GameMode.SOLO_PER_KILL to "☠️ Solo Per Kill",
                GameMode.DUO_PER_KILL to "👥 Duo Per Kill",
                GameMode.LONE_WOLF_LOSS_TO_WIN to "🔄 LW Loss to Win",
                GameMode.SOLO to "Solo",
                GameMode.DUO to "Duo",
                GameMode.SQUAD to "Squad (4v4)",
                GameMode.CLASH_SQUAD to "Clash Squad"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // My Booked Matches Filter Chip
                item {
                    val isMyBookedSelected = showOnlyMyBooked
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                showOnlyMyBooked = !showOnlyMyBooked
                            },
                        color = if (isMyBookedSelected) ElectricGreen else Color(0xFF1B1D2B),
                        shape = RoundedCornerShape(20.dp),
                        border = if (isMyBookedSelected) null else CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
                    ) {
                        Text(
                            text = "🎟️ My Booked (${bookings.size})",
                            color = if (isMyBookedSelected) GamingDarkBackground else ElectricGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                // All Booked Players Filter / Viewer Chip
                item {
                    val totalBookedCount = allTournamentsList.sumOf { it.bookedSlots }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                showAllBookedPlayersDialog = true
                            }
                            .testTag("all_booked_players_chip"),
                        color = Color(0xFF1B1D2B),
                        shape = RoundedCornerShape(20.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonGold, NeonFireOrange)))
                    ) {
                        Text(
                            text = "👥 All Booked Players ($totalBookedCount)",
                            color = NeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                items(filters) { (mode, label) ->
                    val isSelected = !showOnlyMyBooked && selectedFilter == mode
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                showOnlyMyBooked = false
                                viewModel.setModeFilter(mode)
                            },
                        color = if (isSelected) NeonFireOrange else Color(0xFF1B1D2B),
                        shape = RoundedCornerShape(20.dp),
                        border = if (isSelected) null else CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Vault Referral & All Tournament Rules Quick Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Vault Referral Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateToWallet(2) }
                        .testTag("vault_referral_shortcut_button"),
                    color = if (isWhiteTheme) Color(0xFFFAF5FF) else Color(0xFF22152F),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFAB47BC), NeonGold))
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎁", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Vault Referral",
                                color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                "Free ₹15 Cash",
                                color = NeonGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // All Tournament Rules Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showAllTournamentRulesDialog = true }
                        .testTag("all_tournament_rules_button"),
                    color = if (isWhiteTheme) Color(0xFFEFF6FF) else Color(0xFF141A2D),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonFireOrange))
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📜", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Tournament Rules",
                                color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                "All Modes & Guide",
                                color = ElectricCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Available Tournaments + Organise Button (Owner 1 & 2 only)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (showOnlyMyBooked) "My Booked Tournaments (${displayedTournaments.size})"
                        else "Tournaments (${displayedTournaments.size})",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        "Real-Time Updates",
                        color = ElectricGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Organise Option: Restricted strictly to 2nd Owner (hidden for 1st Owner)
                if (activeRole != OwnerRole.OWNER_1) {
                    Button(
                        onClick = {
                            if (activeRole == OwnerRole.OWNER_2) {
                                viewModel.navigateTo(Screen.OrganiseTournament)
                            } else {
                                showOrganiseAuthDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = GamingDarkBackground
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("organise_tournament_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (activeRole == OwnerRole.OWNER_2) "Organise Match" else "Organise (Owner 2)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Tournament Cards
        if (displayedTournaments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard)
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            if (showOnlyMyBooked) "You have not booked any tournaments yet.\nBrowse open tournaments above and book your slot!"
                            else "No tournaments found in this mode. Check back soon!",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayedTournaments, key = { it.id }) { tourn ->
                val isBooked = bookings.any { it.tournamentId == tourn.id && it.playerUid == profile.uid }
                val userBooking = bookings.find { it.tournamentId == tourn.id && it.playerUid == profile.uid }

                TournamentCard(
                    tournament = tourn,
                    isBooked = isBooked,
                    bookedSlotNumber = userBooking?.slotNumber,
                    vaultBalance = profile.vaultBalance,
                    onCardClick = { viewModel.navigateTo(Screen.TournamentDetail(tourn.id)) },
                    onBookClick = {
                        bookingDialogTournament = tourn
                        inputIgn = profile.ign
                        inputUid = profile.uid
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    // Floating Organise Tournament Action Button (2nd Owner only - hidden for 1st Owner)
    if (activeRole != OwnerRole.OWNER_1) {
        ExtendedFloatingActionButton(
            onClick = {
                if (activeRole == OwnerRole.OWNER_2) {
                    viewModel.navigateTo(Screen.OrganiseTournament)
                } else {
                    showOrganiseAuthDialog = true
                }
            },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = GamingDarkBackground) },
            text = {
                Text(
                    text = if (activeRole == OwnerRole.OWNER_2) "Organise Match" else "Organise (Owner 2)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            },
            containerColor = ElectricCyan,
            contentColor = GamingDarkBackground,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_organise_tournament")
        )
    }

    // --- Booking Modal Dialog with Automatic Balance Deduction & Vault Balance Check ---
    bookingDialogTournament?.let { tourn ->
        AlertDialog(
            onDismissRequest = { bookingDialogTournament = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎮 ", fontSize = 20.sp)
                    Text(
                        "Book Tournament Slot",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = tourn.title,
                        color = NeonGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Match: ${tourn.matchTime} • ${tourn.mapType.displayName}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Vault balance review
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF141624),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Your Vault Balance", color = TextTertiary, fontSize = 11.sp)
                                Text(
                                    "₹${profile.vaultBalance}",
                                    color = if (profile.vaultBalance >= tourn.entryFee) ElectricGreen else DangerRed,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Entry Fee", color = TextTertiary, fontSize = 11.sp)
                                Text(
                                    "₹${tourn.entryFee}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Player Free Fire Credentials Input
                    OutlinedTextField(
                        value = inputIgn,
                        onValueChange = { inputIgn = it },
                        label = { Text("Free Fire In-Game Name (IGN)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonFireOrange,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputUid,
                        onValueChange = { inputUid = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Free Fire UID (Numbers only)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonFireOrange,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "🔒 Room ID & Password will be unlocked exclusively for you 15 minutes before match.",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentTourn = bookingDialogTournament ?: return@Button
                        viewModel.bookSlot(
                            tournamentId = currentTourn.id,
                            ign = inputIgn,
                            uid = inputUid
                        ) { result ->
                            bookingDialogTournament = null
                            if (result is BookingResult.InsufficientBalance) {
                                insufficientBalanceDialogData = Pair(result.currentBalance, result.requiredAmount)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonFireOrange,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm & Deduct ₹${tourn.entryFee}", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingDialogTournament = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingCardElevated
        )
    }

    // --- Insufficient Balance Dialog (Automatic Redirection to FamPay QR) ---
    insufficientBalanceDialogData?.let { (curr, req) ->
        AlertDialog(
            onDismissRequest = { insufficientBalanceDialogData = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠️ ", fontSize = 20.sp)
                    Text("Insufficient Balance in Vault", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column {
                    Text(
                        "No balance in players vault no book tournament in app!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Your current vault balance is ₹$curr, but this tournament requires ₹$req.\n\nPlease add payment via our official FamPay QR to instantly book your slot.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        insufficientBalanceDialogData = null
                        viewModel.navigateTo(Screen.Wallet)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Money via FamPay QR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { insufficientBalanceDialogData = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = GamingCardElevated
        )
    }

    // --- Organise Tournament Owner Authorization Dialog (Only for 1st Owner & 2nd Owner) ---
    if (showOrganiseAuthDialog) {
        AlertDialog(
            onDismissRequest = {
                showOrganiseAuthDialog = false
                authPinInput = ""
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔒 ", fontSize = 20.sp)
                    Text(
                        "Tournament Organise Portal",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        "Tournament organise option is only for 1st Owner and 2nd Owner!",
                        color = NeonGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Only 2nd Owner has the authority to organise tournaments and publish matches to VALOR SCRIMS.\n\nEnter 2nd Owner PIN code to unlock:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = authPinInput,
                        onValueChange = { authPinInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Enter 2nd Owner PIN") },
                        placeholder = { Text("••••") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("organise_auth_pin_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.loginOwner(OwnerRole.OWNER_2, authPinInput)
                        if (success) {
                            showOrganiseAuthDialog = false
                            authPinInput = ""
                            viewModel.navigateTo(Screen.OrganiseTournament)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = GamingDarkBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Unlock & Organise", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showOrganiseAuthDialog = false
                    authPinInput = ""
                }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingCardElevated
        )
    }

    // --- All Tournaments & Booked Players Dialog ---
    if (showAllBookedPlayersDialog) {
        AlertDialog(
            onDismissRequest = { showAllBookedPlayersDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👥 ", fontSize = 20.sp)
                    Column {
                        Text(
                            "All Booked Tournament Players",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            "Live register list across all active scrims",
                            color = NeonGold,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allTournamentsList) { t ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(NeonFireOrange.copy(alpha = 0.4f), BorderDark))
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        t.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        color = NeonGold.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "${t.bookedSlots}/${t.maxSlots} Booked",
                                            color = NeonGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${t.gameMode.displayName} • ${t.mapType.displayName} • Entry: ₹${t.entryFee}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = BorderDark)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (t.bookedPlayers.isEmpty()) {
                                    Text(
                                        "No players booked yet. Be the first to book!",
                                        color = TextTertiary,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    t.bookedPlayers.forEach { b ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = Color(0xFF1F2236),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        "Slot #${b.slotNumber}",
                                                        color = NeonFireOrangeLight,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    b.playerIgn,
                                                    color = if (b.playerUid == profile.uid) ElectricGreen else TextPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            Text(
                                                "UID: ${b.playerUid}",
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAllBookedPlayersDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GamingCardElevated
        )
    }

    // --- All Tournament Rules & Fair Play Modal Dialog ---
    if (showAllTournamentRulesDialog) {
        AlertDialog(
            onDismissRequest = { showAllTournamentRulesDialog = false },
            containerColor = GamingCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📜", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Official Tournament Rules",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            "Select any mode to read full esports regulations",
                            color = NeonFireOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            text = {
                val allModesList = listOf(
                    GameMode.LONE_WOLF_1V1_BODY to "🐺 LW 1v1 Body",
                    GameMode.LONE_WOLF_1V1_HEAD to "🎯 LW 1v1 Head",
                    GameMode.CS_1V1_HEAD_UNLIMITED to "💥 CS 1v1 Head Unlimited",
                    GameMode.CS_2V2_BODY to "⚔️ CS 2v2 Body",
                    GameMode.CS_1V1_BODY to "🛡️ CS 1v1 Body",
                    GameMode.SOLO_PER_KILL to "☠️ Solo Per Kill",
                    GameMode.DUO_PER_KILL to "👥 Duo Per Kill",
                    GameMode.LONE_WOLF_LOSS_TO_WIN to "🔄 LW Loss to Win",
                    GameMode.SQUAD to "🔥 Squad (4v4)",
                    GameMode.CLASH_SQUAD to "🥊 Clash Squad (CS)",
                    GameMode.SOLO to "🎯 Solo Scrims",
                    GameMode.DUO to "👥 Duo Scrims"
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Mode Selector Horizontal Row
                    Text("Select Game Mode:", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allModesList) { (mode, label) ->
                            val isSel = rulesSelectedMode == mode
                            Surface(
                                color = if (isSel) NeonFireOrange else Color(0xFF1B1D2B),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { rulesSelectedMode = mode }
                            ) {
                                Text(
                                    label,
                                    color = if (isSel) TextPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rules for currently selected mode
                    Box(modifier = Modifier.heightIn(max = 350.dp)) {
                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Surface(
                                    color = Color(0xFF161928),
                                    shape = RoundedCornerShape(10.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonGold))
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            "🏆 ${rulesSelectedMode.displayName}",
                                            color = NeonGold,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "Official rules applied automatically to all ${rulesSelectedMode.displayName} tournaments",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            val currentRules = com.example.model.defaultRulesForMode(rulesSelectedMode)
                            items(currentRules) { rule ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF111320))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("• ", color = NeonFireOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        rule,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAllTournamentRulesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange, contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
}
