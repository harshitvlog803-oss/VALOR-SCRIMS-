package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OwnerRole
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.components.FamPayQrCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

import com.example.ui.components.ClashXTopBar

@Composable
fun WalletScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val activeRole by viewModel.activeOwnerRole.collectAsState()
    val isWhiteTheme by viewModel.isWhiteBackground.collectAsState()

    val activeTabFromVm by viewModel.activeWalletTab.collectAsState()
    var activeWalletTab by remember(activeTabFromVm) { mutableStateOf(activeTabFromVm) } // 0: Deposit (FamPay QR), 1: Withdraw, 2: Refer & Earn, 3: History

    // Withdrawal Form State
    var withdrawAmountText by remember { mutableStateOf("") }
    var withdrawUpiId by remember { mutableStateOf("") }
    var selectedWithdrawMethod by remember { mutableStateOf("UPI / FamPay") }

    // Referral Form State
    var inputReferralCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackground(isWhiteTheme))
    ) {
        ClashXTopBar(
            title = "VALOR Gaming Vault",
            onBackClick = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(NeonFireOrange, NeonGold))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "TOTAL VAULT BALANCE",
                            color = TextTertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "₹",
                                color = NeonGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${profile.vaultBalance}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 42.sp
                            )
                        }

                        Text(
                            "Available for tournament bookings & withdrawal",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats pill row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF12141F))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOTAL EARNINGS", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("₹${profile.totalEarnings}", color = ElectricGreen, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("BOOYAHS WON", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${profile.booyahs}", color = NeonGold, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MATCHES PLAYED", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${profile.matchesPlayed}", color = ElectricCyan, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Action Buttons on Vault Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Add Money Button
                            Button(
                                onClick = {
                                    activeWalletTab = 0
                                    viewModel.setActiveWalletTab(0)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = GamingDarkBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text("+ Deposit", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }

                            // Withdraw Button
                            Button(
                                onClick = {
                                    activeWalletTab = 1
                                    viewModel.setActiveWalletTab(1)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricGreen,
                                    contentColor = GamingDarkBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text("💳 Withdraw", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }

                            // Refer & Earn Button (Prominent!)
                            Button(
                                onClick = {
                                    activeWalletTab = 2
                                    viewModel.setActiveWalletTab(2)
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFAB47BC),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text("🎁 Refer & Earn", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Refer & Earn Quick Banner in Vault
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { activeWalletTab = 2 },
                    colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color(0xFFFAF5FF) else Color(0xFF20132B)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFAB47BC), NeonGold))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFAB47BC).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎁", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Refer & Earn ₹15 per Friend",
                                    color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = ElectricGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Free Cash", color = ElectricGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text(
                                "Invite friends to VALOR SCRIMS & get instant vault balance",
                                color = if (isWhiteTheme) TextSecondaryDark else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFAB47BC))
                    }
                }
            }

            // Wallet Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isWhiteTheme) Color(0xFFE2E8F0) else Color(0xFF151724))
                        .padding(4.dp)
                ) {
                    val tabs = listOf("Add Money", "Withdraw", "Refer & Earn", "History")
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = activeWalletTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonFireOrange else Color.Transparent)
                                .clickable { activeWalletTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TextPrimary else (if (isWhiteTheme) TextSecondaryDark else TextSecondary),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Tab 0: Add Money (FamPay QR Integration - Unlimited Support)
            if (activeWalletTab == 0) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF131726),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonGold))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💎", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Add More Than ₹10 • Unlimited Money",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Any player can add ₹10 to unlimited money in app to play tournaments. 100% instant vault credit.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                item {
                    FamPayQrCard(
                        upiId = adminConfig.famPayUpiId,
                        isOwner1 = activeRole == OwnerRole.OWNER_1,
                        onUpdateUpi = { newUpi ->
                            viewModel.updateFamPayUpi(newUpi)
                        },
                        onVerifyOwner1Pin = { pin ->
                            viewModel.loginOwner(OwnerRole.OWNER_1, pin)
                        },
                        onDepositSubmitted = { amount, utr ->
                            viewModel.submitDeposit(amount, utr) { ok, msg ->
                                if (ok) {
                                    activeWalletTab = 2 // jump to history
                                }
                            }
                        }
                    )
                }
            }

            // Tab 1: Withdraw Cash (Min withdrawal > 50 rupees)
            if (activeWalletTab == 1) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(listOf(ElectricGreen.copy(alpha = 0.5f), BorderDark))
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = ElectricGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Request Instant Cash Withdrawal",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Minimum withdrawal must be more than ₹50 rupees",
                                color = NeonGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Withdrawal Method Selector
                            Text("Payout Destination", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("UPI / FamPay", "Paytm / PhonePe", "Bank Transfer").forEach { method ->
                                    val isSelected = selectedWithdrawMethod == method
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedWithdrawMethod = method },
                                        color = if (isSelected) ElectricGreen.copy(alpha = 0.2f) else Color(0xFF161826),
                                        shape = RoundedCornerShape(10.dp),
                                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ElectricGreen, ElectricGreen))) else null
                                    ) {
                                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                method,
                                                color = if (isSelected) ElectricGreen else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = withdrawAmountText,
                                onValueChange = { withdrawAmountText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Withdraw Amount (₹) - Min > ₹50") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = {
                                    Text("₹", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("withdraw_amount_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricGreen,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = withdrawUpiId,
                                onValueChange = { withdrawUpiId = it },
                                label = { Text("Your UPI ID or Account Number") },
                                placeholder = { Text("e.g. yourname@fam or 9813700369@paytm") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("withdraw_upi_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = TextSecondary)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricGreen,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val amount = withdrawAmountText.toIntOrNull() ?: 0
                                    if (amount <= 50) {
                                        Toast.makeText(context, "Withdrawal must be more than ₹50 rupees!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (amount > profile.vaultBalance) {
                                        Toast.makeText(context, "Insufficient vault balance! You have ₹${profile.vaultBalance}.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (withdrawUpiId.trim().isBlank()) {
                                        Toast.makeText(context, "Please enter your UPI ID or Account Number.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    viewModel.submitWithdrawal(amount, withdrawUpiId.trim()) { ok, _ ->
                                        if (ok) {
                                            withdrawAmountText = ""
                                            withdrawUpiId = ""
                                            activeWalletTab = 3 // jump to history
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_withdrawal_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricGreen,
                                    contentColor = GamingDarkBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Withdrawal Request", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            // Tab 2: Refer & Earn Option in Vault
            if (activeWalletTab == 2) {
                // Referral Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color.White else GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFAB47BC), NeonGold))
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFAB47BC).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎁", fontSize = 32.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "Refer & Earn Free Vault Cash",
                                color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Invite your Free Fire squad! Earn ₹15 in your vault for every friend who joins. They get ₹15 welcome cash too!",
                                color = if (isWhiteTheme) TextSecondaryDark else TextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Referral Code Pill
                            Surface(
                                color = if (isWhiteTheme) Color(0xFFF3E8FF) else Color(0xFF2E193C),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(Color(0xFFAB47BC), NeonGold))
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("YOUR UNIQUE REFERRAL CODE", color = Color(0xFFAB47BC), fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        profile.referralCode,
                                        color = NeonGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Copy & Share buttons
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Referral Code", profile.referralCode))
                                        Toast.makeText(context, "Copied code: ${profile.referralCode}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAB47BC)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Code", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Button(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "🔥 Play Free Fire Esports on VALOR SCRIMS! Use my referral code: *${profile.referralCode}* to get ₹15 cash in your vault! Download: https://ais-pre-fvghvhfqduz3527u5sgro7-930514938265.asia-southeast1.run.app"
                                            )
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share Referral Code")
                                        context.startActivity(shareIntent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share Invite", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Redeem a friend's referral code
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color.White else GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.verticalGradient(listOf(ElectricGreen.copy(alpha = 0.4f), BorderDark))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = ElectricGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Redeem Friend's Referral Code",
                                    color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Got invited by another gamer? Enter their code to receive ₹15 welcome bonus in your vault.",
                                color = if (isWhiteTheme) TextSecondaryDark else TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (profile.hasRedeemedReferral) {
                                Surface(
                                    color = ElectricGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "✅ Referral welcome bonus of ₹15 already claimed and credited!",
                                            color = ElectricGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = inputReferralCode,
                                    onValueChange = { inputReferralCode = it.uppercase() },
                                    label = { Text("Friend's Referral Code") },
                                    placeholder = { Text("e.g. VALOR99FF") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricGreen,
                                        unfocusedBorderColor = if (isWhiteTheme) Color(0xFFCBD5E1) else BorderDark,
                                        focusedTextColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                        unfocusedTextColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.redeemReferralCode(inputReferralCode) { ok, _ ->
                                            if (ok) inputReferralCode = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GamingDarkBackground),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Claim ₹15 Welcome Bonus", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Referral Stats Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color(0xFFF8FAFC) else Color(0xFF141724))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Your Referral Statistics",
                                color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("FRIENDS INVITED", color = if (isWhiteTheme) TextSecondaryDark else TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("${profile.referralCount}", color = ElectricCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("EARNINGS CREDITED", color = if (isWhiteTheme) TextSecondaryDark else TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("₹${profile.referralEarnings}", color = NeonGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("REWARD PER FRIEND", color = if (isWhiteTheme) TextSecondaryDark else TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("₹15", color = ElectricGreen, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // How Vault Referral Program Works
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color(0xFFF1F5F9) else GamingCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFAB47BC).copy(alpha = 0.5f), BorderDark))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "How Vault Referral Program Works",
                                    color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val steps = listOf(
                                "1. Share Your Unique Code" to "Copy your referral code *${profile.referralCode}* or tap Share Invite to send via WhatsApp, Telegram, or SMS.",
                                "2. Friends Register & Redeem" to "Your friend signs up or enters your code in their Vault under 'Redeem Friend's Code'.",
                                "3. Instant ₹15 Vault Credit" to "Both you and your friend receive ₹15 instantly into your VALOR Gaming Vault balance.",
                                "4. Play & Withdraw Winnings" to "Use your referral cash to book Free Fire tournament slots and withdraw real money directly to UPI / FamPay!"
                            )

                            steps.forEachIndexed { idx, (title, desc) ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Surface(
                                        color = Color(0xFFAB47BC).copy(alpha = 0.2f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${idx + 1}", color = Color(0xFFAB47BC), fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(title, color = if (isWhiteTheme) TextPrimaryDark else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(desc, color = if (isWhiteTheme) TextSecondaryDark else TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Recent Referrals List
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color(0xFFF8FAFC) else Color(0xFF141724))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Referred Friends (${profile.referralCount})",
                                    color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    color = ElectricGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Total ₹${profile.referralEarnings} Earned",
                                        color = ElectricGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val recentReferrals = listOf(
                                Triple("SNIPER_GOD_07", "Joined 2 hours ago", "₹15 Credited"),
                                Triple("DEVIL_HUNTER", "Joined yesterday", "₹15 Credited"),
                                Triple("ALPHA_ROHIT_FF", "Joined 3 days ago", "₹15 Credited")
                            )

                            recentReferrals.forEach { (friendIgn, time, payout) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isWhiteTheme) Color(0xFFEDF2F7) else Color(0xFF191C2B))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🎮", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(friendIgn, color = if (isWhiteTheme) TextPrimaryDark else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(time, color = if (isWhiteTheme) TextSecondaryDark else TextTertiary, fontSize = 10.sp)
                                        }
                                    }
                                    Text(payout, color = ElectricGreen, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 3: Transaction History
            if (activeWalletTab == 3) {
                item {
                    Text(
                        "Vault Activity & Statements",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (transactions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = GamingCard)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No transactions recorded yet.", color = TextSecondary)
                            }
                        }
                    }
                } else {
                    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                    items(transactions, key = { it.id }) { tx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GamingCard)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (tx.type) {
                                                    TransactionType.DEPOSIT -> ElectricGreen.copy(alpha = 0.2f)
                                                    TransactionType.PRIZE_WINNING -> NeonGold.copy(alpha = 0.2f)
                                                    TransactionType.WITHDRAWAL -> ElectricCyan.copy(alpha = 0.2f)
                                                    TransactionType.ENTRY_FEE -> NeonFireOrange.copy(alpha = 0.2f)
                                                    TransactionType.REFERRAL_BONUS -> Color(0xFFAB47BC).copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            when (tx.type) {
                                                TransactionType.DEPOSIT -> Icons.Default.AddCircle
                                                TransactionType.PRIZE_WINNING -> Icons.Default.EmojiEvents
                                                TransactionType.WITHDRAWAL -> Icons.Default.ArrowOutward
                                                TransactionType.ENTRY_FEE -> Icons.Default.SportsEsports
                                                TransactionType.REFERRAL_BONUS -> Icons.Default.CardGiftcard
                                            },
                                            contentDescription = null,
                                            tint = when (tx.type) {
                                                TransactionType.DEPOSIT -> ElectricGreen
                                                TransactionType.PRIZE_WINNING -> NeonGold
                                                TransactionType.WITHDRAWAL -> ElectricCyan
                                                TransactionType.ENTRY_FEE -> NeonFireOrange
                                                TransactionType.REFERRAL_BONUS -> Color(0xFFAB47BC)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = tx.description.ifBlank { tx.type.name },
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = sdf.format(Date(tx.timestamp)),
                                            color = TextTertiary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val isCredit = tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.PRIZE_WINNING || tx.type == TransactionType.REFERRAL_BONUS
                                    Text(
                                        text = "${if (isCredit) "+" else "-"}₹${tx.amount}",
                                        color = if (isCredit) ElectricGreen else TextPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )

                                    Surface(
                                        color = when (tx.status) {
                                            TransactionStatus.APPROVED, TransactionStatus.COMPLETED -> ElectricGreen.copy(alpha = 0.15f)
                                            TransactionStatus.PENDING -> NeonGold.copy(alpha = 0.15f)
                                            TransactionStatus.REJECTED -> DangerRed.copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = tx.status.name,
                                            color = when (tx.status) {
                                                TransactionStatus.APPROVED, TransactionStatus.COMPLETED -> ElectricGreen
                                                TransactionStatus.PENDING -> NeonGold
                                                TransactionStatus.REJECTED -> DangerRed
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
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
