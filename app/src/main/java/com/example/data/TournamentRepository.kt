package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class OwnerRole {
    NONE,
    OWNER_1,
    OWNER_2
}

sealed class BookingResult {
    data class Success(val booking: Booking) : BookingResult()
    data class InsufficientBalance(val currentBalance: Int, val requiredAmount: Int) : BookingResult()
    data class Error(val message: String) : BookingResult()
}

class TournamentRepository {

    private val _adminConfig = MutableStateFlow(AdminConfig())
    val adminConfig: StateFlow<AdminConfig> = _adminConfig.asStateFlow()

    private val _profile = MutableStateFlow(PlayerProfile())
    val profile: StateFlow<PlayerProfile> = _profile.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _activeOwnerRole = MutableStateFlow(OwnerRole.NONE)
    val activeOwnerRole: StateFlow<OwnerRole> = _activeOwnerRole.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        // Initial sample clash squad knockout bracket
        val csBracket = listOf(
            BracketMatch("m1", "Quarter Finals", 1, "TEAM GODS", "DEADLY VIPERS", 7, 3, "TEAM GODS", true),
            BracketMatch("m2", "Quarter Finals", 2, "ASSASSINS 99", "DARK PHOENIX", 7, 5, "ASSASSINS 99", true),
            BracketMatch("m3", "Quarter Finals", 3, "ALPHA ESPORTS", "RED DRAGONS", 6, 7, "RED DRAGONS", true),
            BracketMatch("m4", "Quarter Finals", 4, "CYBER TITANS", "NEON WARRIORS", 7, 2, "CYBER TITANS", true),
            BracketMatch("m5", "Semi Finals", 5, "TEAM GODS", "ASSASSINS 99", 7, 6, "TEAM GODS", true),
            BracketMatch("m6", "Semi Finals", 6, "RED DRAGONS", "CYBER TITANS", 4, 7, "CYBER TITANS", true),
            BracketMatch("m7", "Grand Finale", 7, "TEAM GODS", "CYBER TITANS", 7, 5, "TEAM GODS", true)
        )

        // Helper to generate booked player entries
        val sampleNames = listOf(
            "RAISTAR_X", "KILLER_BOY", "GOKU_FF", "VIPER_99", "NINJA_CS", "PRO_HEADSHOT",
            "BLADE_RUNNER", "STORM_RIDER", "CYBER_WOLF", "SHADOW_FF", "TITAN_SLAYER",
            "SNIPER_KING", "APEX_PREDATOR", "FIRE_STORM", "VENOM_STRIKE", "GHOST_OPERATOR",
            "PHOENIX_LORD", "DELTA_FORCE", "VALOR_HERO", "SKULL_CRUSHER", "DARK_KNIGHT",
            "THUNDER_GOD", "EAGLE_EYE", "SAVAGE_BEAST", "ALPHA_VIPER", "IMMORTAL_PRO",
            "SILENT_KILLER", "ROYAL_WARRIOR", "MAFIA_BOSS", "SOLO_GOD", "HEADSHOT_QUEEN",
            "WAR_MACHINE", "BLOOD_HUNTER", "NEON_VIPER", "HYDRA_DOMINATOR", "LEGEND_FF",
            "DEADLY_AIM", "BOOYAH_MASTER", "INFINITY_FF", "BLAZE_FIRE", "OMEGA_WAR", "CYBER_ASSASSIN"
        )

        fun createSampleBookings(tournId: String, title: String, count: Int, fee: Int): List<Booking> {
            return (1..count).map { slot ->
                Booking(
                    id = "bk-$tournId-$slot",
                    tournamentId = tournId,
                    tournamentTitle = title,
                    playerIgn = sampleNames.getOrElse(slot - 1) { "PRO_GAMER_$slot" },
                    playerUid = "284910${1000 + slot}",
                    slotNumber = slot,
                    entryFee = fee,
                    bookedAt = System.currentTimeMillis() - (count - slot) * 1800000L
                )
            }
        }

