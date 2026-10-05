package com.example.model

enum class GameMode(val displayName: String, val teamSize: Int) {
    LONE_WOLF_1V1_BODY("Lone Wolf 1v1 Body", 1),
    LONE_WOLF_1V1_HEAD("Lone Wolf 1v1 Head", 1),
    SOLO_PER_KILL("Solo Per Kill", 1),
    DUO_PER_KILL("Duo Per Kill", 2),
    CS_1V1_HEAD_UNLIMITED("Clash Squad 1v1 Head Unlimited", 1),
    CS_2V2_BODY("Clash Squad 2v2 Body", 2),
    CS_1V1_BODY("Clash Squad 1v1 Body", 1),
    LONE_WOLF_LOSS_TO_WIN("Lone Wolf Loss to Win", 1),
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

fun defaultRulesForMode(mode: GameMode): List<String> {
    val modeSpecificRules = when (mode) {
        GameMode.LONE_WOLF_1V1_BODY -> listOf(
            "🛡️ Mode Rule: ONLY BODY SHOTS ALLOWED. Headshots are strictly prohibited.",
            "❌ Headshot Penalty: Any headshot elimination forfeits that round to the opponent.",
            "⚔️ Match Format: Best of 9 rounds on Bermuda Iron Cage (First to 5 rounds wins).",
            "🥊 Character Skills: Active skills (Alok, Chrono, Dimitri, K) are OFF for pure gun skill duel.",
            "🔫 Gun Properties: Gun attribute skins are OFF for 100% fair competitive balance.",
            "⏱️ Round Limit: Standard 2 minutes per round; stalling in zone is prohibited."
        )
        GameMode.LONE_WOLF_1V1_HEAD -> listOf(
            "🎯 Mode Rule: ONLY HEADSHOT KILLS COUNT. Pure sniper/one-tap precision duel.",
            "❌ Body Kill Void: Eliminations by body shot damage are void and award the round to opponent.",
            "🔫 Permitted Guns: Desert Eagle, M1887, Woodpecker, AC80, or AWM duels.",
            "🥊 Character Skills: Active skills are turned OFF; passive skills standard.",
            "⚔️ Match Format: Best of 9 rounds on Bermuda Iron Cage (First to 5 rounds wins).",
            "🚫 Fair Play: Emote spamming or gloo wall trapping is strictly monitored."
        )
        GameMode.CS_1V1_HEAD_UNLIMITED -> listOf(
            "💥 Mode Rule: Headshots Only + Unlimited Ammo active.",
            "♾️ Unlimited Ammo: Unlimited Gloo Walls and ammunition enabled in custom room settings.",
            "⚙️ Lobby Settings: 500 Coin start, Limited Ammo: NO, Character Skill: OFF, Gun Attributes: OFF.",
            "🚫 Restricted Weapons: Grenades, flashbangs, smoke spam, and launch pads are strictly forbidden.",
            "⚔️ Win Condition: First player to win 7 rounds is crowned champion."
        )
        GameMode.CS_2V2_BODY -> listOf(
            "⚔️ Mode Rule: 2v2 Body Damage Only. High team coordination & synergy clash.",
            "❌ Headshot Penalty: Intentional headshot elimination awards that round to the opponent duo.",
            "🚫 Roof & High Ground Glitch: Climbing on inaccessible roofs or map exploits is prohibited.",
            "🛡️ Format: Best of 7 rounds Clash Squad on Bermuda.",
            "👥 Roster Policy: Only registered 2 teammates with verified UIDs may participate."
        )
        GameMode.CS_1V1_BODY -> listOf(
            "🛡️ Mode Rule: 1v1 Body Shot Duel. First to win 7 rounds takes the prize pool.",
            "⚙️ Settings: Limited Ammo: YES, Gun attributes: OFF, Character skills: OFF.",
            "🚫 Fair Play: No active abilities (Chrono / Alok / Dimitri / K) allowed.",
            "🏆 Payout: Full prize pool credited directly to winner's Gaming Vault."
        )
        GameMode.SOLO_PER_KILL -> listOf(
            "☠️ Cash Bounty Per Kill: Earn instant cash bounty (₹15 - ₹25) in your Vault for EVERY confirmed elimination!",
            "🏆 Winner Bonus: Booyah winner takes the grand champion pool on top of all kill bounties.",
            "📊 Points System: Placement points (1st=12pts, 2nd=9pts, 3rd=8pts...) + 1 pt per kill.",
            "🚫 Strict Anti-Teaming: Teaming with any player in Solo leads to immediate ban & forfeit.",
            "📸 Verification: Screenshot of final placement and kill summary required for claims."
        )
        GameMode.DUO_PER_KILL -> listOf(
            "👥 Combined Duo Bounty: Both partners accumulate kills together (₹25 per kill).",
            "💰 Instant Payout: Kill bounties & prize pool credited directly to your Gaming Vault.",
            "🔄 Revive Rules: Teammate revives via revive stations are permitted as per room settings.",
            "🤝 Teaming: Both teammates must enter the room with valid registered UIDs."
        )
        GameMode.LONE_WOLF_LOSS_TO_WIN -> listOf(
            "🔄 Challenge Rule: Reverse elimination stakes! High-tension tournament where every round counts.",
            "⚖️ Tie-Breaker: Decided by total cumulative damage dealt across all 9 rounds.",
            "🥊 Fair Play: Deliberate feeding or round throwing is disqualified by match referee.",
            "⏱️ Round Limit: Standard 2 minutes per round limit."
        )
        GameMode.CLASH_SQUAD -> listOf(
            "🥊 Standard Clash Squad: 4v4 Best of 7 rounds format with knockout brackets.",
            "⚙️ Gun Attributes OFF, Character Skills Standard competitive preset.",
            "🏆 Bracket Progression: Winners advance from Quarter Finals to Semi Finals and Grand Finale."
        )
        GameMode.SQUAD -> listOf(
            "🔥 Squad Battle Royale: 12 squads drop, standard competitive esports rules.",
            "📊 Points System: Official Esports Points (Booyah = 12, 2nd = 9, 3rd = 8, Kill = 1 pt).",
            "📻 Voice Chat: In-game team mic recommended for optimal coordination."
        )
        GameMode.SOLO -> listOf(
            "🎯 Classic Solo Scrims: 48 players battle for survival.",
            "🚫 Anti-Teaming: Solo room is strictly monitored by match referees."
        )
        GameMode.DUO -> listOf(
            "👥 Duo Battle Royale: 24 duos battle for Booyah.",
            "🤝 Both players must check in with correct Free Fire UID."
        )
    }

    val commonEsportsRules = listOf(
        "📱 Mobile Devices Only: PC, Emulators, iPads & third-party controllers strictly banned.",
        "🚫 Zero-Tolerance Fair Play: Aimbot, scripts, config files & glitch abuse = instant permanent ban & forfeit.",
        "🔑 Room Credentials: Room ID & Password visible in the app 15 minutes before match start.",
        "⏱️ Punctuality: Join the custom room within 10 minutes of release; late slots are forfeit.",
        "📸 Screenshot Verification: Capture final Booyah & kill summary screenshot for prize claims & dispute resolution.",
        "🤝 Anti-Teaming Rule: Teaming with enemies in Solo or Duo matches will lead to disqualification.",
        "💰 Instant Vault Payout: Cash prizes credited automatically to your Gaming Vault within 30 minutes.",
        "🌐 Disconnections: Personal network ping or device crashes cannot be refunded."
    )

    return modeSpecificRules + commonEsportsRules
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
    val rules: List<String> = defaultRulesForMode(gameMode),
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
    PRIZE_WINNING,
    REFERRAL_BONUS
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
    val vaultBalance: Int = 0, // default vault balance is strictly 0 rupees
    val totalEarnings: Int = 0,
    val matchesPlayed: Int = 0,
    val booyahs: Int = 0,
    val kills: Int = 0,
    val referralCode: String = "VALOR99FF",
    val referralCount: Int = 3,
    val referralEarnings: Int = 45,
    val hasRedeemedReferral: Boolean = false,
    val badges: List<PlayerBadge> = defaultBadges()
)

fun defaultBadges(): List<PlayerBadge> = listOf(
    PlayerBadge("grandmaster", "Grandmaster Tier", "Top 1% Elite Winner", "👑", false, "Win 5+ Tournaments"),
    PlayerBadge("headshot", "Headshot King", "60%+ Lethal Aim", "🎯", false, "30+ Tournament Kills"),
    PlayerBadge("ace_survivor", "Ace Survivor", "Top 3 Placement Master", "🛡️", false, "Reach Top 3 in 5 matches"),
    PlayerBadge("clash_warlord", "Clash Squad Warlord", "Undefeated in 4v4 CS", "🔥", false, "Win 3 CS matches"),
    PlayerBadge("mvp_week", "MVP of the Week", "Highest Weekly Points", "⚡", false, "Rank #1 on Weekly Board"),
    PlayerBadge("high_roller", "High Roller", "Over ₹500 Won", "💰", false, "Accumulate ₹500+ winnings")
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
    val owner1DeveloperCode: String = "0105", // App Developer code for 1st Owner management (private and never displayed)
    val owner2Pin: String = "0105", // 2nd Owner PIN code (private and never displayed)
    val owner2Name: String = "Harshit VALOR Manager",
    val owner2Phone: String = "7207080543",
    val owner2Enabled: Boolean = true,
    val famPayUpiId: String = "9813700369@fam",
    val supportNumber1: String = "9813700369",
    val supportNumber2: String = "72070 80543",
    val minDeposit: Int = 10,
    val minWithdrawal: Int = 50,
    val referralRewardAmount: Int = 15,
    val customRulesMap: Map<GameMode, List<String>> = emptyMap()
)
