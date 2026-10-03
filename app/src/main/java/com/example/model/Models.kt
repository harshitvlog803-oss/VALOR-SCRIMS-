package com.example.model

enum class GameMode(val displayName: String, val teamSize: Int) {
    SOLO("Solo", 1),
    DUO("Duo", 2),
    SQUAD("Squad (4v4)", 4),
    CLASH_SQUAD("Clash Squad (CS)", 4)
}

enum class MapType(val displayName: String) {
    BERMUDA("Bermuda"),
    PURGATORY("Purgatory"),
    KALAHARI("Kalahari"),
    ALPINE("Alpine"),
    NEXTERRA("NexTerra")
}

enum class TournamentStatus(val label: String) {
    UPCOMING("Upcoming"),
    OPEN("Open for Booking"),
    LIVE("Match Ongoing"),
    COMPLETED("Completed")
}

data class Tournament(
    val id: String,
    val title: String,
    val gameMode: GameMode,
    val mapType: MapType,
    val matchTime: String,
    val entryFee: Int,
    val prizePool: Int,
    val perKillPrize: Int,
    val maxSlots: Int,
    val bookedSlots: Int,
    val status: TournamentStatus,
    val roomId: String = "",
    val roomPassword: String = "",
    val rules: List<String> = listOf(
        "No emulators allowed. Mobile players only.",
        "Hacking / Scripts / Glitch abuse results in permanent ban & forfeit.",
        "Room ID & Pass will be visible to booked players 15 mins prior.",
        "Take screenshot of victory Booyah & kill summary."
    ),
    val scoreboards: List<ScoreboardEntry> = emptyList(),
    val bracketMatches: List<BracketMatch> = emptyList(),
    val bookedPlayers: List<Booking> = emptyList()
)

data class Booking(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val playerIgn: String,
    val playerUid: String,
    val slotNumber: Int,
    val entryFee: Int,
    val bookedAt: Long = System.currentTimeMillis()
)

data class ScoreboardEntry(
    val rank: Int,
    val playerIgn: String,
    val playerUid: String,
    val kills: Int,
    val placementPoints: Int,
    val killPoints: Int,
    val totalPoints: Int,
    val prizeWon: Int
)

data class BracketMatch(
    val id: String,
    val roundName: String, // "Quarter Finals", "Semi Finals", "Grand Finale"
    val matchNumber: Int,
    val team1Name: String,
    val team2Name: String,
    val team1Score: Int,
    val team2Score: Int,
    val winnerTeam: String?,
    val isCompleted: Boolean
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    ENTRY_FEE,
    PRIZE_WINNING
}

enum class TransactionStatus {
    PENDING,
    APPROVED,
    COMPLETED,
    REJECTED
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = "",
    val upiId: String = "",
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val description: String = ""
)

data class PlayerBadge(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val requirement: String
)

data class PlayerProfile(
    val ign: String = "ALPHA_PRO_99",
    val uid: String = "2849102847",
    val phoneNumber: String = "9813700369",
    val password: String = "pass123",
    val isLoggedIn: Boolean = false,
    val vaultBalance: Int = 120, // default vault balance in rupees
    val totalEarnings: Int = 850,
    val matchesPlayed: Int = 18,
    val booyahs: Int = 7,
    val kills: Int = 49,
    val badges: List<PlayerBadge> = defaultBadges()
)

fun defaultBadges(): List<PlayerBadge> = listOf(
    PlayerBadge("grandmaster", "Grandmaster Tier", "Top 1% Elite Winner", "👑", true, "Win 5+ Tournaments"),
    PlayerBadge("headshot", "Headshot King", "60%+ Lethal Aim", "🎯", true, "30+ Tournament Kills"),
    PlayerBadge("ace_survivor", "Ace Survivor", "Top 3 Placement Master", "🛡️", true, "Reach Top 3 in 5 matches"),
    PlayerBadge("clash_warlord", "Clash Squad Warlord", "Undefeated in 4v4 CS", "🔥", true, "Win 3 CS matches"),
    PlayerBadge("mvp_week", "MVP of the Week", "Highest Weekly Points", "⚡", true, "Rank #1 on Weekly Board"),
    PlayerBadge("high_roller", "High Roller", "Over ₹500 Won", "💰", true, "Accumulate ₹500+ winnings")
)

data class LeaderboardPlayer(
    val rank: Int,
    val ign: String,
    val kills: Int,
    val points: Int,
    val prize: String,
    val badgeEmoji: String,
    val avatarColor: Long
)

data class AdminConfig(
    val owner1Pin: String = "0105",
    val owner1RequiresPin: Boolean = true,
    val owner2Pin: String = "2424",
    val owner2Name: String = "Harshit VALOR Manager",
    val owner2Phone: String = "7207080543",
    val owner2Enabled: Boolean = true,
    val famPayUpiId: String = "9813700369@fam",
    val supportNumber1: String = "9813700369",
    val supportNumber2: String = "72070 80543",
    val minDeposit: Int = 10,
    val minWithdrawal: Int = 50
)
