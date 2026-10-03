package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

import com.example.ui.components.ClashXTopBar

@Composable
fun SupportScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val adminConfig by viewModel.adminConfig.collectAsState()

    val num1 = adminConfig.supportNumber1 // 9813700369
    val num2 = adminConfig.supportNumber2 // 72070 80543
    val cleanNum1 = num1.filter { it.isDigit() }
    val cleanNum2 = num2.filter { it.isDigit() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
    ) {
        ClashXTopBar(
            title = "24/7 Customer Support",
            onBackClick = { viewModel.navigateTo(Screen.Home) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(NeonFireOrange, ElectricCyan))
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NeonFireOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = NeonFireOrange, modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    "VALOR SCRIMS Player Helpdesk",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Instant assistance for tournament slots & payouts",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Customer Support Line 1 (9813700369)
            item {
                SupportNumberCard(
                    title = "Customer Support Line 1",
                    badge = "Primary Official Line",
                    badgeColor = NeonFireOrange,
                    number = num1,
                    onCallClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNum1"))
                        context.startActivity(intent)
                    },
                    onWhatsAppClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91$cleanNum1"))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed. Number: $num1", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCopyClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Support Line 1", num1))
                        Toast.makeText(context, "Support Line 1 Copied: $num1", Toast.LENGTH_SHORT).show()
                    },
                    testTagPrefix = "support_1"
                )
            }

            // Customer Support Line 2 (72070 80543)
            item {
                SupportNumberCard(
                    title = "Customer Support Line 2",
                    badge = "Payout & Match Line",
                    badgeColor = ElectricCyan,
                    number = num2,
                    onCallClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNum2"))
                        context.startActivity(intent)
                    },
                    onWhatsAppClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91$cleanNum2"))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed. Number: $num2", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCopyClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Support Line 2", num2))
                        Toast.makeText(context, "Support Line 2 Copied: $num2", Toast.LENGTH_SHORT).show()
                    },
                    testTagPrefix = "support_2"
                )
            }

            // FAQs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = GamingCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            "Frequently Asked Questions",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        FaqItem(
                            q = "Where do I find my Tournament Room ID & Password?",
                            a = "Room ID and Password are strictly locked and visible ONLY to booked players in the Tournament Details screen 15 minutes before match start."
                        )
                        HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))
                        FaqItem(
                            q = "How do I deposit money using FamPay QR?",
                            a = "Go to Vault -> Add Money. Scan the 1st Owner FamPay QR or copy UPI ID 9813700369@fam. Enter payment of more than ₹10 rupees and submit your 12-digit UTR number."
                        )
                        HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))
                        FaqItem(
                            q = "What is the minimum withdrawal limit?",
                            a = "You can withdraw any winnings over ₹50 rupees directly to your UPI ID, FamPay, PhonePe, or Bank account."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SupportNumberCard(
    title: String,
    badge: String,
    badgeColor: Color,
    number: String,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCopyClick: () -> Unit,
    testTagPrefix: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GamingCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(BorderDark, Color(0xFF131520))))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        badge,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCopyClick() },
                color = Color(0xFF131522)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = NeonGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = number,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCallClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${testTagPrefix}_call_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonFireOrange, contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onWhatsAppClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${testTagPrefix}_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GamingDarkBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun FaqItem(q: String, a: String) {
    Column {
        Text(q, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(a, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
    }
}
