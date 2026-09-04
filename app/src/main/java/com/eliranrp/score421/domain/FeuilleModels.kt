package com.eliranrp.score421.domain

data class Player(
    val id: String,
    val name: String,
)

data class PlayerRoundEntry(
    val charge: Int = 0,
    val special: SpecialMarker? = null,
)

data class CompletedRound(
    val number: Int,
    val entries: Map<String, PlayerRoundEntry>,
)

data class FeuilleState(
    val players: List<Player>,
    val completedRounds: List<CompletedRound> = emptyList(),
    val current: Map<String, PlayerRoundEntry> = emptyMap(),
    val canUndo: Boolean = false,
) {
    val roundNumber: Int get() = completedRounds.size + 1

    fun entry(playerId: String): PlayerRoundEntry = current[playerId] ?: PlayerRoundEntry()

    fun totalFor(playerId: String): Int {
        val archived = completedRounds.sumOf { it.entries[playerId]?.charge ?: 0 }
        return archived + (current[playerId]?.charge ?: 0)
    }

    fun totals(): Map<String, Int> = players.associate { it.id to totalFor(it.id) }

    fun displayName(player: Player, untitledPrefix: String): String {
        val trimmed = player.name.trim()
        if (trimmed.isNotEmpty()) return trimmed
        val index = players.indexOfFirst { it.id == player.id }.takeIf { it >= 0 } ?: 0
        return "$untitledPrefix ${index + 1}"
    }
}
