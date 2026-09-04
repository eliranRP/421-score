package com.eliranrp.score421.ui.feuille

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eliranrp.score421.R
import com.eliranrp.score421.domain.FeuilleState
import com.eliranrp.score421.domain.SpecialMarker
import com.eliranrp.score421.ui.ConfirmAction
import com.eliranrp.score421.ui.theme.Burgundy
import com.eliranrp.score421.ui.theme.ChipIdle
import com.eliranrp.score421.ui.theme.ChipInk
import com.eliranrp.score421.ui.theme.DarkOak
import com.eliranrp.score421.ui.theme.Ink
import com.eliranrp.score421.ui.theme.Oak
import com.eliranrp.score421.ui.theme.Paper
import com.eliranrp.score421.ui.theme.StampRed

@Composable
fun FeuilleScreen(
    state: FeuilleState,
    confirm: ConfirmAction?,
    onCharge: (playerId: String, delta: Int) -> Unit,
    onSpecial: (playerId: String, SpecialMarker) -> Unit,
    onNextRound: () -> Unit,
    onUndo: () -> Unit,
    onRequestConfirm: (ConfirmAction) -> Unit,
    onDismissConfirm: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val untitled = stringResource(R.string.untitled_player)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Header(roundNumber = state.roundNumber)
        Spacer(Modifier.height(8.dp))
        PlayerGrid(
            state = state,
            untitled = untitled,
            landscape = landscape,
            onCharge = onCharge,
            onSpecial = onSpecial,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.height(8.dp))
        ActionBar(
            canUndo = state.canUndo,
            onUndo = onUndo,
            onNextRound = onNextRound,
            onResetRound = { onRequestConfirm(ConfirmAction.ResetRound) },
            onResetTable = { onRequestConfirm(ConfirmAction.ResetTable) },
        )
    }

    if (confirm != null) {
        val title: String
        val body: String
        when (confirm) {
            ConfirmAction.ResetRound -> {
                title = stringResource(R.string.reset_round)
                body = stringResource(R.string.reset_round_confirm)
            }
            ConfirmAction.ResetTable -> {
                title = stringResource(R.string.reset_table)
                body = stringResource(R.string.reset_table_confirm)
            }
        }
        AlertDialog(
            onDismissRequest = onDismissConfirm,
            title = { Text(title, style = MaterialTheme.typography.headlineMedium) },
            text = { Text(body, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.confirm), color = StampRed)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissConfirm) {
                    Text(stringResource(R.string.cancel), color = DarkOak)
                }
            },
            containerColor = Paper,
        )
    }
}

@Composable
private fun Header(roundNumber: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = stringResource(R.string.feuille_subtitle),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(
            text = stringResource(R.string.round_label, roundNumber),
            style = MaterialTheme.typography.headlineMedium,
            color = Burgundy,
        )
    }
}

@Composable
private fun PlayerGrid(
    state: FeuilleState,
    untitled: String,
    landscape: Boolean,
    onCharge: (String, Int) -> Unit,
    onSpecial: (String, SpecialMarker) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (landscape) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.players.forEach { player ->
                PlayerColumn(
                    displayName = state.displayName(player, untitled),
                    charge = state.entry(player.id).charge,
                    special = state.entry(player.id).special,
                    total = state.totalFor(player.id),
                    landscape = true,
                    onCharge = { onCharge(player.id, it) },
                    onSpecial = { onSpecial(player.id, it) },
                    modifier = Modifier
                        .widthIn(min = 200.dp, max = 280.dp)
                        .fillMaxHeight(),
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.players.forEach { player ->
                PlayerColumn(
                    displayName = state.displayName(player, untitled),
                    charge = state.entry(player.id).charge,
                    special = state.entry(player.id).special,
                    total = state.totalFor(player.id),
                    landscape = false,
                    onCharge = { onCharge(player.id, it) },
                    onSpecial = { onSpecial(player.id, it) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PlayerColumn(
    displayName: String,
    charge: Int,
    special: SpecialMarker?,
    total: Int,
    landscape: Boolean,
    onCharge: (Int) -> Unit,
    onSpecial: (SpecialMarker) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Paper)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.running_total, total),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = if (landscape) 36.sp else 40.sp,
            color = Burgundy,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.charge_this_round),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 6.dp),
        ) {
            StepperButton(
                decrease = true,
                enabled = charge > 0,
                onClick = { onCharge(-1) },
            )
            Text(
                text = charge.toString(),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = if (landscape) 48.sp else 56.sp,
                color = Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 72.dp),
            )
            StepperButton(
                decrease = false,
                enabled = true,
                onClick = { onCharge(1) },
            )
        }
        SpecialRow(selected = special, onSpecial = onSpecial)
        if (!landscape) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                color = Oak.copy(alpha = 0.35f),
            )
        }
    }
}

@Composable
private fun StepperButton(
    decrease: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val label = if (decrease) {
        stringResource(R.string.decrease_charge)
    } else {
        stringResource(R.string.increase_charge)
    }
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(76.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (decrease) Oak else Burgundy,
            contentColor = ChipInk,
            disabledContainerColor = Oak.copy(alpha = 0.3f),
        ),
    ) {
        Icon(
            imageVector = if (decrease) Icons.Filled.Remove else Icons.Filled.Add,
            contentDescription = label,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
private fun SpecialRow(
    selected: SpecialMarker?,
    onSpecial: (SpecialMarker) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        SpecialChip(
            label = stringResource(R.string.special_421),
            selected = selected == SpecialMarker.QUATRE_CENT_VINGT_UN,
            onClick = { onSpecial(SpecialMarker.QUATRE_CENT_VINGT_UN) },
            modifier = Modifier.weight(1f),
        )
        SpecialChip(
            label = stringResource(R.string.special_nenette),
            selected = selected == SpecialMarker.NENETTE,
            onClick = { onSpecial(SpecialMarker.NENETTE) },
            modifier = Modifier.weight(1f),
        )
        SpecialChip(
            label = stringResource(R.string.special_macque),
            selected = selected == SpecialMarker.MACQUE,
            onClick = { onSpecial(SpecialMarker.MACQUE) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SpecialChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = ChipIdle,
            labelColor = DarkOak,
            selectedContainerColor = StampRed,
            selectedLabelColor = ChipInk,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Oak,
            selectedBorderColor = StampRed,
        ),
    )
}

@Composable
private fun ActionBar(
    canUndo: Boolean,
    onUndo: () -> Unit,
    onNextRound: () -> Unit,
    onResetRound: () -> Unit,
    onResetTable: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                border = BorderStroke(2.dp, Oak),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkOak),
            ) {
                Text(stringResource(R.string.undo), fontSize = 16.sp)
            }
            Button(
                onClick = onNextRound,
                modifier = Modifier
                    .weight(1.4f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Burgundy,
                    contentColor = ChipInk,
                ),
            ) {
                Text(stringResource(R.string.next_round), fontSize = 18.sp)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onResetRound,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                border = BorderStroke(1.dp, Oak),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkOak),
            ) {
                Text(stringResource(R.string.reset_round))
            }
            OutlinedButton(
                onClick = onResetTable,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                border = BorderStroke(1.dp, StampRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StampRed),
            ) {
                Text(stringResource(R.string.reset_table))
            }
        }
    }
}
