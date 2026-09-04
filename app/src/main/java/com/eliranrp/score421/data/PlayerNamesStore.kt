package com.eliranrp.score421.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eliranrp.score421.domain.FeuilleEngine
import kotlinx.coroutines.flow.first

private val Context.playerNamesDataStore by preferencesDataStore(name = "player_names")

/** Persists last café-table names only — never scores, accounts, or network state. */
class PlayerNamesStore(private val context: Context) {

    suspend fun load(): List<String> {
        val raw = context.playerNamesDataStore.data.first()[KEY].orEmpty()
        if (raw.isEmpty()) return emptyList()
        val names = raw.split(SEPARATOR).map { it.trim() }
        return when {
            names.size < FeuilleEngine.MIN_PLAYERS -> emptyList()
            names.size > FeuilleEngine.MAX_PLAYERS -> names.take(FeuilleEngine.MAX_PLAYERS)
            else -> names
        }
    }

    suspend fun save(names: List<String>) {
        val clipped = names
            .map { it.trim() }
            .let { list ->
                if (list.size < FeuilleEngine.MIN_PLAYERS) {
                    list + List(FeuilleEngine.MIN_PLAYERS - list.size) { "" }
                } else {
                    list.take(FeuilleEngine.MAX_PLAYERS)
                }
            }
        context.playerNamesDataStore.edit { prefs ->
            prefs[KEY] = clipped.joinToString(SEPARATOR)
        }
    }

    companion object {
        private val KEY = stringPreferencesKey("last_player_names")
        private const val SEPARATOR = "\u001e"
    }
}
