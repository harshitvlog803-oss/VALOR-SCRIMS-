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
        assertFalse("Owner 1 requires NO PIN", config.owner1RequiresPin)
        assertEquals("0105", config.owner2Pin)
        assertEquals("9813700369@fam", config.famPayUpiId)
        assertEquals("9813700369", config.supportNumber1)
        assertEquals("72070 80543", config.supportNumber2)
        assertEquals(10, config.minDeposit)
        assertEquals(50, config.minWithdrawal)
        assertEquals(0, repository.profile.value.vaultBalance)
    }

    @Test
    fun testOwner1NoPinAndOwner2Pin0105() {
        // 1st Owner requires NO PIN: 1-tap direct login
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, ""))
        assertEquals(OwnerRole.OWNER_1, repository.activeOwnerRole.value)

        repository.logoutOwner()

        // 2nd Owner requires PIN 0105
        assertFalse(repository.loginOwner(OwnerRole.OWNER_2, "wrong_pin"))
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "0105"))
        assertEquals(OwnerRole.OWNER_2, repository.activeOwnerRole.value)
    }

    @Test
    fun testOwner1Chooses2ndOwner() {
        // Owner 1 configures 2nd Owner
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, ""))
        val update2nd = repository.updateOwner2Credentials("Harshit Manager", "7207080543", "0105", true)
        assertTrue(update2nd.isSuccess)

        assertEquals("Harshit Manager", repository.adminConfig.value.owner2Name)
        assertEquals("0105", repository.adminConfig.value.owner2Pin)

        // 2nd Owner logs in with 0105
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "0105"))
        assertEquals(OwnerRole.OWNER_2, repository.activeOwnerRole.value)
    }

    @Test
    fun testOnlyOwner1CanChangeFamPayQr() {
        // 2nd Owner attempts to change FamPay QR -> Must FAIL
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "0105"))
        val failRes = repository.updateFamPayUpi("hacker@upi")
        assertTrue(failRes.isFailure)
        assertTrue(failRes.exceptionOrNull() is SecurityException)

        // 1st Owner changes FamPay QR -> Must SUCCEED
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, ""))
        val successRes = repository.updateFamPayUpi("9813700369@fam")
        assertTrue(successRes.isSuccess)
        assertEquals("9813700369@fam", repository.adminConfig.value.famPayUpiId)
    }

    @Test
    fun testOnlyOwner2CanOrganiseTournaments() {
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

        // 1st Owner CANNOT organise tournaments ("don't show owner 1 organise")
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_1, ""))
        val failOwner1 = repository.createTournament(t1)
        assertTrue("Owner 1 cannot organise tournaments", failOwner1.isFailure)

        // 2nd Owner CAN organise tournaments ("Only see owner 2 login and organise tournament")
        repository.logoutOwner()
        assertTrue(repository.loginOwner(OwnerRole.OWNER_2, "0105"))
        val successOwner2 = repository.createTournament(t1)
        assertTrue("Owner 2 can organise tournaments", successOwner2.isSuccess)
    }

    @Test
    fun testPlayerVaultBalanceStartsAt0AndRequiresDeposit() {
        // Initial vault balance is strictly 0
        assertEquals(0, repository.profile.value.vaultBalance)

        val targetTournament = repository.tournaments.value.first { it.entryFee == 30 }

        // Booking with 0 balance must fail with InsufficientBalance
        val failBooking = repository.bookTournament(targetTournament.id, "PLAYER_ONE", "11223344")
        assertTrue(failBooking is BookingResult.InsufficientBalance)

        // Player adds money via FamPay QR deposit (e.g. ₹50)
        val depositRes = repository.addDeposit(50, "UTR987654321010")
        assertTrue(depositRes.isSuccess)
        assertEquals(50, repository.profile.value.vaultBalance)

        // Now player can book the tournament!
        val successBooking = repository.bookTournament(targetTournament.id, "PLAYER_ONE", "11223344")
        assertTrue(successBooking is BookingResult.Success)
        assertEquals(20, repository.profile.value.vaultBalance)
    }

    @Test
    fun testDepositMinimum10RupeesAccepted() {
        // Less than 10 must fail
        val failRes1 = repository.addDeposit(9, "UTR123456789")
        assertTrue(failRes1.isFailure)

        // 10 rupees succeeds (User requested minimum scanner payment)
        val successRes10 = repository.addDeposit(10, "UTR987654321010")
        assertTrue(successRes10.isSuccess)
        assertEquals(10, repository.profile.value.vaultBalance)
    }

    @Test
    fun testWithdrawalMinimumMoreThan50Rupees() {
        // Add 100 to balance first
        repository.addDeposit(100, "UTR123456789012")

        // 50 or less must fail
        val failRes1 = repository.requestWithdrawal(50, "test@fam")
        assertTrue(failRes1.isFailure)

        // More than 50 must succeed
        val successRes = repository.requestWithdrawal(60, "player@upi")
        assertTrue(successRes.isSuccess)
        assertEquals(40, repository.profile.value.vaultBalance)
    }

    @Test
    fun testPlayerSignupInitialVaultBalanceIsZero() {
        // Signup new player
        val signupRes = repository.signupPlayer("NEW_CHAMP_FF", "2948192847", "9813700369", "mypassword")
        assertTrue(signupRes.isSuccess)
        assertTrue(repository.profile.value.isLoggedIn)
        assertEquals("NEW_CHAMP_FF", repository.profile.value.ign)
        // Strictly 0 balance!
        assertEquals(0, repository.profile.value.vaultBalance)
    }
}
