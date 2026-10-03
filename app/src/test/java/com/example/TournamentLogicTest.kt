package com.example

import com.example.data.BookingResult
import com.example.data.OwnerRole
import com.example.data.TournamentRepository
import com.example.model.GameMode
import com.example.model.MapType
import com.example.model.Tournament
import com.example.model.TournamentStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TournamentLogicTest {

    private lateinit var repository: TournamentRepository

    @Before
    fun setUp() {
        repository = TournamentRepository()
    }

    @Test
    fun testInitialAdminConfigAndPins() {
        val config = repository.adminConfig.value
        assertEquals("0105", config.owner1Pin)
        assertEquals("9813700369@fam", config.famPayUpiId)
        assertEquals("9813700369", config.supportNumber1)
        assertEquals("72070 80543", config.supportNumber2)
        assertEquals(10, config.minDeposit)
        assertEquals(50, config.minWithdrawal)
    }

    @Test
    fun testOwner1LoginAndChangePin() {
        // Login with default private PIN 0105
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "0105"))
        assertEquals(OwnerRole.OWNER_1, repository.activeOwnerRole.value)

        // 1st Owner can change PIN anytime with no limit
        val changeRes = repository.updateOwner1Pin("0105", "9988")
        assertTrue(changeRes.isSuccess)
        assertEquals("9988", repository.adminConfig.value.owner1Pin)

        // Old PIN no longer works
        repository.logoutOwner()
        assertFalse(repository.loginOwner(OwnerRole.OWNER_1, "0105"))
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "9988"))

        // Change again with no limit
        val changeRes2 = repository.updateOwner1Pin("9988", "0105")
        assertTrue(changeRes2.isSuccess)
        assertEquals("0105", repository.adminConfig.value.owner1Pin)
    }

    @Test
    fun testOwner1NoPinCodeMode() {
        // Login with private PIN 0105
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "0105"))

        // Toggle No PIN Code mode
        val toggleRes = repository.setOwner1RequiresPin(false)
        assertTrue(toggleRes.isSuccess)
        assertFalse(repository.adminConfig.value.owner1RequiresPin)

        // Logout
        repository.logoutOwner()

        // 1st Owner can now login with NO PIN CODE
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, ""))
        assertEquals(OwnerRole.OWNER_1, repository.activeOwnerRole.value)

        // Toggle back to require private PIN
        val toggleBack = repository.setOwner1RequiresPin(true)
        assertTrue(toggleBack.isSuccess)
        assertTrue(repository.adminConfig.value.owner1RequiresPin)

        repository.logoutOwner()
        assertFalse(repository.loginOwner(OwnerRole.OWNER_1, ""))
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "0105"))
    }

    @Test
    fun testOwner1Chooses2ndOwner() {
        // Owner 1 configures 2nd Owner
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "0105"))
        val update2nd = repository.updateOwner2Credentials("Custom Manager", "7207080543", "5566", true)
        assertTrue(update2nd.isSuccess)

        assertEquals("Custom Manager", repository.adminConfig.value.owner2Name)
        assertEquals("5566", repository.adminConfig.value.owner2Pin)

        // 2nd Owner can now log in with the PIN set by 1st Owner
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "5566"))
        assertEquals(OwnerRole.OWNER_2, repository.activeOwnerRole.value)
    }

    @Test
    fun testOnlyOwner1CanChangeFamPayQr() {
        // 2nd Owner attempts to change FamPay QR -> Must FAIL
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "2424"))
        val failRes = repository.updateFamPayUpi("hacker@upi")
        assertTrue(failRes.isFailure)
        assertTrue(failRes.exceptionOrNull() is SecurityException)

        // 1st Owner changes FamPay QR -> Must SUCCEED
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "0105"))
        val successRes = repository.updateFamPayUpi("9813700369@fam")
        assertTrue(successRes.isSuccess)
        assertEquals("9813700369@fam", repository.adminConfig.value.famPayUpiId)
    }

    @Test
    fun testTournamentBookingWithBalanceDeduction() {
        val initialBalance = repository.profile.value.vaultBalance // 120
        val targetTournament = repository.tournaments.value.first { it.entryFee == 30 }

        val result = repository.bookTournament(targetTournament.id, "PLAYER_ONE", "11223344")
        assertTrue(result is BookingResult.Success)

        val success = result as BookingResult.Success
        assertEquals(30, success.booking.entryFee)
        assertEquals(initialBalance - 30, repository.profile.value.vaultBalance)
        assertTrue(repository.isUserBooked(targetTournament.id))

        // Verify tournament has this player in its bookedPlayers list
        val updatedTourn = repository.tournaments.value.first { it.id == targetTournament.id }
        assertTrue(updatedTourn.bookedPlayers.any { it.playerUid == "11223344" && it.playerIgn == "PLAYER_ONE" })
    }

    @Test
    fun testInsufficientBalanceBlocksBooking() {
        // Force balance to 5
        repository.requestWithdrawal(repository.profile.value.vaultBalance - 5, "test@fam")

        val expensiveTournament = repository.tournaments.value.first { it.entryFee == 50 }
        val result = repository.bookTournament(expensiveTournament.id, "TESTER", "99999999")

        assertTrue("Should return InsufficientBalance", result is BookingResult.InsufficientBalance)
        val insufficient = result as BookingResult.InsufficientBalance
        assertEquals(50, insufficient.requiredAmount)
    }

    @Test
    fun testDepositMinimum10RupeesAccepted() {
        // Less than 10 must fail
        val failRes1 = repository.addDeposit(9, "UTR123456789")
        assertTrue(failRes1.isFailure)

        val failRes2 = repository.addDeposit(5, "UTR123456789")
        assertTrue(failRes2.isFailure)

        // 10 rupees must succeed (User requested: only pay 10 rupees scanner)
        val balBefore = repository.profile.value.vaultBalance
        val successRes10 = repository.addDeposit(10, "UTR987654321010")
        assertTrue(successRes10.isSuccess)
        assertEquals(balBefore + 10, repository.profile.value.vaultBalance)

        // Custom amount > 10 also succeeds
        val successRes50 = repository.addDeposit(50, "UTR987654321050")
        assertTrue(successRes50.isSuccess)
        assertEquals(balBefore + 60, repository.profile.value.vaultBalance)
    }

    @Test
    fun testWithdrawalMinimumMoreThan50Rupees() {
        // Less than or equal to 50 must fail
        val failRes1 = repository.requestWithdrawal(50, "test@fam")
        assertTrue(failRes1.isFailure)

        val failRes2 = repository.requestWithdrawal(20, "test@fam")
        assertTrue(failRes2.isFailure)

        // More than 50 must succeed
        val balBefore = repository.profile.value.vaultBalance
        val successRes = repository.requestWithdrawal(60, "player@upi")
        assertTrue(successRes.isSuccess)
        assertEquals(balBefore - 60, repository.profile.value.vaultBalance)
    }

    @Test
    fun testBothOwnersCanOrganiseTournaments() {
        val t1 = Tournament(
            id = "test-1",
            title = "Squad Showdown",
            gameMode = GameMode.SQUAD,
            mapType = MapType.BERMUDA,
            matchTime = "Tomorrow 8 PM",
            entryFee = 20,
            prizePool = 500,
            perKillPrize = 10,
            maxSlots = 48,
            bookedSlots = 0,
            status = TournamentStatus.OPEN,
            roomId = "112233",
            roomPassword = "pass"
        )

        // Unauthorized user cannot create
        repository.logoutOwner()
        assertTrue(repository.createTournament(t1).isFailure)

        // Owner 1 can create
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, "0105"))
        assertTrue(repository.createTournament(t1).isSuccess)

        // Owner 2 can create
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "2424"))
        val t2 = t1.copy(id = "test-2", title = "Owner 2 CS Cup")
        assertTrue(repository.createTournament(t2).isSuccess)
    }

    @Test
    fun testPlayerLoginAndSignupFlow() {
        // Initial state is not logged in
        assertFalse(repository.profile.value.isLoggedIn)

        // Login with IGN and password
        val loginRes = repository.loginPlayer("ALPHA_PRO_99", "pass123")
        assertTrue(loginRes.isSuccess)
        assertTrue(repository.profile.value.isLoggedIn)
        assertEquals("ALPHA_PRO_99", repository.profile.value.ign)

        // Logout
        repository.logoutPlayer()
        assertFalse(repository.profile.value.isLoggedIn)

        // Signup new player with ₹20 welcome bonus
        val initialBalance = repository.profile.value.vaultBalance
        val signupRes = repository.signupPlayer("NEW_CHAMP_FF", "2948192847", "9813700369", "mypassword")
        assertTrue(signupRes.isSuccess)
        assertTrue(repository.profile.value.isLoggedIn)
        assertEquals("NEW_CHAMP_FF", repository.profile.value.ign)
        assertEquals("2948192847", repository.profile.value.uid)
        assertEquals(initialBalance + 20, repository.profile.value.vaultBalance)
    }
}
