package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isWhiteTheme by viewModel.isWhiteBackground.collectAsState()
    var isSignUpMode by remember { mutableStateOf(false) }

    // Sign In Fields
    var loginIdentifier by remember { mutableStateOf("ALPHA_PRO_99") }
    var loginPassword by remember { mutableStateOf("pass123") }

    // Sign Up Fields
    var signupIgn by remember { mutableStateOf("") }
    var signupUid by remember { mutableStateOf("") }
    var signupPhone by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("pass123") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appBackground(isWhiteTheme))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Brand Emblem
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonFireOrange, NeonGold))
                        )
                        .border(3.dp, NeonGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔥", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Brand Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "VALOR ",
                        color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "SCRIMS",
                        color = NeonFireOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    "Free Fire Esports Tournaments & Cash Scrims",
                    color = if (isWhiteTheme) TextSecondaryDark else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ⚡ Instant Easy 1-Tap Entry Button
                Button(
                    onClick = {
                        viewModel.quickGuestLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("instant_guest_login_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricGreen,
                        contentColor = GamingDarkBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⚡ Easy 1-Tap Instant Login (Play Now)", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Toggle (Sign In vs Create Account)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isWhiteTheme) Color(0xFFE2E8F0) else Color(0xFF141624))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isSignUpMode) NeonFireOrange else Color.Transparent)
                            .clickable { isSignUpMode = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Player Sign In",
                            color = if (!isSignUpMode) TextPrimary else (if (isWhiteTheme) TextSecondaryDark else TextSecondary),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSignUpMode) NeonFireOrange else Color.Transparent)
                            .clickable { isSignUpMode = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Create Gamer ID",
                            color = if (isSignUpMode) TextPrimary else (if (isWhiteTheme) TextSecondaryDark else TextSecondary),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isWhiteTheme) Color.White else GamingCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(NeonFireOrange.copy(alpha = 0.5f), if (isWhiteTheme) Color(0xFFE2E8F0) else BorderDark)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (!isSignUpMode) {
                            // SIGN IN MODE
                            Text(
                                "Welcome Back, Gamer",
                                color = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Enter your Free Fire IGN to access tournaments & vault",
                                color = if (isWhiteTheme) TextSecondaryDark else TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = { loginIdentifier = it },
                                label = { Text("Free Fire IGN or Mobile Number") },
                                placeholder = { Text("e.g. ALPHA_PRO_99") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_ign_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonGold)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = if (isWhiteTheme) Color(0xFFCBD5E1) else BorderDark,
                                    focusedTextColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                    unfocusedTextColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password (Default: pass123)") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = if (isWhiteTheme) TextSecondaryDark else TextSecondary)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = if (isWhiteTheme) Color(0xFFCBD5E1) else BorderDark,
                                    focusedTextColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary,
                                    unfocusedTextColor = if (isWhiteTheme) TextPrimaryDark else TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val id = loginIdentifier.ifBlank { "ALPHA_PRO_99" }
                                    val pass = loginPassword.ifBlank { "pass123" }
                                    viewModel.loginPlayer(id, pass)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_submit_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonFireOrange,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Easy Sign In & Enter Scrims", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // One-Tap Demo Login
                            OutlinedButton(
                                onClick = {
                                    viewModel.loginPlayer("ALPHA_PRO_99", "pass123")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("demo_login_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan)
                            ) {
                                Text("⚡ One-Tap Quick Login (ALPHA_PRO_99)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // SIGN UP MODE
                            Text(
                                "Create New Gamer Profile",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Initial Vault balance is ₹0. Add money via FamPay QR to play tournaments!",
                                color = NeonGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = signupIgn,
                                onValueChange = { signupIgn = it },
                                label = { Text("Free Fire In-Game Name (IGN)") },
                                placeholder = { Text("e.g. RAISTAR_PRO") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_ign_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = NeonGold)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = signupUid,
                                onValueChange = { signupUid = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Free Fire UID (Digits only)") },
                                placeholder = { Text("e.g. 2948102847") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_uid_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = TextSecondary)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = signupPhone,
                                onValueChange = { signupPhone = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Mobile Phone Number") },
                                placeholder = { Text("e.g. 9813700369") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_phone_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = signupPassword,
                                onValueChange = { signupPassword = it },
                                label = { Text("Create Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_password_input"),
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonFireOrange,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    viewModel.signupPlayer(signupIgn, signupUid, signupPhone, signupPassword)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_submit_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricGreen,
                                    contentColor = GamingDarkBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create Account & Start (₹0 Vault Balance)", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Trust & Security Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FeatureBadge("FamPay QR", "Instant Deposit")
                    FeatureBadge("Secret Room ID", "Booked Only")
                    FeatureBadge("Fast Payouts", "UPI & Bank")
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = TextTertiary, fontSize = 9.sp)
    }
}
