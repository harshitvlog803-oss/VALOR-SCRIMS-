package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OwnerRole
import com.example.model.GameMode
import com.example.model.MapType
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.components.ClashXTopBar
import com.example.ui.theme.*

@Composable
fun OrganiseTournamentScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val activeRole by viewModel.activeOwnerRole.collectAsState()
    val isWhiteTheme by viewModel.isWhiteBackground.collectAsState()

    var tournTitle by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(GameMode.SQUAD) }
    var selectedMap by remember { mutableStateOf(MapType.BERMUDA) }
    var matchTime by remember { mutableStateOf("Today, 09:00 PM IST") }
    var entryFee by remember { mutableStateOf("25") }
    var prizePool by remember { mutableStateOf("1000") }
    var perKill by remember { mutableStateOf("15") }
    var totalSlots by remember { mutableStateOf("48") }
    var secretRoomId by remember { mutableStateOf("") }
    var secretRoomPass by remember { mutableStateOf("") }

    // 1st Owner Rules Customisation State
    var customRulesList by remember(selectedMode) {
        mutableStateOf(com.example.model.defaultRulesForMode(selectedMode).toMutableList())
    }
    var newRuleInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackground(isWhiteTheme))
    ) {
        ClashXTopBar(
            title = "Organise Tournament",
            onBackClick = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeRole != OwnerRole.OWNER_1 && activeRole != OwnerRole.OWNER_2) {
                // Not Authenticated as Owner View
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(ElectricCyan, ElectricGreen))
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🛡️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Owner Tournament Organiser",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Tournament organizing is authorized for 1st Owner (App Developer) and 2nd Owner (Tournament Manager).\n\nPlease authenticate in Owner Panel to continue.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.navigateTo(Screen.Admin) },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = GamingDarkBackground),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Login to Owner Panel", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Owner Organiser Portal
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(ElectricCyan, ElectricGreen))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛡️", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    if (activeRole == OwnerRole.OWNER_1) "1st Owner (App Developer) Organiser" else "2nd Owner Organiser Portal",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "🔒 Authorised: Configure matches, entry fees & secret room ID",
                                    color = ElectricCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Form Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingCard)
                    ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Tournament Information", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = tournTitle,
                            onValueChange = { tournTitle = it },
                            label = { Text("Tournament Title") },
                            placeholder = { Text("e.g. 🔥 VALOR SCRIMS Bermuda Squad Championship") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("organise_title_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonFireOrange,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Game Mode Selector
                        Text("Game Mode (Select match type)", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GameMode.values().forEach { mode ->
                                val isSelected = selectedMode == mode
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedMode = mode },
                                    color = if (isSelected) NeonFireOrange else Color(0xFF151827),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            mode.displayName,
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Map Selector
                        Text("Map", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(MapType.BERMUDA, MapType.PURGATORY, MapType.KALAHARI).forEach { map ->
                                val isSelected = selectedMap == map
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedMap = map },
                                    color = if (isSelected) ElectricCyan else Color(0xFF151827),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            map.displayName,
                                            color = if (isSelected) GamingDarkBackground else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = entryFee,
                                onValueChange = { entryFee = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Entry Fee (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("organise_entry_fee"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = prizePool,
                                onValueChange = { prizePool = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Total Prize (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("organise_prize_pool"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = perKill,
                                onValueChange = { perKill = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Per Kill (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = totalSlots,
                                onValueChange = { totalSlots = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Total Slots") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = matchTime,
                            onValueChange = { matchTime = it },
                            label = { Text("Match Schedule Time") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonFireOrange,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderDark)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Secret Room Credentials (Protected - Only for booking players)
                        Text(
                            "Secret Custom Room Credentials (Protected)",
                            color = NeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Only visible to booked players 15 minutes before match start",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = secretRoomId,
                                onValueChange = { secretRoomId = it },
                                label = { Text("Room ID") },
                                placeholder = { Text("e.g. 8920147") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricGreen,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = secretRoomPass,
                                onValueChange = { secretRoomPass = it },
                                label = { Text("Room Password") },
                                placeholder = { Text("e.g. cx99") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricGreen,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // RULES SECTION: 1st Owner can CUSTOMISE rules freely! 2nd Owner has read-only esports presets.
                        if (activeRole == OwnerRole.OWNER_1) {
                            // 👑 1st Owner: App Customiser Rule Editor
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF1B1428),
                                shape = RoundedCornerShape(14.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(NeonFireOrange, NeonGold))
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("👑", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    "Customised Tournament Rules (1st Owner)",
                                                    color = NeonGold,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    "App Customiser: Add, edit & remove rules for this match",
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        TextButton(
                                            onClick = {
                                                customRulesList = com.example.model.defaultRulesForMode(selectedMode).toMutableList()
                                            }
                                        ) {
                                            Text("Reset", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Add New Custom Rule Input
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = newRuleInput,
                                            onValueChange = { newRuleInput = it },
                                            placeholder = { Text("e.g. Special M1887 duel / No Gloo Wall break") },
                                            label = { Text("Add Custom Rule") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = NeonGold,
                                                unfocusedBorderColor = BorderDark,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )
                                        Button(
                                            onClick = {
                                                if (newRuleInput.isNotBlank()) {
                                                    customRulesList = (customRulesList + newRuleInput.trim()).toMutableList()
                                                    newRuleInput = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = GamingDarkBackground),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Add", fontWeight = FontWeight.Black)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text("Match Rules (${customRulesList.size}):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    customRulesList.forEachIndexed { idx, rule ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF131524))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                                                Text("${idx + 1}. ", color = NeonFireOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(rule, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                                            }
                                            IconButton(
                                                onClick = {
                                                    customRulesList = customRulesList.filterIndexed { i, _ -> i != idx }.toMutableList()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // 🛡️ 2nd Owner: Standard Rules Preview (Read-only)
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF131524),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(ElectricCyan.copy(alpha = 0.5f), BorderDark))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📜", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                "Rules Applied to this Tournament (${selectedMode.displayName})",
                                                color = NeonGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                "🔒 Rule Customisation is exclusively for 1st Owner (App Customiser)",
                                                color = ElectricCyan,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val previewRules = com.example.model.defaultRulesForMode(selectedMode)
                                    previewRules.take(4).forEach { rule ->
                                        Text("• $rule", color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                    if (previewRules.size > 4) {
                                        Text("+ ${previewRules.size - 4} more esports fair play rules included automatically.", color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (tournTitle.isBlank()) {
                                    Toast.makeText(context, "Please enter tournament title", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val fee = entryFee.toIntOrNull() ?: 20
                                val prize = prizePool.toIntOrNull() ?: 500
                                val kill = perKill.toIntOrNull() ?: 10
                                val slots = totalSlots.toIntOrNull() ?: 48

                                val finalRules = if (activeRole == OwnerRole.OWNER_1) customRulesList.toList() else com.example.model.defaultRulesForMode(selectedMode)

                                val newTournament = Tournament(
                                    id = "cx-${System.currentTimeMillis()}",
                                    title = tournTitle.trim(),
                                    gameMode = selectedMode,
                                    mapType = selectedMap,
                                    matchTime = matchTime.trim(),
                                    entryFee = fee,
                                    prizePool = prize,
                                    perKillPrize = kill,
                                    maxSlots = slots,
                                    bookedSlots = 0,
                                    status = TournamentStatus.OPEN,
                                    roomId = secretRoomId.trim(),
                                    roomPassword = secretRoomPass.trim(),
                                    rules = finalRules,
                                    bookedPlayers = emptyList()
                                )

                                if (viewModel.createTournament(newTournament)) {
                                    viewModel.navigateTo(Screen.Home)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("publish_tournament_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonFireOrange,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish Tournament to VALOR SCRIMS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
}
