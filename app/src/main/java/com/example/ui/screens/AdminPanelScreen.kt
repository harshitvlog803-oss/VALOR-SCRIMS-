package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OwnerRole
import com.example.model.*
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

import com.example.ui.components.ClashXTopBar

@Composable
fun AdminPanelScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val activeRole by viewModel.activeOwnerRole.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val tournaments by viewModel.filteredTournaments.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    // Login state - Default to Owner 2 (No player can access 1st Owner management)
    var selectedLoginRole by remember { mutableStateOf(OwnerRole.OWNER_2) }
    var enteredPin by remember { mutableStateOf("") }
    var showDeveloperAuth by remember { mutableStateOf(false) }

    // 1st Owner Pin Change state
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }

    // 2nd Owner Configuration state (Managed by 1st Owner)
    var owner2NameInput by remember(adminConfig.owner2Name) { mutableStateOf(adminConfig.owner2Name) }
    var owner2PhoneInput by remember(adminConfig.owner2Phone) { mutableStateOf(adminConfig.owner2Phone) }
    var owner2PinInput by remember { mutableStateOf("") }
    var owner2EnabledState by remember(adminConfig.owner2Enabled) { mutableStateOf(adminConfig.owner2Enabled) }

    // FamPay QR Edit state (Only 1st Owner can change!)
    var famPayUpiInput by remember(adminConfig.famPayUpiId) { mutableStateOf(adminConfig.famPayUpiId) }

    // Organise Tournament state (Available for Owner 1 & Owner 2)
    var newTournTitle by remember { mutableStateOf("") }
    var newTournMode by remember { mutableStateOf(GameMode.SQUAD) }
    var newTournMap by remember { mutableStateOf(MapType.BERMUDA) }
    var newTournTime by remember { mutableStateOf("Today, 09:00 PM IST") }
    var newTournEntryFee by remember { mutableStateOf("25") }
    var newTournPrize by remember { mutableStateOf("1000") }
    var newTournPerKill by remember { mutableStateOf("15") }
    var newTournSlots by remember { mutableStateOf("48") }
    var newTournRoomId by remember { mutableStateOf("") }
    var newTournRoomPass by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
    ) {
        // Top App Bar
        ClashXTopBar(
            title = if (activeRole == OwnerRole.NONE) "Owner / Admin Portal"
            else if (activeRole == OwnerRole.OWNER_1) "1st Owner (Super Admin)"
            else "2nd Owner (Payouts & Tournaments)",
            onBackClick = { viewModel.navigateTo(Screen.Home) },
            actions = {
                if (activeRole != OwnerRole.NONE) {
                    TextButton(onClick = { viewModel.logoutOwner() }) {
                        Text("Logout", color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )

        if (activeRole == OwnerRole.NONE) {
            // --- Owner Login Screen ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(NeonFireOrange, NeonGold))
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(NeonFireOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = NeonFireOrange, modifier = Modifier.size(28.dp))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "VALOR SCRIMS Two-Owner Management",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "1st Owner: App Developer & QR Customization\n2nd Owner: Tournament Management & Organising",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Role Selection Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF141724))
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selectedLoginRole == OwnerRole.OWNER_2) ElectricCyan else Color.Transparent)
                                        .clickable {
                                            selectedLoginRole = OwnerRole.OWNER_2
                                            enteredPin = ""
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "2nd Owner",
                                        color = if (selectedLoginRole == OwnerRole.OWNER_2) GamingDarkBackground else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selectedLoginRole == OwnerRole.OWNER_1) NeonFireOrange else Color.Transparent)
                                        .clickable {
                                            selectedLoginRole = OwnerRole.OWNER_1
                                            enteredPin = ""
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "App Developer",
                                        color = if (selectedLoginRole == OwnerRole.OWNER_1) TextPrimary else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (selectedLoginRole == OwnerRole.OWNER_2) {
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = { enteredPin = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Enter 2nd Owner PIN") },
                                    placeholder = { Text("••••") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_pin_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricCyan,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "🔒 Enter private 2nd Owner PIN code to manage and organise tournaments.",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            } else {
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = { enteredPin = it },
                                    label = { Text("App Developer Passcode") },
                                    placeholder = { Text("••••") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_dev_passcode_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "🔒 Exclusive for App Developer only. Regular players cannot log in to 1st Owner management.",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val success = viewModel.loginOwner(selectedLoginRole, enteredPin)
                                    if (success) {
                                        enteredPin = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_owner_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedLoginRole == OwnerRole.OWNER_2) ElectricCyan else NeonFireOrange,
                                    contentColor = if (selectedLoginRole == OwnerRole.OWNER_2) GamingDarkBackground else TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (selectedLoginRole == OwnerRole.OWNER_2) "Unlock 2nd Owner Management"
                                    else "Verify App Developer (1st Owner)",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // --- Authenticated Owner Dashboard ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Role Badge Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (activeRole == OwnerRole.OWNER_1) Color(0xFF281C14) else Color(0xFF142428)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                if (activeRole == OwnerRole.OWNER_1) listOf(NeonFireOrange, NeonGold)
                                else listOf(ElectricCyan, ElectricGreen)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (activeRole == OwnerRole.OWNER_1) "👑" else "🛡️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    if (activeRole == OwnerRole.OWNER_1) "1st Owner (Super Admin)"
                                    else "2nd Owner (${adminConfig.owner2Name})",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(
                                    if (activeRole == OwnerRole.OWNER_1) "Full System Authority & Exclusive QR Code Control"
                                    else "Tournament Organizing & Secure Payouts Management",
                                    color = if (activeRole == OwnerRole.OWNER_1) NeonGold else ElectricCyan,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // 1ST OWNER ONLY: Authority Overview (No PIN Required, QR Customization)
                if (activeRole == OwnerRole.OWNER_1) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(NeonFireOrange, NeonGold))
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NeonGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "1st Owner Authority & Controls",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "👑 App Developer (1st Owner) Authority:\n" +
                                    "• Exclusively customize FamPay QR Code & UPI ID\n" +
                                    "• Choose & configure 2nd Owner credentials\n" +
                                    "• Organise tournaments & manage payouts alongside 2nd Owner",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // 1ST OWNER ONLY: Choose & Configure 2nd Owner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Choose & Configure 2nd Owner",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "1st Owner selects who 2nd Owner is and their access credentials",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = owner2NameInput,
                                    onValueChange = { owner2NameInput = it },
                                    label = { Text("2nd Owner Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = owner2PhoneInput,
                                    onValueChange = { owner2PhoneInput = it },
                                    label = { Text("2nd Owner Phone Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = owner2PinInput,
                                    onValueChange = { owner2PinInput = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Set 2nd Owner PIN") },
                                    placeholder = { Text("••••") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Enable 2nd Owner Access", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Switch(
                                        checked = owner2EnabledState,
                                        onCheckedChange = { owner2EnabledState = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricGreen)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.updateOwner2Credentials(
                                            name = owner2NameInput,
                                            phone = owner2PhoneInput,
                                            pin = owner2PinInput,
                                            enabled = owner2EnabledState
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = GamingDarkBackground),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Save 2nd Owner Configuration", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 1ST OWNER ONLY: FamPay QR Code Management
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(listOf(NeonGold.copy(alpha = 0.5f), BorderDark))
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = NeonGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "1st Owner FamPay QR Code & UPI ID",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Text(
                                    "🔒 Right only for 1st Owner. 2nd Owner CANNOT change QR code.",
                                    color = NeonGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = famPayUpiInput,
                                    onValueChange = { famPayUpiInput = it },
                                    label = { Text("FamPay UPI ID") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonGold,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.updateFamPayUpi(famPayUpiInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = GamingDarkBackground),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Update FamPay QR UPI ID", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 2ND OWNER: Display Locked FamPay QR notice
                if (activeRole == OwnerRole.OWNER_2) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1418)),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(listOf(DangerRed.copy(alpha = 0.5f), BorderDark))
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = DangerRed)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "FamPay QR Code Settings (LOCKED)",
                                        color = DangerRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "🔒 Permission Denied: 2nd Owner has NO right to change the FamPay QR code. Only 1st Owner has the authority to modify the FamPay QR code.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Current QR UPI ID: ${adminConfig.famPayUpiId}",
                                    color = NeonGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 1ST & 2ND OWNER: Organise Tournaments (Both have option)
                if (activeRole == OwnerRole.OWNER_1 || activeRole == OwnerRole.OWNER_2) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(listOf(NeonFireOrange.copy(alpha = 0.5f), BorderDark))
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AddBox, contentDescription = null, tint = NeonFireOrange)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Organise New Tournament",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Text(
                                    "Tournament organising controls for 1st Owner (App Developer) & 2nd Owner",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = newTournTitle,
                                onValueChange = { newTournTitle = it },
                                label = { Text("Tournament Title") },
                                placeholder = { Text("e.g. 🔥 VALOR SCRIMS Midnight Squad") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mode Selector
                            Text("Game Mode (Select match type)", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GameMode.values().forEach { mode ->
                                    val isSelected = newTournMode == mode
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { newTournMode = mode },
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

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newTournEntryFee,
                                    onValueChange = { newTournEntryFee = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Entry Fee (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = newTournPrize,
                                    onValueChange = { newTournPrize = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Total Prize (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newTournPerKill,
                                    onValueChange = { newTournPerKill = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Per Kill (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = newTournSlots,
                                    onValueChange = { newTournSlots = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Total Slots") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonFireOrange,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newTournTime,
                                onValueChange = { newTournTime = it },
                                label = { Text("Match Schedule Time") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newTournRoomId,
                                    onValueChange = { newTournRoomId = it },
                                    label = { Text("Room ID (Secret)") },
                                    placeholder = { Text("e.g. 9812741") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricGreen,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = newTournRoomPass,
                                    onValueChange = { newTournRoomPass = it },
                                    label = { Text("Room Password") },
                                    placeholder = { Text("e.g. cx12") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricGreen,
                                        unfocusedBorderColor = BorderDark,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (newTournTitle.isBlank()) {
                                        Toast.makeText(context, "Please enter tournament title", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val entry = newTournEntryFee.toIntOrNull() ?: 20
                                    val prize = newTournPrize.toIntOrNull() ?: 500
                                    val perKill = newTournPerKill.toIntOrNull() ?: 10
                                    val slots = newTournSlots.toIntOrNull() ?: 48

                                    val t = Tournament(
                                        id = "cx-${System.currentTimeMillis()}",
                                        title = newTournTitle.trim(),
                                        gameMode = newTournMode,
                                        mapType = newTournMap,
                                        matchTime = newTournTime.trim(),
                                        entryFee = entry,
                                        prizePool = prize,
                                        perKillPrize = perKill,
                                        maxSlots = slots,
                                        bookedSlots = 0,
                                        status = TournamentStatus.OPEN,
                                        roomId = newTournRoomId.trim(),
                                        roomPassword = newTournRoomPass.trim()
                                    )

                                    if (viewModel.createTournament(t)) {
                                        newTournTitle = ""
                                        newTournRoomId = ""
                                        newTournRoomPass = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publish Tournament", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

                // MANAGE PAYOUTS & EARNINGS DISTRIBUTION (2nd Owner primary role & 1st Owner)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(listOf(ElectricGreen.copy(alpha = 0.5f), BorderDark))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = ElectricGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Tournament Payouts & Earnings Distribution",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                "Manage player withdrawal requests and distribute tournament earnings securely",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            val pendingWithdrawals = transactions.filter { it.type == TransactionType.WITHDRAWAL && it.status == TransactionStatus.PENDING }

                            if (pendingWithdrawals.isEmpty()) {
                                Surface(
                                    color = Color(0xFF141724),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                                        Text("No pending withdrawal payout requests.", color = TextSecondary, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                pendingWithdrawals.forEach { tx ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151827))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("₹${tx.amount}", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                                    Text("To: ${tx.upiId}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Surface(color = NeonGold.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                                    Text("PENDING", color = NeonGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = { viewModel.processWithdrawal(tx.id, approve = true) },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GamingDarkBackground),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Approve & Pay", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.processWithdrawal(tx.id, approve = false) },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Reject & Refund", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
    }
}
