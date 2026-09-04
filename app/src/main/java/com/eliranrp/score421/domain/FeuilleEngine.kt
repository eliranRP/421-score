package com.eliranrp.score421.domain

/**
 * Pure JVM feuille (paper scoresheet) — charges, house-rule markers, undo, reset.
 * No dice, no RNG.
 */
class FeuilleEngine(
    initialPlayerCount: Int = MIN_PLAYERS,
    initialNames: List<String> = emptyList(),
) {
    companion object {
        const val MIN_PLAYERS = 2
        const val MAX_PLAYERS = 6
        private const val UNDO_LIMIT = 80
    }

    private data class Snapshot(
        val players: List<Player>,
        val completedRounds: List<CompletedRound>,
        val current: Map<String, PlayerRoundEntry>,
        val nextId: Int,
    )

    private val undoStack = ArrayDeque<Snapshot>()
    private var nextId = 1
    private var players: MutableList<Player> = mutableListOf()
    private var completedRounds: MutableList<CompletedRound> = mutableListOf()
    private var current: MutableMap<String, PlayerRoundEntry> = mutableMapOf()

    init {
        val count = initialPlayerCount.coerceIn(MIN_PLAYERS, MAX_PLAYERS)
        repeat(count) { index ->
            addBlankPlayer(initialNames.getOrNull(index).orEmpty())
        }
    }

    fun snapshot(): FeuilleState = FeuilleState(
        players = players.toList(),
        completedRounds = completedRounds.map { it.copy(entries = it.entries.toMap()) },
        current = current.toMap(),
        canUndo = undoStack.isNotEmpty(),
    )

    fun addPlayer(name: String = ""): Boolean {
        if (players.size >= MAX_PLAYERS) return false
        pushUndo()
        addBlankPlayer(name)
        return true
    }

    fun removePlayer(playerId: String): Boolean {
        if (players.size <= MIN_PLAYERS) return false
        if (players.none { it.id == playerId }) return false
        pushUndo()
        players.removeAll { it.id == playerId }
        current.remove(playerId)
        completedRounds = completedRounds.map { round ->
            round.copy(entries = round.entries - playerId)
        }.toMutableList()
        return true
    }

    fun renamePlayer(playerId: String, name: String): Boolean {
        val index = players.indexOfFirst { it.id == playerId }
        if (index < 0) return false
        val cleaned = name.trim()
        if (players[index].name == cleaned) return false
        pushUndo()
        players[index] = players[index].copy(name = cleaned)
        return true
    }

    fun addCharge(playerId: String, delta: Int): Boolean {
        val entry = current[playerId] ?: return false
        val next = (entry.charge + delta).coerceAtLeast(0)
        if (next == entry.charge) return false
        pushUndo()
        current[playerId] = entry.copy(charge = next)
        return true
    }

    fun setCharge(playerId: String, charge: Int): Boolean {
        val entry = current[playerId] ?: return false
        val next = charge.coerceAtLeast(0)
        if (next == entry.charge) return false
        pushUndo()
        current[playerId] = entry.copy(charge = next)
        return true
    }

    fun markSpecial(playerId: String, special: SpecialMarker): Boolean {
        val entry = current[playerId] ?: return false
        val next = if (entry.special == special) null else special
        if (next == entry.special) return false
        pushUndo()
        current[playerId] = entry.copy(special = next)
        return true
    }

    fun nextRound(): Boolean {
        pushUndo()
        val number = completedRounds.size + 1
        completedRounds += CompletedRound(number, current.toMap())
        current = players.associate { it.id to PlayerRoundEntry() }.toMutableMap()
        return true
    }

    fun resetRound(): Boolean {
        val alreadyClear = current.values.all { it.charge == 0 && it.special == null }
        if (alreadyClear) return false
        pushUndo()
        current = players.associate { it.id to PlayerRoundEntry() }.toMutableMap()
        return true
    }

    /**
     * Clears scores and undo. Keeps [names] as the new table (2–6 seats).
     */
    fun resetTable(names: List<String> = players.map { it.name }) {
        undoStack.clear()
        nextId = 1
        players = mutableListOf()
        completedRounds = mutableListOf()
        current = mutableMapOf()
        val count = when {
            names.isEmpty() -> MIN_PLAYERS
            else -> names.size.coerceIn(MIN_PLAYERS, MAX_PLAYERS)
        }
        repeat(count) { index ->
            addBlankPlayer(names.getOrNull(index).orEmpty())
        }
    }

    fun undo(): Boolean {
        val previous = undoStack.removeLastOrNull() ?: return false
        players = previous.players.toMutableList()
        completedRounds = previous.completedRounds.toMutableList()
        current = previous.current.toMutableMap()
        nextId = previous.nextId
        return true
    }

    private fun addBlankPlayer(name: String) {
        val id = "p${nextId++}"
        players += Player(id, name.trim())
        current[id] = PlayerRoundEntry()
    }

    private fun pushUndo() {
        undoStack.addLast(
            Snapshot(
                players = players.map { it.copy() },
                completedRounds = completedRounds.map { it.copy(entries = it.entries.toMap()) },
                current = current.toMap(),
                nextId = nextId,
            ),
        )
        while (undoStack.size > UNDO_LIMIT) {
            undoStack.removeFirst()
        }
    }
}
