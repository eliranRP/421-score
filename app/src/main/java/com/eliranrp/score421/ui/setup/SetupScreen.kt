package com.eliranrp.score421.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eliranrp.score421.R
import com.eliranrp.score421.domain.FeuilleEngine
import com.eliranrp.score421.domain.FeuilleState
import com.eliranrp.score421.ui.theme.Burgundy
import com.eliranrp.score421.ui.theme.ChipInk
import com.eliranrp.score421.ui.theme.DarkOak
import com.eliranrp.score421.ui.theme.Ink
import com.eliranrp.score421.ui.theme.Oak

@Composable
fun SetupScreen(
    state: FeuilleState,
    onAddPlayer: () -> Unit,
    onRemovePlayer: (String) -> Unit,
    onRename: (String, String) -> Unit,
    onOpenFeuille: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.setup_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = DarkOak,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
        )
        Text(
            text = stringResource(R.string.setup_not_a_game),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FilledIconButton(
                onClick = { state.players.lastOrNull()?.let { onRemovePlayer(it.id) } },
                enabled = state.players.size > FeuilleEngine.MIN_PLAYERS,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Oak,
                    contentColor = ChipInk,
                    disabledContainerColor = Oak.copy(alpha = 0.35f),
                ),
            ) {
                Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.remove_player))
            }
            Text(
                text = stringResource(R.string.player_count, state.players.size),
                style = MaterialTheme.typography.headlineLarge,
            )
            FilledIconButton(
                onClick = onAddPlayer,
                enabled = state.players.size < FeuilleEngine.MAX_PLAYERS,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Burgundy,
                    contentColor = ChipInk,
                    disabledContainerColor = Burgundy.copy(alpha = 0.35f),
                ),
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_player))
            }
        }

        Spacer(Modifier.height(20.dp))

        state.players.forEachIndexed { index, player ->
            OutlinedTextField(
                value = player.name,
                onValueChange = { onRename(player.id, it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                label = { Text(stringResource(R.string.player_name_label, index + 1)) },
                placeholder = { Text(stringResource(R.string.player_name_optional)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Burgundy,
                    unfocusedBorderColor = Oak,
                    focusedLabelColor = Burgundy,
                    unfocusedLabelColor = DarkOak,
                    cursorColor = Ink,
                    focusedTextColor = Ink,
                    unfocusedTextColor = Ink,
                ),
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onOpenFeuille,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Burgundy,
                contentColor = ChipInk,
            ),
        ) {
            Text(
                text = stringResource(R.string.open_feuille),
                fontSize = 22.sp,
                style = MaterialTheme.typography.labelLarge,
                color = ChipInk,
            )
        }
    }
}
