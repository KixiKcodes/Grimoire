package org.kixik.botc.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.kixik.botc.R
import org.kixik.botc.service.Character
import org.kixik.botc.service.CharacterType
import org.kixik.botc.service.Script
import org.kixik.botc.viewmodel.GameSetupViewModel

@Composable
fun SetupGameScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: GameSetupViewModel = viewModel()
    val scripts by viewModel.scripts.collectAsState()
    var selectedScriptIndex by remember { mutableIntStateOf(-1) }
    val selectedScript by viewModel.selectedScript.collectAsState()
    val playerList by viewModel.playerList.collectAsState()
    val status = when {
        selectedScript == null -> "Please select a script."
        playerList.size < 5 -> "Please add at last 5 players."
        playerList.any { it.isBlank() } -> "Please fill in all player names."
        else -> null
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select script",
                style = MaterialTheme.typography.titleLarge
            )

            if (scripts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors()
                ) {
                    Text(
                        text = "No scripts available.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                ScriptDropdown(
                    scripts = scripts,
                    selectedIndex = selectedScriptIndex,
                    onSelectedIndexChange = {
                        selectedScriptIndex = it
                        viewModel.setSelectedScript(scripts.getOrNull(it))
                    }
                )
                if (selectedScript != null) {
                    ExpandableCard(collapsedText = "Preview script", expandedContent = {
                        ScriptPreview(script = selectedScript!!)
                    })
                }
            }
            Text(
                text = "Add Players",
                style = MaterialTheme.typography.titleLarge
            )
            playerList.forEachIndexed { index, playerName ->
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { newName ->
                        viewModel.updatePlayerName(index, newName)
                    },
                    singleLine = true,
                    label = { Text("Player ${index + 1}") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        FilledIconButton(
                            onClick = {
                                viewModel.removePlayer(index)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove player"
                            )
                        }
                    }
                )
            }
            FilledIconButton(
                onClick = {
                    viewModel.addPlayer()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add player"
                )
            }
        }
        if (status != null) {
            Text(
                text = status,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Button(
            onClick = {},
            enabled = status == null
        ) {
            Text("Assign Roles", fontSize = 16.sp)
        }
    }
}

@Composable
private fun ScriptDropdown(
    scripts: List<Script>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedScript = scripts.getOrNull(selectedIndex)

    Box(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedScript?.name ?: "Select a script")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            scripts.forEachIndexed { index, script ->
                DropdownMenuItem(
                    text = { Text(script.name) },
                    onClick = {
                        onSelectedIndexChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ExpandableCard(
    modifier: Modifier = Modifier,
    collapsedText: String,
    expandedContent: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier
            .fillMaxWidth()
            .clickable() { expanded = !expanded }
    ) {
        AnimatedContent(targetState = expanded) {
            if (it)
                expandedContent()
            else {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = collapsedText,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun ScriptPreview(
    script: Script,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = script.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "by ${script.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ScriptSection(title = "Townsfolk", characters = script.townsfolk)
        ScriptSection(title = "Outsiders", characters = script.outsiders)
        ScriptSection(title = "Minions", characters = script.minions)
        ScriptSection(title = "Demons", characters = script.demons)
    }
}

@Composable
private fun ScriptSection(
    title: String,
    characters: List<Character>,
    modifier: Modifier = Modifier
) {
    if (characters.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenericIcon(type = characters.first().type)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            characters.forEach { character ->
                CharacterRow(character = character)
            }
        }
    }
}

@Composable
private fun CharacterRow(
    character: Character,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = character.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GenericIcon(
    type: CharacterType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconResId = remember(type) {
        characterIconResId(context, Character(id = "", name = "", type = type, description = ""))
    }

    Image(
        painter = painterResource(id = iconResId),
        contentDescription = type.name,
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}

@Composable
private fun CharacterIcon(
    character: Character,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconResId = remember(character.id, character.type) {
        characterIconResId(context, character)
    }

    Image(
        painter = painterResource(id = iconResId),
        contentDescription = character.name,
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}

@Suppress("DiscouragedApi")
private fun characterIconResId(context: Context, character: Character): Int {
    val specificIcon = context.resources.getIdentifier(
        "icon_${character.id}",
        "drawable",
        context.packageName
    )

    if (specificIcon != 0) return specificIcon

    return when (character.type) {
        CharacterType.TOWNSFOLK -> R.drawable.generic_townsfolk
        CharacterType.OUTSIDER -> R.drawable.generic_outsider
        CharacterType.MINION -> R.drawable.generic_minion
        CharacterType.DEMON -> R.drawable.generic_demon
        CharacterType.TRAVELLER -> R.drawable.generic_traveller
        CharacterType.FABLED -> R.drawable.generic_fabled
        CharacterType.LORIC -> R.drawable.generic_loric
    }
}
