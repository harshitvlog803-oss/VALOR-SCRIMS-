package com.example.ui.screens

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

    var activeWalletTab by remember { mutableStateOf(0) } // 0: Deposit (FamPay QR), 1: Withdraw, 2: History

    // Withdrawal Form State
    var withdrawAmountText by remember { mutableStateOf("") }
    var withdrawUpiId by remember { mutableStateOf("") }
    var selectedWithdrawMethod by remember { mutableStateOf("UPI / FamPay") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
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
                    }
                }
            }

            // Wallet Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF151724))
                        .padding(4.dp)
                ) {
                    val tabs = listOf("Add Money (FamPay)", "Withdraw Cash", "History")
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
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
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
                                            activeWalletTab = 2 // jump to history
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

            // Tab 2: Transaction History
            if (activeWalletTab == 2) {
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
                                            },
                                            contentDescription = null,
                                            tint = when (tx.type) {
                                                TransactionType.DEPOSIT -> ElectricGreen
                                                TransactionType.PRIZE_WINNING -> NeonGold
                                                TransactionType.WITHDRAWAL -> ElectricCyan
                                                TransactionType.ENTRY_FEE -> NeonFireOrange
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
                                    val isCredit = tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.PRIZE_WINNING
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
