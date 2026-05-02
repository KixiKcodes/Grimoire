package org.kixik.botc.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.kixik.botc.R
import org.kixik.botc.service.Character
import org.kixik.botc.service.CharacterType
import org.kixik.botc.service.GameData
import org.kixik.botc.ui.component.CharacterIcon
import org.kixik.botc.ui.component.GenericIcon
import org.kixik.botc.ui.theme.EvilCharacter
import org.kixik.botc.ui.theme.Fabled
import org.kixik.botc.ui.theme.GoodCharacter
import org.kixik.botc.ui.theme.Loric
import org.kixik.botc.viewmodel.AssignmentPreviewViewModel

@Composable
fun AssignmentPreviewScreen(
    modifier: Modifier = Modifier,
    scriptId: String,
    players: List<String>
) {
    val viewModel: AssignmentPreviewViewModel = viewModel()
    val gameData by viewModel.gameData.collectAsState()
    val cardTexture = ImageBitmap.imageResource(R.drawable.card_texture)
    val brush = remember(cardTexture) { ShaderBrush(shader = ImageShader(cardTexture, tileModeY = TileMode.Mirror)) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (gameData != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = brush, shape = CardDefaults.shape),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompositionLocalProvider(LocalContentColor provides Color.Black) {
                            AssignmentPreview(gameData!!)
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { viewModel.runAssignment() }) {
                Text("Reassign", fontSize = 16.sp)
            }
            Button(onClick = { }) {
                Text("Finish", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun AssignmentPreview(
    gameData: GameData
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = "Good Team",
            style = MaterialTheme.typography.titleLarge,
            color = GoodCharacter
        )
    }

    AssignmentSection(
        title = "Townsfolk",
        characterType = CharacterType.TOWNSFOLK,
        gameData = gameData
    )
    AssignmentSection(
        title = "Outsiders",
        characterType = CharacterType.OUTSIDER,
        gameData = gameData
    )

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = Color.DarkGray
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = "Evil Team",
            style = MaterialTheme.typography.titleLarge,
            color = EvilCharacter
        )
    }

    AssignmentSection(
        title = "Minions",
        characterType = CharacterType.MINION,
        gameData = gameData
    )
    AssignmentSection(
        title = "Demons",
        characterType = CharacterType.DEMON,
        gameData = gameData
    )

    if (gameData.fabled.isNotEmpty() || gameData.loric.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.DarkGray
        )

        if (gameData.fabled.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenericIcon(type = CharacterType.FABLED)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Fabled", style = MaterialTheme.typography.titleMedium, color = Fabled)
            }
            gameData.fabled.forEach { RoleRow(character = it, gameData = gameData) }
        }

        if (gameData.loric.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenericIcon(type = CharacterType.LORIC)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Loric", style = MaterialTheme.typography.titleMedium, color = Loric)
            }
            gameData.loric.forEach { RoleRow(character = it, gameData = gameData) }
        }
    }
}

@Composable
fun AssignmentSection(
    title: String,
    characterType: CharacterType,
    gameData: GameData,
    modifier: Modifier = Modifier
) {
    val relevantRoles = gameData.playerRoles.filter { it.value.type == characterType }
    if (relevantRoles.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenericIcon(type = relevantRoles.values.first().type)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        relevantRoles.forEach { characterRole ->
            RoleRow(player = characterRole.key, character = characterRole.value, gameData = gameData)
        }
    }
}

@Composable
fun RoleRow(
    modifier: Modifier = Modifier,
    player: String? = null,
    character: Character,
    gameData: GameData? = null
) {
    val realRole = when (character.id) {
        "amnesiac" -> gameData?.amnesiacRole
        else -> null
    }
    val fakeRole = when (character.id) {
        "drunk" -> gameData?.drunkFake
        "lunatic" -> gameData?.lunaticFake
        "marionette" -> gameData?.marionetteFake
        else -> null
    }
    val targetPlayer = when (character.id) {
        "grandmother" -> gameData?.grandmotherTarget
        "eviltwin" -> gameData?.evilTwinTarget
        else -> null
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        CharacterIcon(character = character)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (player != null) {
                Text(
                    text = buildAnnotatedString {
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append(player)
                        pop()
                        append(" the ${character.name}")
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            } else Text(text = character.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = character.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            if (realRole != null) {
                Text(
                    text = "Real role",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoleRow(character = realRole)
                }
            }
            if (fakeRole != null) {
                Text(
                    text = "Fake role",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoleRow(character = fakeRole)
                }
            }
            if (targetPlayer != null) {
                Text(
                    text = buildAnnotatedString {
                        append("Target: ")
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append(targetPlayer)
                        pop()
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