        val cx101Bookings = createSampleBookings("cx-101", "🔥 VALOR SCRIMS Mega Championship", 42, 30)
        val cx102Bookings = createSampleBookings("cx-102", "⚡ Bermuda 4v4 VALOR CS War", 14, 50)
        val cx103Bookings = createSampleBookings("cx-103", "🎯 Purgatory Sniper Solo Rush", 35, 15)

        val userPastBooking = Booking(
            id = "b-1",
            tournamentId = "cx-104",
            tournamentTitle = "👑 Kalahari Duo Survival Cup",
            playerIgn = _profile.value.ign,
            playerUid = _profile.value.uid,
            slotNumber = 7,
            entryFee = 40,
            bookedAt = System.currentTimeMillis() - 86400000
        )
        val cx104Bookings = createSampleBookings("cx-104", "👑 Kalahari Duo Survival Cup", 23, 40).toMutableList().apply {
            add(6, userPastBooking) // at slot #7
        }

        // Initial realistic VALOR SCRIMS Tournaments with all booked players
        _tournaments.value = listOf(
            Tournament(
                id = "cx-101",
                title = "🔥 VALOR SCRIMS Mega Championship",
                gameMode = GameMode.SQUAD,
                mapType = MapType.BERMUDA,
                matchTime = "Today, 08:30 PM IST",
                entryFee = 30,
                prizePool = 1200,
                perKillPrize = 15,
                maxSlots = 48,
                bookedSlots = 42,
                status = TournamentStatus.OPEN,
                roomId = "8920147",
                roomPassword = "cx99",
                bookedPlayers = cx101Bookings
            ),
            Tournament(
                id = "cx-102",
                title = "⚡ Bermuda 4v4 VALOR CS War",
                gameMode = GameMode.CLASH_SQUAD,
                mapType = MapType.BERMUDA,
                matchTime = "Today, 09:30 PM IST",
                entryFee = 50,
                prizePool = 800,
                perKillPrize = 20,
                maxSlots = 16,
                bookedSlots = 14,
                status = TournamentStatus.OPEN,
                roomId = "7189024",
                roomPassword = "cs44",
                bracketMatches = csBracket,
                bookedPlayers = cx102Bookings
            ),
            Tournament(
                id = "cx-103",
                title = "🎯 Purgatory Sniper Solo Rush",
                gameMode = GameMode.SOLO,
                mapType = MapType.PURGATORY,
                matchTime = "Today, 10:30 PM IST",
                entryFee = 15,
                prizePool = 650,
                perKillPrize = 10,
                maxSlots = 48,
                bookedSlots = 35,
                status = TournamentStatus.OPEN,
                roomId = "4401829",
                roomPassword = "snipe",
                bookedPlayers = cx103Bookings
            ),
            Tournament(
                id = "cx-104",
                title = "👑 Kalahari Duo Survival Cup",
                gameMode = GameMode.DUO,
                mapType = MapType.KALAHARI,
                matchTime = "Yesterday, 09:00 PM IST",
                entryFee = 40,
                prizePool = 1500,
                perKillPrize = 20,
                maxSlots = 24,
                bookedSlots = 24,
                status = TournamentStatus.COMPLETED,
                roomId = "9901823",
                roomPassword = "duo9",
                bookedPlayers = cx104Bookings,
                scoreboards = listOf(
                    ScoreboardEntry(1, "ALPHA_PRO_99", "2849102847", 12, 12, 12, 24, 600),
                    ScoreboardEntry(2, "RAISTAR_X", "1983029102", 9, 9, 9, 18, 350),
                    ScoreboardEntry(3, "KILLER_BOY", "8839201948", 7, 8, 7, 15, 200),
                    ScoreboardEntry(4, "GOKU_FF", "7729103948", 6, 7, 6, 13, 150),
                    ScoreboardEntry(5, "VIPER_99", "6629104829", 4, 6, 4, 10, 100),
                    ScoreboardEntry(6, "NINJA_CS", "5519203948", 3, 5, 3, 8, 50),
                    ScoreboardEntry(7, "PRO_HEADSHOT", "4419203948", 3, 4, 3, 7, 30),
                    ScoreboardEntry(8, "BLADE_RUNNER", "3319203948", 2, 3, 2, 5, 20)
                )
            )
        )

