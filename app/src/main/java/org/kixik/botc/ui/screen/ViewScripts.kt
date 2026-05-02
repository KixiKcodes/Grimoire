package org.kixik.botc.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.kixik.botc.R
import org.kixik.botc.service.Character
import org.kixik.botc.service.Script
import org.kixik.botc.ui.component.CharacterIcon
import org.kixik.botc.ui.component.GenericIcon
import org.kixik.botc.viewmodel.ViewScriptsViewModel

@Composable
fun ViewScriptsScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: ViewScriptsViewModel = viewModel()
    val scripts by viewModel.scripts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val orderedScripts = remember(scripts) { prioritizeScripts(scripts) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    val cardTexture = ImageBitmap.imageResource(R.drawable.card_texture)
    val brush = remember(cardTexture) { ShaderBrush(shader = ImageShader(cardTexture, tileModeY = TileMode.Mirror)) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = orderedScripts,
                key = { it.name }
            ) { script ->
                val expanded = expandedStates[script.name] == true
                ExpandableScriptCard(
                    script = script,
                    expanded = expanded,
                    brush = brush,
                    onToggle = { expandedStates[script.name] = !expanded }
                )
            }
        }

        if (isLoading && orderedScripts.isEmpty()) {
            Text(
                text = "Loading scripts...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun prioritizeScripts(scripts: List<Script>): List<Script> {
    val featuredOrder = listOf("Trouble Brewing", "Sects & Violets", "Bad Moon Rising")
    return scripts.sortedWith(
        compareBy<Script> {
            featuredOrder.indexOf(it.name).let { index -> if (index == -1) Int.MAX_VALUE else index }
        }.thenBy { it.name }
    )
}

@Composable
private fun ExpandableScriptCard(
    script: Script,
    expanded: Boolean,
    brush: ShaderBrush,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .background(brush = brush, shape = CardDefaults.shape),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.Black) {
            AnimatedContent(targetState = expanded) { isExpanded ->
                if (isExpanded) {
                    ScriptCardContent(script = script)
                } else {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = script.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ScriptCardContent(script: Script) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        featuredLogoFor(script)?.let { logo ->
            Image(
                painter = painterResource(id = logo),
                contentDescription = "${script.name} logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = script.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "by ${script.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }

        ScriptSection(title = "Townsfolk", characters = script.townsfolk)
        ScriptSection(title = "Outsiders", characters = script.outsiders)
        ScriptSection(title = "Minions", characters = script.minions)
        ScriptSection(title = "Demons", characters = script.demons)
        ScriptSection(title = "Travellers", characters = script.travellers)
        ScriptSection(title = "Fabled", characters = script.fabled)
        ScriptSection(title = "Loric", characters = script.loric)
    }
}

private fun featuredLogoFor(script: Script): Int? {
    return when (script.name) {
        "Trouble Brewing" -> R.drawable.script_trouble_brewing
        "Bad Moon Rising" -> R.drawable.script_bad_moon_rising
        "Sects & Violets" -> R.drawable.script_sects_and_violets
        else -> null
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenericIcon(type = characters.first().type)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            characters.forEach { character ->
                ScriptCharacterRow(character = character)
            }
        }
    }
}

@Composable
private fun ScriptCharacterRow(
    modifier: Modifier = Modifier,
    character: Character
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
                color = Color.DarkGray
            )
        }
    }
}
