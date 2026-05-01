package org.kixik.botc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.kixik.botc.service.Character
import org.kixik.botc.service.CharacterType
import org.kixik.botc.service.ChosenRoles
import org.kixik.botc.service.GameData
import org.kixik.botc.ui.component.CharacterRow
import org.kixik.botc.ui.component.GenericIcon
import org.kixik.botc.viewmodel.AssignmentPreviewViewModel

@Composable
fun AssignmentPreviewScreen(
    modifier: Modifier = Modifier,
    scriptId: String,
    players: List<String>
) {
    val viewModel: AssignmentPreviewViewModel = viewModel()
    val gameData by viewModel.gameData.collectAsState()

    LaunchedEffect(scriptId, players) {
        viewModel.setupScriptAndPlayers(scriptId, players)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (gameData != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AssignmentPreview(gameData!!)
                    }
                }
            }
        }
        Button(onClick = { viewModel.runAssignment() }) {
            Text("Reassign", fontSize = 16.sp)
        }
    }
}

@Composable
fun AssignmentPreview(
    gameData: GameData
) {
    Text(text = "Good Team", style = MaterialTheme.typography.titleLarge, color = Color.Blue)
    AssignmentSection(title = "Townsfolk", getPlayersOfType(CharacterType.TOWNSFOLK, gameData.playerRoles))
    AssignmentSection(title = "Outsiders", getPlayersOfType(CharacterType.OUTSIDER, gameData.playerRoles))
    Text(text = "Evil Team", style = MaterialTheme.typography.titleLarge, color = Color.Red)
    AssignmentSection(title = "Minions", getPlayersOfType(CharacterType.MINION, gameData.playerRoles))
    AssignmentSection(title = "Demons", getPlayersOfType(CharacterType.DEMON, gameData.playerRoles))
    Text(text = "Fabled", style = MaterialTheme.typography.titleLarge, color = Color.Yellow)
    Text(text = "Loric", style = MaterialTheme.typography.titleLarge, color = Color.Green)
}

@Composable
fun AssignmentSection(
    title: String,
    playerRoles: Map<String, Character>,
    modifier: Modifier = Modifier
) {
    if (playerRoles.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenericIcon(type = playerRoles.values.first().type)
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        playerRoles.forEach { characterRole ->
            CharacterRow(player = characterRole.key, character = characterRole.value)
        }
    }
}

@Stable
fun getPlayersOfType(characterType: CharacterType, playerRoles: Map<String, Character>): Map<String, Character> =
    playerRoles.filter { it.value.type == characterType }
