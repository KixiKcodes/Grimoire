package org.kixik.botc.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import org.kixik.botc.viewmodel.AssignmentPreviewViewModel

@Composable
fun AssignmentPreviewScreen(scriptId: String, players: List<String>) {
    val viewModel: AssignmentPreviewViewModel = viewModel()

    LaunchedEffect(scriptId, players) {
        viewModel.setupScriptAndPlayers(scriptId, players)
    }
}
