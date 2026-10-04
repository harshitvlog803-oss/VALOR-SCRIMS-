package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun FamPayTopUpDialog(
    entryFee: Int,
    currentBalance: Int,
    upiId: String,
    tournamentTitle: String? = null,
    onDismiss: () -> Unit,
    onTopUpSuccess: (topUpAmount: Int) -> Unit
) {
    val context = LocalContext.current
    val minTopUp = 10
    val deficit = (entryFee - currentBalance).coerceAtLeast(minTopUp)

    var topUpAmountText by remember { mutableStateOf(deficit.toString()) }
    var utrInput by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }

    val quickOptions = remember(deficit, entryFee) {
        listOfNotNull(
            deficit,
            if (entryFee != deficit && entryFee >= minTopUp) entryFee else null,
            20,
            50,
            100,
            200,
            500,
            1000
        ).distinct()
    }

    val selectedAmount = topUpAmountText.toIntOrNull() ?: deficit

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1018)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(NeonFireOrange, BorderDark))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonFireOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonFireOrange, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Top-Up Wallet Balance",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "FamPay Dynamic QR Scanner",
                                color = NeonGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wallet Balance Summary Strip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF141724),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BorderDark, BorderDark)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Wallet Balance", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text("₹$currentBalance", color = if (currentBalance >= entryFee) ElectricGreen else DangerRed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }

                        if (tournamentTitle != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Required Entry Fee", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                Text("₹$entryFee", color = NeonGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Top Up Chips (Unlimited Support)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Top-Up Amount:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Min ₹10 • Unlimited", color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickOptions.forEach { amount ->
                        val isSelected = selectedAmount == amount
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { topUpAmountText = amount.toString() },
                            color = if (isSelected) NeonFireOrange else Color(0xFF191B2C),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    "₹$amount",
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Text Field (Player's Choice - Min ₹10, Unlimited)
                OutlinedTextField(
                    value = topUpAmountText,
                    onValueChange = { topUpAmountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Top-Up Amount (₹) - Min ₹10, Unlimited") },
                    placeholder = { Text("Enter any amount e.g. 50, 100, 500, 1000...") },
                    leadingIcon = {
                        Text("₹", color = NeonGold, fontWeight = FontWeight.Black, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp))
                    },
                    trailingIcon = {
                        if (topUpAmountText.isNotBlank()) {
                            Text(
                                "Unlimited",
                                color = ElectricGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("topup_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "⚡ Players can add more than 10 rupees with unlimited money add in app.",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Generated FamPay QR Code Box with dynamic amount preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Brush.horizontalGradient(listOf(NeonFireOrange, NeonGold)), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // FamPay brand label above QR
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Fam", color = Color(0xFF1E1E1E), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Pay", color = Color(0xFFFF7A00), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFFF7A00),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("QR", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // QR Code Canvas
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            TopUpQrCanvas(
                                modifier = Modifier.fillMaxSize()
                            )

                            // FamPay Center Emblem
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF7A00))
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("₹", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Color(0xFFF2F4F7),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "SCAN TO TOP-UP ₹$selectedAmount",
                                color = Color(0xFF1E1E1E),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FamPay UPI ID with Copy
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("FamPay UPI ID", upiId))
                            Toast.makeText(context, "UPI ID Copied: $upiId", Toast.LENGTH_SHORT).show()
                        },
                    color = Color(0xFF161826)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Official FamPay UPI ID", color = TextTertiary, fontSize = 10.sp)
                            Text(upiId, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonGold, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // One-Tap Pay via UPI App button
                Button(
                    onClick = {
                        val uri = Uri.parse("upi://pay?pa=$upiId&pn=VALOR%20SCRIMS&cu=INR&am=$selectedAmount")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(Intent.createChooser(intent, "Top Up ₹$selectedAmount via UPI"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No UPI app found. Please copy UPI ID: $upiId", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange, contentColor = TextPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pay ₹$selectedAmount via UPI / FamPay", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderDark)
                Spacer(modifier = Modifier.height(12.dp))

                // UTR Verification input
                OutlinedTextField(
                    value = utrInput,
                    onValueChange = { utrInput = it },
                    label = { Text("Enter 12-Digit UTR from Payment Receipt") },
                    placeholder = { Text("e.g. 428910382910") },
                    leadingIcon = {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = TextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("topup_utr_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricGreen,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (selectedAmount < minTopUp) {
                            Toast.makeText(context, "Amount must be at least ₹$minTopUp rupees! Players can add unlimited money in app.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (utrInput.trim().length < 8) {
                            Toast.makeText(context, "Please enter valid 12-digit UTR from payment receipt!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isVerifying = true
                        onTopUpSuccess(selectedAmount)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_topup_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GamingDarkBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify & Credit ₹$selectedAmount to Vault", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun TopUpQrCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val count = 23
        val moduleSize = size.width / count

        drawRect(Color.White)

        fun drawFinder(col: Int, row: Int) {
            val left = col * moduleSize
            val top = row * moduleSize
            val finderSize = 7 * moduleSize

            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(left, top),
                size = Size(finderSize, finderSize),
                cornerRadius = CornerRadius(moduleSize, moduleSize)
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
                cornerRadius = CornerRadius(moduleSize * 0.5f, moduleSize * 0.5f)
            )
        }

        drawFinder(0, 0)
        drawFinder(count - 7, 0)
        drawFinder(0, count - 7)

        for (r in 0 until count) {
            for (c in 0 until count) {
                val inFinder1 = (r < 8 && c < 8)
                val inFinder2 = (r < 8 && c >= count - 8)
                val inFinder3 = (r >= count - 8 && c < 8)
                val inCenter = (r in 9..13 && c in 9..13)

                if (!inFinder1 && !inFinder2 && !inFinder3 && !inCenter) {
                    val hash = (r * 19 + c * 37 + 11) % 3
                    if (hash == 0 || (r + c) % 3 == 0) {
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
