package com.eliranrp.score421.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eliranrp.score421.data.PlayerNamesStore
import com.eliranrp.score421.domain.FeuilleEngine
import com.eliranrp.score421.domain.FeuilleState
import com.eliranrp.score421.domain.SpecialMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen { Setup, Feuille }

enum class ConfirmAction { ResetRound, ResetTable }

data class FeuilleUiState(
    val screen: Screen = Screen.Setup,
    val feuille: FeuilleState,
    val confirm: ConfirmAction? = null,
    val namesReady: Boolean = false,
)

class FeuilleViewModel(
    private val namesStore: PlayerNamesStore,
) : ViewModel() {

    private val engine = FeuilleEngine()

    private val _ui = MutableStateFlow(
        FeuilleUiState(feuille = engine.snapshot()),
    )
    val ui: StateFlow<FeuilleUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            val names = namesStore.load()
            if (names.isNotEmpty()) {
                engine.resetTable(names)
            }
            publish(namesReady = true)
        }
    }

    fun addPlayer() {
        engine.addPlayer()
        persistNames()
        publish()
    }

    fun removePlayer(playerId: String) {
        engine.removePlayer(playerId)
        persistNames()
        publish()
    }

    fun renamePlayer(playerId: String, name: String) {
        engine.renamePlayer(playerId, name)
        persistNames()
        publish()
    }

    fun openFeuille() {
        persistNames()
        _ui.update { it.copy(screen = Screen.Feuille, confirm = null) }
    }

    fun addCharge(playerId: String, delta: Int) {
        engine.addCharge(playerId, delta)
        publish()
    }

    fun markSpecial(playerId: String, special: SpecialMarker) {
        engine.markSpecial(playerId, special)
        publish()
    }

    fun nextRound() {
        engine.nextRound()
        publish()
    }

    fun undo() {
        engine.undo()
        persistNames()
        publish()
    }

    fun requestConfirm(action: ConfirmAction) {
        _ui.update { it.copy(confirm = action) }
    }

    fun dismissConfirm() {
        _ui.update { it.copy(confirm = null) }
    }

    fun confirmPending() {
        when (_ui.value.confirm) {
            ConfirmAction.ResetRound -> engine.resetRound()
            ConfirmAction.ResetTable -> {
                engine.resetTable(engine.snapshot().players.map { it.name })
                persistNames()
                _ui.update { it.copy(screen = Screen.Setup, confirm = null, feuille = engine.snapshot()) }
                return
            }
            null -> return
        }
        _ui.update { it.copy(confirm = null, feuille = engine.snapshot()) }
    }

    private fun persistNames() {
        val names = engine.snapshot().players.map { it.name }
        viewModelScope.launch { namesStore.save(names) }
    }

    private fun publish(namesReady: Boolean = true) {
        _ui.update { it.copy(feuille = engine.snapshot(), namesReady = namesReady) }
    }

    companion object {
        fun factory(store: PlayerNamesStore): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FeuilleViewModel(store) as T
                }
            }
    }
}