        // Pre-book one completed tournament for user so they see history
        _bookings.value = listOf(userPastBooking)

        // Seed transactions
        _transactions.value = listOf(
            WalletTransaction(
                id = "tx-1",
                type = TransactionType.DEPOSIT,
                amount = 100,
                timestamp = System.currentTimeMillis() - 172800000,
                referenceId = "428910382910",
                upiId = "9813700369@fam",
                status = TransactionStatus.COMPLETED,
                description = "FamPay Deposit (UTR: 428910382910)"
            ),
            WalletTransaction(
                id = "tx-2",
                type = TransactionType.PRIZE_WINNING,
                amount = 600,
                timestamp = System.currentTimeMillis() - 86000000,
                status = TransactionStatus.COMPLETED,
                description = "Rank #1 Booyah Prize: Kalahari Duo Survival"
            ),
            WalletTransaction(
                id = "tx-3",
                type = TransactionType.ENTRY_FEE,
                amount = 40,
                timestamp = System.currentTimeMillis() - 86400000,
                status = TransactionStatus.COMPLETED,
                description = "Entry Fee: Kalahari Duo Survival Cup"
            )
        )
    }

    // --- Booking Logic (Strict vault balance check!) ---
    fun bookTournament(tournamentId: String, ign: String, uid: String): BookingResult {
        val tourn = _tournaments.value.find { it.id == tournamentId }
            ?: return BookingResult.Error("Tournament not found")

        if (tourn.status == TournamentStatus.COMPLETED) {
            return BookingResult.Error("Tournament has already ended")
        }

        if (tourn.bookedSlots >= tourn.maxSlots) {
            return BookingResult.Error("Tournament is full! All slots booked.")
        }

        val alreadyBooked = _bookings.value.any { it.tournamentId == tournamentId && it.playerUid == uid }
        if (alreadyBooked) {
            return BookingResult.Error("You have already booked a slot in this tournament!")
        }

        val currentBalance = _profile.value.vaultBalance
        if (currentBalance < tourn.entryFee) {
            // User requested: "No balance in players vault no book tournament in app"
            return BookingResult.InsufficientBalance(
                currentBalance = currentBalance,
                requiredAmount = tourn.entryFee
            )
        }

        // Deduct entry fee automatically
        val newBalance = currentBalance - tourn.entryFee
        _profile.update {
            it.copy(
                vaultBalance = newBalance,
                ign = ign.ifBlank { it.ign },
                uid = uid.ifBlank { it.uid },
                matchesPlayed = it.matchesPlayed + 1
            )
        }

        val slotNum = tourn.bookedSlots + 1
        val newBooking = Booking(
            id = "bk-${System.currentTimeMillis()}",
            tournamentId = tourn.id,
            tournamentTitle = tourn.title,
            playerIgn = ign,
            playerUid = uid,
            slotNumber = slotNum,
            entryFee = tourn.entryFee
        )

        _bookings.update { it + newBooking }

        // Update tournament booked slot count and append to bookedPlayers
        _tournaments.update { list ->
            list.map {
                if (it.id == tournamentId) it.copy(
                    bookedSlots = it.bookedSlots + 1,
                    bookedPlayers = it.bookedPlayers + newBooking
                ) else it
            }
        }

        // Add transaction entry
        val tx = WalletTransaction(
            id = "tx-${System.currentTimeMillis()}",
            type = TransactionType.ENTRY_FEE,
            amount = tourn.entryFee,
            description = "Entry Fee: ${tourn.title} (Slot #$slotNum)"
        )
        _transactions.update { listOf(tx) + it }

        return BookingResult.Success(newBooking)
    }

    fun isUserBooked(tournamentId: String): Boolean {
        val currentUid = _profile.value.uid
        return _bookings.value.any { it.tournamentId == tournamentId && it.playerUid == currentUid }
    }

    fun getBookingForTournament(tournamentId: String): Booking? {
        val currentUid = _profile.value.uid
        return _bookings.value.find { it.tournamentId == tournamentId && it.playerUid == currentUid }
    }

    // --- Wallet: Deposit via FamPay QR (Min >= 10 rupees) ---
    fun addDeposit(amount: Int, utrNumber: String): Result<Unit> {
        val minDep = _adminConfig.value.minDeposit
        if (amount < minDep) {
            return Result.failure(IllegalArgumentException("Deposit must be at least ₹$minDep rupees!"))
        }
        if (utrNumber.trim().length < 8) {
            return Result.failure(IllegalArgumentException("Please enter a valid 12-digit UTR / Ref Number from FamPay / UPI payment."))
        }

        _profile.update { it.copy(vaultBalance = it.vaultBalance + amount) }

        val tx = WalletTransaction(
            id = "dep-${System.currentTimeMillis()}",
            type = TransactionType.DEPOSIT,
            amount = amount,
            referenceId = utrNumber.trim(),
            upiId = _adminConfig.value.famPayUpiId,
            status = TransactionStatus.COMPLETED,
            description = "FamPay Deposit (UTR: ${utrNumber.trim()})"
        )
        _transactions.update { listOf(tx) + it }
        return Result.success(Unit)
    }

    // --- Wallet: Withdrawal (Min > 50 rupees) ---
    fun requestWithdrawal(amount: Int, upiId: String): Result<Unit> {
        val minWd = _adminConfig.value.minWithdrawal
        if (amount <= minWd) {
            return Result.failure(IllegalArgumentException("Withdrawal must be more than ₹$minWd rupees!"))
        }
        val currentBalance = _profile.value.vaultBalance
        if (amount > currentBalance) {
            return Result.failure(IllegalArgumentException("Insufficient balance! You only have ₹$currentBalance in your vault."))
        }
        if (upiId.trim().isBlank()) {
            return Result.failure(IllegalArgumentException("Please provide a valid UPI ID (e.g., yourname@fam or phone@paytm)"))
        }

        // Deduct from vault balance
        _profile.update { it.copy(vaultBalance = it.vaultBalance - amount) }

        val tx = WalletTransaction(
            id = "wd-${System.currentTimeMillis()}",
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            upiId = upiId.trim(),
            status = TransactionStatus.PENDING,
            description = "Withdrawal request to ${upiId.trim()}"
        )
        _transactions.update { listOf(tx) + it }
        return Result.success(Unit)
    }

    // --- Owner / Admin Operations ---

    fun loginOwner(role: OwnerRole, pin: String): Boolean {
        return when (role) {
            OwnerRole.OWNER_1 -> {
                // If 1st Owner enabled "No PIN code" mode, allow login directly
                if (!_adminConfig.value.owner1RequiresPin || pin.trim() == _adminConfig.value.owner1Pin) {
                    _activeOwnerRole.value = OwnerRole.OWNER_1
                    true
                } else false
            }
            OwnerRole.OWNER_2 -> {
                if (_adminConfig.value.owner2Enabled && pin.trim() == _adminConfig.value.owner2Pin) {
                    _activeOwnerRole.value = OwnerRole.OWNER_2
                    true
                } else false
            }
            OwnerRole.NONE -> {
                _activeOwnerRole.value = OwnerRole.NONE
                true
            }
        }
    }

    fun logoutOwner() {
        _activeOwnerRole.value = OwnerRole.NONE
    }

    // 1 Owner can toggle PIN requirement or use "No PIN code" mode
    fun setOwner1RequiresPin(requiresPin: Boolean): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1) {
            return Result.failure(SecurityException("Only 1st Owner can toggle PIN requirement!"))
        }
        _adminConfig.update { it.copy(owner1RequiresPin = requiresPin) }
        return Result.success(Unit)
    }

    // 1 Owner can change private PIN anytime with no limit
    fun updateOwner1Pin(oldPin: String, newPin: String): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1) {
            return Result.failure(SecurityException("Only 1st Owner can change the Owner 1 PIN!"))
        }
        if (_adminConfig.value.owner1RequiresPin && oldPin != _adminConfig.value.owner1Pin) {
            return Result.failure(IllegalArgumentException("Current PIN is incorrect."))
        }
        if (newPin.trim().length < 4) {
            return Result.failure(IllegalArgumentException("PIN must be at least 4 digits."))
        }
        _adminConfig.update { it.copy(owner1Pin = newPin.trim()) }
        return Result.success(Unit)
    }

    // 1 Owner chooses who is 2 Owner
    fun updateOwner2Credentials(name: String, phone: String, newPin: String, enabled: Boolean): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1) {
            return Result.failure(SecurityException("Only 1st Owner has authority to choose and configure 2nd Owner!"))
        }
        _adminConfig.update {
            it.copy(
                owner2Name = name.trim().ifBlank { it.owner2Name },
                owner2Phone = phone.trim().ifBlank { it.owner2Phone },
                owner2Pin = newPin.trim().ifBlank { it.owner2Pin },
                owner2Enabled = enabled
            )
        }
        return Result.success(Unit)
    }

    // "add 1 owner fam pay QR CODE not change QR by 2 owner right only change QR only for 1 owner"
    fun updateFamPayUpi(newUpi: String): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1) {
            return Result.failure(SecurityException("Permission Denied: Only 1st Owner has the right to change the FamPay QR and UPI ID! 2nd Owner cannot change QR code."))
        }
        if (newUpi.trim().isBlank() || !newUpi.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid UPI ID. Example: 9813700369@fam"))
        }
        _adminConfig.update { it.copy(famPayUpiId = newUpi.trim()) }
        return Result.success(Unit)
    }

    // "add tournament organise option option is only for 1 owner and 2 owner"
    fun createTournament(tournament: Tournament): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1 && _activeOwnerRole.value != OwnerRole.OWNER_2) {
            return Result.failure(SecurityException("Only 1st Owner and 2nd Owner can organise tournaments!"))
        }
        _tournaments.update { listOf(tournament) + it }
        return Result.success(Unit)
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, pass: String): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1 && _activeOwnerRole.value != OwnerRole.OWNER_2) {
            return Result.failure(SecurityException("Only 1st Owner and 2nd Owner can set Room ID & Password!"))
        }
        _tournaments.update { list ->
            list.map {
                if (it.id == tournamentId) it.copy(roomId = roomId.trim(), roomPassword = pass.trim()) else it
            }
        }
        return Result.success(Unit)
    }

    fun updateTournamentStatus(tournamentId: String, newStatus: TournamentStatus): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1 && _activeOwnerRole.value != OwnerRole.OWNER_2) {
            return Result.failure(SecurityException("Only 1st Owner and 2nd Owner can update tournament status!"))
        }
        _tournaments.update { list ->
            list.map {
                if (it.id == tournamentId) it.copy(status = newStatus) else it
            }
        }
        return Result.success(Unit)
    }

    // "2 owner only right to manage tournament payouts and ensure secure earnings distribution for all tournament organizers"
    fun processWithdrawalPayout(transactionId: String, approve: Boolean): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1 && _activeOwnerRole.value != OwnerRole.OWNER_2) {
            return Result.failure(SecurityException("Only authorized Owners can manage payouts!"))
        }
        val tx = _transactions.value.find { it.id == transactionId }
            ?: return Result.failure(IllegalArgumentException("Transaction not found"))

        val newStatus = if (approve) TransactionStatus.APPROVED else TransactionStatus.REJECTED
        _transactions.update { list ->
            list.map {
                if (it.id == transactionId) it.copy(status = newStatus) else it
            }
        }

        // If rejected, refund vault balance
        if (!approve) {
            _profile.update { it.copy(vaultBalance = it.vaultBalance + tx.amount) }
        }
        return Result.success(Unit)
    }

    fun distributePrize(tournamentId: String, winnerIgn: String, prizeAmount: Int): Result<Unit> {
        if (_activeOwnerRole.value != OwnerRole.OWNER_1 && _activeOwnerRole.value != OwnerRole.OWNER_2) {
            return Result.failure(SecurityException("Only authorized Owners can distribute tournament prizes!"))
        }
        // If winner matches current player, credit balance
        if (winnerIgn.equals(_profile.value.ign, ignoreCase = true)) {
            _profile.update {
                it.copy(
                    vaultBalance = it.vaultBalance + prizeAmount,
                    totalEarnings = it.totalEarnings + prizeAmount,
                    booyahs = it.booyahs + 1
                )
            }
        }
        val tx = WalletTransaction(
            id = "win-${System.currentTimeMillis()}",
            type = TransactionType.PRIZE_WINNING,
            amount = prizeAmount,
            status = TransactionStatus.COMPLETED,
            description = "Prize payout distributed to $winnerIgn (Tournament #$tournamentId)"
        )
        _transactions.update { listOf(tx) + it }
        return Result.success(Unit)
    }

    fun updateProfile(ign: String, uid: String) {
        _profile.update {
            it.copy(
                ign = ign.trim().ifBlank { it.ign },
                uid = uid.trim().ifBlank { it.uid }
            )
        }
    }

    fun loginPlayer(ignOrPhone: String, pass: String): Result<Unit> {
        if (ignOrPhone.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your Free Fire IGN or Mobile Number"))
        }
        if (pass.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your password"))
        }

        _profile.update {
            it.copy(
                ign = if (ignOrPhone.all { ch -> ch.isDigit() }) it.ign else ignOrPhone.trim(),
                phoneNumber = if (ignOrPhone.all { ch -> ch.isDigit() }) ignOrPhone.trim() else it.phoneNumber,
                password = pass.trim(),
                isLoggedIn = true
            )
        }
        return Result.success(Unit)
    }

    fun signupPlayer(ign: String, uid: String, phone: String, pass: String): Result<Unit> {
        if (ign.isBlank()) return Result.failure(IllegalArgumentException("Please enter your Free Fire In-Game Name (IGN)"))
        if (uid.isBlank() || uid.length < 6) return Result.failure(IllegalArgumentException("Please enter a valid Free Fire UID (at least 6 digits)"))
        if (phone.isBlank() || phone.length < 10) return Result.failure(IllegalArgumentException("Please enter a valid 10-digit phone number"))
        if (pass.isBlank() || pass.length < 4) return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))

        _profile.update {
            it.copy(
                ign = ign.trim(),
                uid = uid.trim(),
                phoneNumber = phone.trim(),
                password = pass.trim(),
                isLoggedIn = true,
                vaultBalance = it.vaultBalance + 20 // ₹20 Welcome Bonus!
            )
        }
        val bonusTx = WalletTransaction(
            id = "bonus-${System.currentTimeMillis()}",
            type = TransactionType.DEPOSIT,
            amount = 20,
            status = TransactionStatus.COMPLETED,
            description = "🎉 Welcome to VALOR SCRIMS Signup Bonus"
        )
        _transactions.update { listOf(bonusTx) + it }
        return Result.success(Unit)
    }

    fun logoutPlayer() {
        _profile.update { it.copy(isLoggedIn = false) }
    }
}
