package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
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
            // Authority Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (activeRole == OwnerRole.OWNER_1) listOf(NeonFireOrange, NeonGold)
                            else listOf(ElectricCyan, ElectricGreen)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (activeRole == OwnerRole.OWNER_1) "👑" else "🛡️", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                if (activeRole == OwnerRole.OWNER_1) "1st Owner Organiser Portal"
                                else "2nd Owner Organiser Portal",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                "🔒 Authorised: Only 1st Owner and 2nd Owner can publish matches",
                                color = if (activeRole == OwnerRole.OWNER_1) NeonGold else ElectricCyan,
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
                        Text("Game Mode", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GameMode.values().forEach { mode ->
                                val isSelected = selectedMode == mode
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedMode = mode },
                                    color = if (isSelected) NeonFireOrange else Color(0xFF151827),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            mode.displayName.split(" ").first(),
                                            color = if (isSelected) TextPrimary else TextSecondary,
                                            fontSize = 11.sp,
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
