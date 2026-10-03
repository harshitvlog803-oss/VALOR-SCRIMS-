package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BracketMatch
import com.example.ui.theme.*

@Composable
fun BracketView(
    matches: List<BracketMatch>,
    modifier: Modifier = Modifier
) {
    if (matches.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GamingCard)
        ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    "Knockout brackets will be generated once slots are finalized.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    val rounds = matches.groupBy { it.roundName }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = NeonGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Clash Squad Knockout Bracket Tree",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
        ) {
            items(rounds.entries.toList()) { (roundTitle, roundMatches) ->
                RoundColumn(roundTitle = roundTitle, matches = roundMatches)
            }
        }
    }
}

@Composable
private fun RoundColumn(
    roundTitle: String,
    matches: List<BracketMatch>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Round header pill
        Surface(
            color = if (roundTitle.contains("Grand", ignoreCase = true)) NeonGold.copy(alpha = 0.2f) else Color(0xFF202334),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = roundTitle.uppercase(),
                    color = if (roundTitle.contains("Grand", ignoreCase = true)) NeonGold else ElectricCyan,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        matches.forEach { match ->
            MatchCard(match = match)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MatchCard(
    match: BracketMatch,
    modifier: Modifier = Modifier
) {
    val isFinal = match.roundName.contains("Grand", ignoreCase = true)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GamingCardElevated),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    if (isFinal) NeonGold.copy(alpha = 0.8f) else BorderDark,
                    Color(0xFF151722)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Team 1
            val team1IsWinner = match.winnerTeam == match.team1Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (team1IsWinner) ElectricGreen.copy(alpha = 0.15f) else Color.Transparent)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.team1Name,
                    color = if (team1IsWinner) ElectricGreen else TextPrimary,
                    fontWeight = if (team1IsWinner) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${match.team1Score}",
                    color = if (team1IsWinner) ElectricGreen else TextSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }

            HorizontalDivider(color = Color(0xFF2A2D40), modifier = Modifier.padding(vertical = 4.dp))

            // Team 2
            val team2IsWinner = match.winnerTeam == match.team2Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (team2IsWinner) ElectricGreen.copy(alpha = 0.15f) else Color.Transparent)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.team2Name,
                    color = if (team2IsWinner) ElectricGreen else TextPrimary,
                    fontWeight = if (team2IsWinner) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${match.team2Score}",
                    color = if (team2IsWinner) ElectricGreen else TextSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }

            if (match.winnerTeam != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Winner: ", color = TextTertiary, fontSize = 10.sp)
                    Text(
                        match.winnerTeam,
                        color = NeonGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
