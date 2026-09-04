package com.eliranrp.score421.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeuilleEngineTest {

    @Test
    fun startsWithTwoUntitledPlayers() {
        val engine = FeuilleEngine()
        val state = engine.snapshot()
        assertEquals(2, state.players.size)
        assertEquals(1, state.roundNumber)
        assertEquals(0, state.totals().values.sum())
        assertFalse(state.canUndo)
    }

    @Test
    fun totalsAccumulateAcrossRounds() {
        val engine = FeuilleEngine(initialPlayerCount = 3, initialNames = listOf("Ada", "Bo", "Cam"))
        val a = engine.snapshot().players[0].id
        val b = engine.snapshot().players[1].id

        engine.addCharge(a, 4)
        engine.addCharge(b, 2)
        engine.nextRound()
        engine.addCharge(a, 1)
        engine.addCharge(a, 1)

        val totals = engine.snapshot().totals()
        assertEquals(6, totals[a])
        assertEquals(2, totals[b])
        assertEquals(0, totals[engine.snapshot().players[2].id])
        assertEquals(2, engine.snapshot().roundNumber)
    }

    @Test
    fun specialMarkerDoesNotChangeTotals() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        engine.addCharge(player, 3)
        engine.markSpecial(player, SpecialMarker.NENETTE)
        engine.markSpecial(player, SpecialMarker.MACQUE)
        assertEquals(SpecialMarker.MACQUE, engine.snapshot().entry(player).special)
        assertEquals(3, engine.snapshot().totalFor(player))
    }

    @Test
    fun markingSameSpecialTogglesItOff() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        engine.markSpecial(player, SpecialMarker.QUATRE_CENT_VINGT_UN)
        engine.markSpecial(player, SpecialMarker.QUATRE_CENT_VINGT_UN)
        assertNull(engine.snapshot().entry(player).special)
    }

    @Test
    fun undoRevertsLastCharge() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        engine.addCharge(player, 5)
        engine.addCharge(player, 1)
        assertEquals(6, engine.snapshot().totalFor(player))
        assertTrue(engine.undo())
        assertEquals(5, engine.snapshot().totalFor(player))
        assertTrue(engine.undo())
        assertEquals(0, engine.snapshot().totalFor(player))
        assertFalse(engine.undo())
    }

    @Test
    fun undoRevertsNextRound() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        engine.addCharge(player, 8)
        engine.nextRound()
        assertEquals(2, engine.snapshot().roundNumber)
        assertEquals(0, engine.snapshot().entry(player).charge)
        assertEquals(8, engine.snapshot().totalFor(player))
        engine.undo()
        assertEquals(1, engine.snapshot().roundNumber)
        assertEquals(8, engine.snapshot().entry(player).charge)
    }

    @Test
    fun resetRoundClearsCurrentChargesButKeepsArchive() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        engine.addCharge(player, 2)
        engine.nextRound()
        engine.addCharge(player, 7)
        engine.markSpecial(player, SpecialMarker.NENETTE)
        assertTrue(engine.resetRound())
        val state = engine.snapshot()
        assertEquals(0, state.entry(player).charge)
        assertNull(state.entry(player).special)
        assertEquals(2, state.totalFor(player))
        assertEquals(1, state.completedRounds.size)
    }

    @Test
    fun resetRoundIsNoOpWhenAlreadyClear() {
        val engine = FeuilleEngine()
        assertFalse(engine.resetRound())
        assertFalse(engine.snapshot().canUndo)
    }

    @Test
    fun undoResetRound() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        engine.addCharge(player, 4)
        engine.resetRound()
        engine.undo()
        assertEquals(4, engine.snapshot().entry(player).charge)
    }

    @Test
    fun resetTableRestoresSeatCountAndClearsScores() {
        val engine = FeuilleEngine(initialPlayerCount = 4, initialNames = listOf("A", "B", "C", "D"))
        engine.addCharge(engine.snapshot().players[0].id, 9)
        engine.nextRound()
        engine.resetTable(listOf("A", "B", "C", "D"))
        val state = engine.snapshot()
        assertEquals(4, state.players.size)
        assertEquals(listOf("A", "B", "C", "D"), state.players.map { it.name })
        assertEquals(1, state.roundNumber)
        assertEquals(0, state.totals().values.sum())
        assertFalse(state.canUndo)
        assertFalse(engine.undo())
    }

    @Test
    fun addPlayerUpToSix() {
        val engine = FeuilleEngine()
        assertTrue(engine.addPlayer("trois"))
        assertTrue(engine.addPlayer("quatre"))
        assertTrue(engine.addPlayer("cinq"))
        assertTrue(engine.addPlayer("six"))
        assertFalse(engine.addPlayer("trop"))
        assertEquals(6, engine.snapshot().players.size)
    }

    @Test
    fun removePlayerDownToTwo() {
        val engine = FeuilleEngine(initialPlayerCount = 3)
        val third = engine.snapshot().players[2].id
        assertTrue(engine.removePlayer(third))
        assertEquals(2, engine.snapshot().players.size)
        assertFalse(engine.removePlayer(engine.snapshot().players[0].id))
        assertEquals(2, engine.snapshot().players.size)
    }

    @Test
    fun addingPlayerMidGameDoesNotRewritePastTotals() {
        val engine = FeuilleEngine(initialPlayerCount = 2, initialNames = listOf("Ada", "Bo"))
        val ada = engine.snapshot().players[0].id
        engine.addCharge(ada, 5)
        engine.nextRound()
        assertTrue(engine.addPlayer("Cam"))
        val cam = engine.snapshot().players[2].id
        assertEquals(5, engine.snapshot().totalFor(ada))
        assertEquals(0, engine.snapshot().totalFor(cam))
        assertEquals(2, engine.snapshot().roundNumber)
    }

    @Test
    fun removingPlayerDropsTheirHistoryFromTotals() {
        val engine = FeuilleEngine(initialPlayerCount = 3, initialNames = listOf("A", "B", "C"))
        val c = engine.snapshot().players[2].id
        engine.addCharge(c, 10)
        engine.nextRound()
        assertTrue(engine.removePlayer(c))
        assertEquals(2, engine.snapshot().players.size)
        assertFalse(engine.snapshot().totals().containsKey(c))
    }

    @Test
    fun undoAddAndRemovePlayers() {
        val engine = FeuilleEngine()
        engine.addPlayer("Cam")
        assertEquals(3, engine.snapshot().players.size)
        engine.undo()
        assertEquals(2, engine.snapshot().players.size)
        val extra = run {
            engine.addPlayer("Cam")
            engine.snapshot().players[2].id
        }
        engine.removePlayer(extra)
        assertEquals(2, engine.snapshot().players.size)
        engine.undo()
        assertEquals(3, engine.snapshot().players.size)
        assertEquals("Cam", engine.snapshot().players[2].name)
    }

    @Test
    fun chargeCannotGoBelowZero() {
        val engine = FeuilleEngine()
        val player = engine.snapshot().players[0].id
        assertFalse(engine.addCharge(player, -1))
        engine.addCharge(player, 2)
        engine.addCharge(player, -5)
        assertEquals(0, engine.snapshot().entry(player).charge)
    }

    @Test
    fun displayNameFallsBackToUntitledPrefix() {
        val engine = FeuilleEngine()
        val state = engine.snapshot()
        assertEquals("Joueur 1", state.displayName(state.players[0], "Joueur"))
        engine.renamePlayer(state.players[1].id, "  Léa  ")
        val renamed = engine.snapshot()
        assertEquals("Léa", renamed.displayName(renamed.players[1], "Joueur"))
    }
}
