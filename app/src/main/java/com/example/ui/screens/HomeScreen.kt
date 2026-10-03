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

    var bookingDialogTournament by remember { mutableStateOf<Tournament?>(null) }
    var insufficientBalanceDialogData by remember { mutableStateOf<Pair<Int, Int>?>(null) } // Pair(balance, required)
    var inputIgn by remember { mutableStateOf(profile.ign) }
    var inputUid by remember { mutableStateOf(profile.uid) }

    var showOnlyMyBooked by remember { mutableStateOf(false) }
    var showAllBookedPlayersDialog by remember { mutableStateOf(false) }
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
            .background(GamingDarkBackground)
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
                                color = TextPrimary,
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

                    // Owner / Admin Portal Button
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Admin) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2132))
                            .testTag("admin_button")
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Owner Panel",
                            tint = NeonFireOrangeLight,
                            modifier = Modifier.size(20.dp)
                        )
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

                // Organise Option: Restricted to 1st Owner & 2nd Owner
                Button(
                    onClick = {
                        val role = activeRole
                        if (role == OwnerRole.OWNER_1 || role == OwnerRole.OWNER_2) {
                            viewModel.navigateTo(Screen.OrganiseTournament)
                        } else {
                            showOrganiseAuthDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonFireOrange,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("organise_tournament_button")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Organise (Owner 1 & 2)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

    // Floating Organise Tournament Action Button (Owner 1 & 2 only)
    ExtendedFloatingActionButton(
        onClick = {
            val role = activeRole
            if (role == OwnerRole.OWNER_1 || role == OwnerRole.OWNER_2) {
                viewModel.navigateTo(Screen.OrganiseTournament)
            } else {
                showOrganiseAuthDialog = true
            }
        },
        icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = TextPrimary) },
        text = {
            Text(
                text = if (activeRole == OwnerRole.OWNER_1 || activeRole == OwnerRole.OWNER_2) "Organise Match" else "Organise (Owner 1 & 2)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        },
        containerColor = NeonFireOrange,
        contentColor = TextPrimary,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .testTag("fab_organise_tournament")
    )

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
                        "Please verify your Owner credentials to publish a new tournament.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Role Selector (Owner 1 vs Owner 2)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF141624))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authSelectedRole == OwnerRole.OWNER_1) NeonFireOrange else Color.Transparent)
                                .clickable { authSelectedRole = OwnerRole.OWNER_1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "1st Owner",
                                color = if (authSelectedRole == OwnerRole.OWNER_1) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (authSelectedRole == OwnerRole.OWNER_2) ElectricCyan else Color.Transparent)
                                .clickable { authSelectedRole = OwnerRole.OWNER_2 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "2nd Owner",
                                color = if (authSelectedRole == OwnerRole.OWNER_2) GamingDarkBackground else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (authSelectedRole == OwnerRole.OWNER_1 && !adminConfig.owner1RequiresPin) {
                        Surface(
                            color = ElectricGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "1 Owner No PIN Code Mode is Active! Tap Unlock to proceed directly.",
                                    color = ElectricGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = authPinInput,
                            onValueChange = { authPinInput = it.filter { ch -> ch.isDigit() } },
                            label = {
                                Text(
                                    if (authSelectedRole == OwnerRole.OWNER_1) "Enter 1st Owner Private PIN (Default: 0105)"
                                    else "Enter 2nd Owner PIN"
                                )
                            },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("organise_auth_pin_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonFireOrange,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.loginOwner(authSelectedRole, authPinInput)
                        if (success) {
                            showOrganiseAuthDialog = false
                            authPinInput = ""
                            viewModel.navigateTo(Screen.OrganiseTournament)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange),
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
}
}
