package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookingResult
import com.example.data.OwnerRole
import com.example.data.TournamentRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Login : Screen()
    data object Home : Screen()
    data class TournamentDetail(val tournamentId: String) : Screen()
    data object Wallet : Screen()
    data object Leaderboard : Screen()
    data object Profile : Screen()
    data object Support : Screen()
    data object Admin : Screen()
    data object OrganiseTournament : Screen()
}

class TournamentViewModel(
    val repository: TournamentRepository = TournamentRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(
        if (repository.profile.value.isLoggedIn) Screen.Home else Screen.Login
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedModeFilter = MutableStateFlow<GameMode?>(null)
    val selectedModeFilter: StateFlow<GameMode?> = _selectedModeFilter.asStateFlow()

    private val _isWhiteBackground = MutableStateFlow(false)
    val isWhiteBackground: StateFlow<Boolean> = _isWhiteBackground.asStateFlow()

    fun toggleAppTheme() {
        _isWhiteBackground.value = !_isWhiteBackground.value
        val mode = if (_isWhiteBackground.value) "White Theme" else "Dark Gaming Theme"
        showToast("🎨 Switched to $mode")
    }

    fun setWhiteTheme(enabled: Boolean) {
        _isWhiteBackground.value = enabled
    }

    fun quickGuestLogin(ign: String = "GAMER_${(1000..9999).random()}") {
        val res = repository.signupPlayer(
            ign = ign,
            uid = "${(1000000000L..9999999999L).random()}",
            phone = "98765${(10000..99999).random()}",
            pass = "pass123"
        )
        if (res.isSuccess) {
            showToast("⚡ Quick Login as ${repository.profile.value.ign}!")
            navigateTo(Screen.Home)
        } else {
            repository.loginPlayer("ALPHA_PRO_99", "pass123")
            showToast("⚡ Logged in successfully!")
            navigateTo(Screen.Home)
        }
    }

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    val profile = repository.profile
    val adminConfig = repository.adminConfig
    val activeOwnerRole = repository.activeOwnerRole
    val bookings = repository.bookings
    val transactions = repository.transactions

    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        repository.tournaments,
        _selectedModeFilter
    ) { tournaments, filter ->
        if (filter == null) tournaments else tournaments.filter { it.gameMode == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeWalletTab = MutableStateFlow(0)
    val activeWalletTab: StateFlow<Int> = _activeWalletTab.asStateFlow()

    fun setActiveWalletTab(tab: Int) {
        _activeWalletTab.value = tab
    }

    fun navigateToWallet(tabIndex: Int = 0) {
        _activeWalletTab.value = tabIndex
        navigateTo(Screen.Wallet)
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun loginPlayer(ignOrPhone: String, pass: String) {
        val res = repository.loginPlayer(ignOrPhone, pass)
        if (res.isSuccess) {
            showToast("🎮 Welcome back, ${repository.profile.value.ign}!")
            navigateTo(Screen.Home)
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
        }
    }

    fun signupPlayer(ign: String, uid: String, phone: String, pass: String) {
        val res = repository.signupPlayer(ign, uid, phone, pass)
        if (res.isSuccess) {
            showToast("🎉 Account created! ₹20 bonus added to your vault.")
            navigateTo(Screen.Home)
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
        }
    }

    fun logoutPlayer() {
        repository.logoutPlayer()
        showToast("Signed out of gamer account")
        navigateTo(Screen.Login)
    }

    fun openOrganiseTournament(onRequireAuth: () -> Unit) {
        val role = repository.activeOwnerRole.value
        if (role == OwnerRole.OWNER_1 || role == OwnerRole.OWNER_2) {
            navigateTo(Screen.OrganiseTournament)
        } else {
            onRequireAuth()
        }
    }

    fun setModeFilter(mode: GameMode?) {
        _selectedModeFilter.value = mode
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    // --- Slot Booking with Automatic Balance Deduction ---
    fun bookSlot(tournamentId: String, ign: String, uid: String, onResult: (BookingResult) -> Unit) {
        val result = repository.bookTournament(tournamentId, ign, uid)
        onResult(result)
        when (result) {
            is BookingResult.Success -> {
                showToast("🎉 Slot #${result.booking.slotNumber} booked successfully! Entry fee ₹${result.booking.entryFee} deducted.")
            }
            is BookingResult.InsufficientBalance -> {
                showToast("⚠️ Insufficient vault balance! Required: ₹${result.requiredAmount}, Available: ₹${result.currentBalance}")
            }
            is BookingResult.Error -> {
                showToast("❌ ${result.message}")
            }
        }
    }

    // --- FamPay QR Deposit (More than 10 rupees) ---
    fun submitDeposit(amount: Int, utr: String, onComplete: (Boolean, String) -> Unit) {
        val res = repository.addDeposit(amount, utr)
        if (res.isSuccess) {
            showToast("✅ Deposit of ₹$amount added to your vault successfully!")
            onComplete(true, "Deposit added!")
        } else {
            val err = res.exceptionOrNull()?.message ?: "Deposit failed"
            showToast("❌ $err")
            onComplete(false, err)
        }
    }

    // --- Withdrawal (More than 50 rupees) ---
    fun submitWithdrawal(amount: Int, upiId: String, onComplete: (Boolean, String) -> Unit) {
        val res = repository.requestWithdrawal(amount, upiId)
        if (res.isSuccess) {
            showToast("✅ Withdrawal request of ₹$amount submitted! Processing via UPI.")
            onComplete(true, "Request submitted!")
        } else {
            val err = res.exceptionOrNull()?.message ?: "Withdrawal failed"
            showToast("❌ $err")
            onComplete(false, err)
        }
    }

    // --- Referral Redemption ---
    fun redeemReferralCode(code: String, onComplete: (Boolean, String) -> Unit) {
        val res = repository.redeemReferralCode(code)
        if (res.isSuccess) {
            showToast("🎁 Referral Code Applied! ₹15 added to your Vault!")
            onComplete(true, "₹15 Bonus Added!")
        } else {
            val err = res.exceptionOrNull()?.message ?: "Invalid Referral Code"
            showToast("❌ $err")
            onComplete(false, err)
        }
    }

    // --- Admin Authentication & Controls ---
    fun loginOwner(role: OwnerRole, pin: String): Boolean {
        val success = repository.loginOwner(role, pin)
        if (success) {
            showToast("🔓 Authenticated as ${if (role == OwnerRole.OWNER_1) "1st Owner (App Developer)" else "2nd Owner (Tournament Manager)"}")
        } else {
            showToast(if (role == OwnerRole.OWNER_1) "❌ Access Denied: Only App Developer can log in to 1st Owner management!" else "❌ Incorrect 2nd Owner PIN code!")
        }
        return success
    }

    fun logoutOwner() {
        repository.logoutOwner()
        showToast("Logged out of Owner Panel")
    }

    fun updateOwner2Credentials(name: String, phone: String, pin: String, enabled: Boolean): Boolean {
        val res = repository.updateOwner2Credentials(name, phone, pin, enabled)
        return if (res.isSuccess) {
            showToast("✅ 2nd Owner credentials updated by 1st Owner")
            true
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
            false
        }
    }

    fun updateFamPayUpi(newUpi: String): Boolean {
        val res = repository.updateFamPayUpi(newUpi)
        return if (res.isSuccess) {
            showToast("✅ FamPay UPI ID & QR Code updated to $newUpi")
            true
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
            false
        }
    }

    fun updateCustomRules(mode: GameMode, rules: List<String>): Boolean {
        val res = repository.updateCustomTournamentRules(mode, rules)
        return if (res.isSuccess) {
            showToast("✅ Customized rules for ${mode.displayName} saved by 1st Owner!")
            true
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
            false
        }
    }

    fun updateAppFeatures(support1: String, support2: String, minDep: Int, minWith: Int, referralReward: Int): Boolean {
        val res = repository.updateAppFeatures(support1, support2, minDep, minWith, referralReward)
        return if (res.isSuccess) {
            showToast("✅ App features & limits customized by 1st Owner!")
            true
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
            false
        }
    }

    fun createTournament(tournament: Tournament): Boolean {
        val res = repository.createTournament(tournament)
        return if (res.isSuccess) {
            showToast("✅ Tournament '${tournament.title}' organized successfully!")
            true
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
            false
        }
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, pass: String): Boolean {
        val res = repository.updateRoomCredentials(tournamentId, roomId, pass)
        return if (res.isSuccess) {
            showToast("✅ Room ID ($roomId) and Password set!")
            true
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
            false
        }
    }

    fun processWithdrawal(txId: String, approve: Boolean) {
        val res = repository.processWithdrawalPayout(txId, approve)
        if (res.isSuccess) {
            showToast(if (approve) "✅ Payout Approved & Transferred!" else "❌ Payout Rejected & Refunded.")
        } else {
            showToast("❌ ${res.exceptionOrNull()?.message}")
        }
    }

    fun updateProfile(ign: String, uid: String) {
        repository.updateProfile(ign, uid)
        showToast("✅ Profile updated: $ign ($uid)")
    }
}
