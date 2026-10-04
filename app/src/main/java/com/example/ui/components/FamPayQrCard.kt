package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class ScannerPaymentMode {
    ONLY_10_RUPEES,
    PLAYERS_CHOICE
}

@Composable
fun FamPayQrCard(
    upiId: String,
    onDepositSubmitted: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    isOwner1: Boolean = false,
    onUpdateUpi: ((String) -> Unit)? = null,
    onVerifyOwner1Pin: ((String) -> Boolean)? = null
) {
    val context = LocalContext.current
    var selectedMethodTab by remember { mutableStateOf(0) } // 0: FamX, 1: Bank
    var scannerMode by remember { mutableStateOf(ScannerPaymentMode.PLAYERS_CHOICE) } // Default: Unlimited custom money add (Min ₹10)
    var depositAmountText by remember { mutableStateOf("10") }
    var utrText by remember { mutableStateOf("") }

    var showEditUpiDialog by remember { mutableStateOf(false) }
    var showOwner1PinDialog by remember { mutableStateOf(false) }
    var owner1PinInput by remember { mutableStateOf("") }
    var editingUpiInput by remember(upiId) { mutableStateOf(upiId) }

    val quickAmounts = listOf(10, 20, 50, 100, 200, 500, 1000, 2000, 5000)

    val effectiveAmount = when (scannerMode) {
        ScannerPaymentMode.ONLY_10_RUPEES -> 10
        ScannerPaymentMode.PLAYERS_CHOICE -> (depositAmountText.toIntOrNull() ?: 10).coerceAtLeast(1)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // "Introducing flex - Get crazy cashbacks & more!" Banner matching screenshot
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            color = Color(0xFF1E1D16),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(Color(0xFFE89A38), Color(0xFF8A622A)))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Introducing flex ",
                    color = Color(0xFFB099D6),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    "• Get crazy cashbacks & more!",
                    color = NeonGold,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = NeonFireOrange),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1017)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(NeonFireOrange.copy(alpha = 0.5f), Color(0xFF222436))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1st Owner QR Authority Badge & Change QR Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (isOwner1) {
                                editingUpiInput = upiId
                                showEditUpiDialog = true
                            } else {
                                owner1PinInput = ""
                                showOwner1PinDialog = true
                            }
                        }
                        .testTag("owner1_qr_banner"),
                    color = Color(0xFF141726),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(NeonGold, NeonFireOrange))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "App QR: 1st Owner Managed",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    if (isOwner1) "✅ Verified 1st Owner (Tap to change QR)" else "🔒 Only Owner 1 can change QR (Tap to unlock)",
                                    color = if (isOwner1) ElectricGreen else NeonGold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonGold.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(NeonGold, NeonGold))
                            )
                        ) {
                            Text(
                                "Change QR",
                                color = NeonGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Scanner Mode Selector (User request: Player add more than 10 rupees unlimited money add in app)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141624))
                        .padding(4.dp)
                ) {
                    // Mode 1: Unlimited Custom Add (Default - Min ₹10, No Limit)
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) ElectricCyan else Color.Transparent)
                            .clickable {
                                scannerMode = ScannerPaymentMode.PLAYERS_CHOICE
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "💰 Unlimited Add (₹10+)",
                                color = if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) GamingDarkBackground else TextSecondary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Text(
                                "Min ₹10 • No Limit",
                                color = if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) GamingDarkBackground.copy(alpha = 0.85f) else TextTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mode 2: Quick ₹10
                    Box(
                        modifier = Modifier
                            .weight(0.9f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (scannerMode == ScannerPaymentMode.ONLY_10_RUPEES) NeonFireOrange else Color.Transparent)
                            .clickable {
                                scannerMode = ScannerPaymentMode.ONLY_10_RUPEES
                                depositAmountText = "10"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "⚡ Quick ₹10",
                                color = if (scannerMode == ScannerPaymentMode.ONLY_10_RUPEES) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Text(
                                "1-Tap Entry",
                                color = if (scannerMode == ScannerPaymentMode.ONLY_10_RUPEES) NeonGold else TextTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Mode Banner
                Surface(
                    color = if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) ElectricCyan.copy(alpha = 0.15f) else NeonFireOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) listOf(ElectricCyan, ElectricGreen)
                            else listOf(NeonFireOrange, NeonGold)
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) "💎" else "⚡", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) "UNLIMITED MONEY ADD (MIN ₹10)" else "QUICK ₹10 ENTRY DEPOSIT",
                                    color = if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) ElectricCyan else NeonFireOrangeLight,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                                Text(
                                    if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) "Add more than 10 rupees • Unlimited money add in app" else "Quick 10 rupees instant payment",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            "₹$effectiveAmount",
                            color = NeonGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Header Pill: FamX vs Bank (with orange notification dot on Bank matching screenshot)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF1B1C28))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (selectedMethodTab == 0) Color(0xFF2E3146) else Color.Transparent)
                            .clickable { selectedMethodTab = 0 }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✕ ", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "FamX",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (selectedMethodTab == 1) Color(0xFF2E3146) else Color.Transparent)
                            .clickable { selectedMethodTab = 1 }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = "Bank",
                                tint = if (selectedMethodTab == 1) NeonGold else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Bank",
                                color = if (selectedMethodTab == 1) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Orange dot from screenshot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE89A38))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // QR Code Container with Decorative Falling Bills / Coins + Canvas QR
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF14141E))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative bills and gold coins background from screenshot
                    FamPayDecorBackground(modifier = Modifier.fillMaxSize())

                    // Crisp White QR Card
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FamPayQrCanvas(modifier = Modifier.fillMaxSize())

                        // Center Emblem (Golden Falcon with spread wings from screenshot)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF161622))
                                .border(2.dp, Color(0xFFE89A38), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(24.dp)) {
                                val path = Path().apply {
                                    moveTo(size.width * 0.2f, size.height * 0.6f)
                                    lineTo(size.width * 0.5f, size.height * 0.2f)
                                    lineTo(size.width * 0.8f, size.height * 0.5f)
                                    lineTo(size.width * 0.5f, size.height * 0.8f)
                                    close()
                                }
                                drawPath(path, color = Color(0xFFE89A38))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FamPay UPI ID Pill with Copy, Edit, and Share (Matches Screenshot)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // UPI ID + Copy Pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("FamPay UPI ID", upiId))
                                Toast.makeText(context, "UPI ID Copied: $upiId", Toast.LENGTH_SHORT).show()
                            },
                        color = Color(0xFF1B1D2A),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = upiId,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy UPI ID",
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Edit Icon (Only 1st Owner can change!)
                    IconButton(
                        onClick = {
                            if (isOwner1) {
                                editingUpiInput = upiId
                                showEditUpiDialog = true
                            } else {
                                owner1PinInput = ""
                                showOwner1PinDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B1D2A))
                            .testTag("edit_qr_button")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit QR",
                            tint = if (isOwner1) NeonGold else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Share Icon
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Pay for VALOR SCRIMS tournament on FamPay UPI: $upiId")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share FamPay UPI"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B1D2A))
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // One-Tap Pay via UPI App (GPay / PhonePe / Paytm / FamPay)
                Button(
                    onClick = {
                        val uri = Uri.parse("upi://pay?pa=$upiId&pn=VALOR%20SCRIMS&cu=INR&am=$effectiveAmount")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(Intent.createChooser(intent, "Pay ₹$effectiveAmount via UPI"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No UPI app found. Please copy UPI ID: $upiId", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pay_via_upi_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (scannerMode == ScannerPaymentMode.ONLY_10_RUPEES) NeonFireOrange else ElectricCyan,
                        contentColor = if (scannerMode == ScannerPaymentMode.ONLY_10_RUPEES) TextPrimary else GamingDarkBackground
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (scannerMode == ScannerPaymentMode.ONLY_10_RUPEES) "Pay ₹10 with FamPay / Any UPI App" else "Pay ₹$effectiveAmount via UPI",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = BorderDark)
                Spacer(modifier = Modifier.height(16.dp))

                // Deposit Verification Form
                Text(
                    "Submit Payment Confirmation",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // IF PLAYER'S CHOICE: Show custom write amount option and quick chips (Unlimited Support)
                if (scannerMode == ScannerPaymentMode.PLAYERS_CHOICE) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Select or Write Amount (₹):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Min ₹10 • Unlimited", color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontally scrollable quick amount chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickAmounts.forEach { amount ->
                            val isSelected = depositAmountText == amount.toString()
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { depositAmountText = amount.toString() },
                                color = if (isSelected) ElectricCyan else Color(0xFF1B1D2C),
                                shape = RoundedCornerShape(10.dp),
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ElectricCyan, ElectricCyan))) else null
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        "₹$amount",
                                        color = if (isSelected) GamingDarkBackground else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = depositAmountText,
                        onValueChange = { depositAmountText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Deposit Amount (₹) - Min ₹10, Unlimited") },
                        placeholder = { Text("Enter any amount e.g. 50, 100, 500, 1000...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deposit_amount_input"),
                        leadingIcon = {
                            Text("₹", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp))
                        },
                        trailingIcon = {
                            if (depositAmountText.isNotBlank()) {
                                Text(
                                    "Unlimited",
                                    color = ElectricGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "⚡ Players can add more than 10 rupees with unlimited money add in app.",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF141724),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Payment Mode:", color = TextSecondary, fontSize = 11.sp)
                                Text("₹10 Quick Match Entry", color = NeonGold, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                            TextButton(onClick = { scannerMode = ScannerPaymentMode.PLAYERS_CHOICE }) {
                                Text("Switch to Unlimited Add", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = utrText,
                    onValueChange = { utrText = it },
                    label = { Text("12-Digit UTR / Transaction ID") },
                    placeholder = { Text("e.g. 428910382910") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_input"),
                    leadingIcon = {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = TextSecondary)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonFireOrange,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val amount = effectiveAmount
                        if (amount < 10) {
                            Toast.makeText(context, "Amount must be at least ₹10 rupees! Players can add unlimited money in app.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (utrText.trim().length < 8) {
                            Toast.makeText(context, "Please enter valid 12-digit UTR from payment receipt!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onDepositSubmitted(amount, utrText.trim())
                        utrText = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_deposit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricGreen,
                        contentColor = GamingDarkBackground
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify & Add ₹$effectiveAmount to Vault (Unlimited)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }

    // Edit UPI Modal (1st Owner Only!)
    if (showEditUpiDialog) {
        AlertDialog(
            onDismissRequest = { showEditUpiDialog = false },
            title = {
                Text("1st Owner FamPay QR Settings", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "🔒 You have verified 1st Owner authority. Enter the new FamPay UPI ID for in-app deposits:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editingUpiInput,
                        onValueChange = { editingUpiInput = it },
                        label = { Text("FamPay UPI ID") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGold,
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
                        if (editingUpiInput.isNotBlank() && editingUpiInput.contains("@")) {
                            onUpdateUpi?.invoke(editingUpiInput.trim())
                            showEditUpiDialog = false
                            Toast.makeText(context, "FamPay QR updated by 1st Owner!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Invalid UPI ID", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = GamingDarkBackground)
                ) {
                    Text("Save QR Details", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditUpiDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingCardElevated
        )
    }

    // Owner 1 Verification Dialog (Only Owner 1 can customize App QR)
    if (showOwner1PinDialog) {
        AlertDialog(
            onDismissRequest = {
                showOwner1PinDialog = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑 ", fontSize = 20.sp)
                    Text("1st Owner QR Customization", color = NeonGold, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Only 1st Owner has the authority to customize the app FamPay QR code and UPI ID!\n\n2nd Owner and players cannot modify the QR code.\n\n1st Owner has NO PIN code. Tap below to proceed and customize the QR code:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val verified = onVerifyOwner1Pin?.invoke("") ?: false
                        if (verified) {
                            showOwner1PinDialog = false
                            editingUpiInput = upiId
                            showEditUpiDialog = true
                            Toast.makeText(context, "🔓 1st Owner Verified: You can now customize the app QR code & UPI ID!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "❌ Access Denied: Only 1st Owner can customize the app QR code.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = GamingDarkBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm & Customize QR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showOwner1PinDialog = false
                }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingCardElevated
        )
    }
}

/**
 * Decorative floating bills and coins background matching the screenshot
 */
@Composable
private fun FamPayDecorBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Floating green bills
        val billColor = Color(0x3300C853)
        drawRoundRect(
            color = billColor,
            topLeft = Offset(size.width * 0.05f, size.height * 0.1f),
            size = Size(60f, 32f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = billColor,
            topLeft = Offset(size.width * 0.8f, size.height * 0.75f),
            size = Size(80f, 40f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Floating gold coins
        val coinColor = Color(0x44FFD700)
        drawCircle(color = coinColor, radius = 14f, center = Offset(size.width * 0.35f, size.height * 0.15f))
        drawCircle(color = coinColor, radius = 10f, center = Offset(size.width * 0.7f, size.height * 0.3f))
        drawCircle(color = coinColor, radius = 12f, center = Offset(size.width * 0.65f, size.height * 0.85f))
    }
}

/**
 * High quality procedural QR Code Canvas
 */
@Composable
private fun FamPayQrCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val count = 25
        val moduleSize = size.width / count

        drawRect(Color.White)

        fun drawFinderPattern(col: Int, row: Int) {
            val left = col * moduleSize
            val top = row * moduleSize
            val finderSize = 7 * moduleSize

            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(left, top),
                size = Size(finderSize, finderSize),
                cornerRadius = CornerRadius(moduleSize * 1.2f, moduleSize * 1.2f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left + moduleSize, top + moduleSize),
                size = Size(finderSize - 2 * moduleSize, finderSize - 2 * moduleSize),
                cornerRadius = CornerRadius(moduleSize * 0.8f, moduleSize * 0.8f)
            )
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(left + 2 * moduleSize, top + 2 * moduleSize),
                size = Size(finderSize - 4 * moduleSize, finderSize - 4 * moduleSize),
                cornerRadius = CornerRadius(moduleSize * 0.6f, moduleSize * 0.6f)
            )
        }

        drawFinderPattern(0, 0)
        drawFinderPattern(count - 7, 0)
        drawFinderPattern(0, count - 7)

        for (i in 8 until count - 8 step 2) {
            drawRect(Color.Black, Offset(i * moduleSize, 6 * moduleSize), Size(moduleSize, moduleSize))
            drawRect(Color.Black, Offset(6 * moduleSize, i * moduleSize), Size(moduleSize, moduleSize))
        }

        val pattern = listOf(
            0b10101100, 0b11001010, 0b01011101, 0b11100101,
            0b10011010, 0b01101011, 0b10110100, 0b11010011,
            0b01010110, 0b10101010, 0b11000110, 0b01110101
        )

        for (r in 0 until count) {
            for (c in 0 until count) {
                val inFinder1 = (r < 8 && c < 8)
                val inFinder2 = (r < 8 && c >= count - 8)
                val inFinder3 = (r >= count - 8 && c < 8)
                val inCenter = (r in 10..14 && c in 10..14)

                if (!inFinder1 && !inFinder2 && !inFinder3 && !inCenter) {
                    val hash = (r * 31 + c * 17 + pattern[(r + c) % pattern.size]) % 3
                    if (hash == 0 || (r + c) % 4 == 0) {
                        drawRoundRect(
                            color = Color.Black,
                            topLeft = Offset(c * moduleSize + moduleSize * 0.1f, r * moduleSize + moduleSize * 0.1f),
                            size = Size(moduleSize * 0.8f, moduleSize * 0.8f),
                            cornerRadius = CornerRadius(moduleSize * 0.2f, moduleSize * 0.2f)
                        )
                    }
                }
            }
        }
    }
}
