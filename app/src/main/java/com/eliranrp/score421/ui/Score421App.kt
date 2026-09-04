package com.eliranrp.score421.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eliranrp.score421.ui.feuille.FeuilleScreen
import com.eliranrp.score421.ui.setup.SetupScreen
import com.eliranrp.score421.ui.theme.Burgundy
import com.eliranrp.score421.ui.theme.Cream
import com.eliranrp.score421.ui.theme.Score421Theme

@Composable
fun Score421App(viewModel: FeuilleViewModel) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    Score421Theme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
                .safeDrawingPadding(),
        ) {
            if (!ui.namesReady) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Burgundy,
                )
            } else when (ui.screen) {
                Screen.Setup -> SetupScreen(
                    state = ui.feuille,
                    onAddPlayer = viewModel::addPlayer,
                    onRemovePlayer = viewModel::removePlayer,
                    onRename = viewModel::renamePlayer,
                    onOpenFeuille = viewModel::openFeuille,
                )
                Screen.Feuille -> FeuilleScreen(
                    state = ui.feuille,
                    confirm = ui.confirm,
                    onCharge = viewModel::addCharge,
                    onSpecial = viewModel::markSpecial,
                    onNextRound = viewModel::nextRound,
                    onUndo = viewModel::undo,
                    onRequestConfirm = viewModel::requestConfirm,
                    onDismissConfirm = viewModel::dismissConfirm,
                    onConfirm = viewModel::confirmPending,
                )
            }
        }
    }
}
