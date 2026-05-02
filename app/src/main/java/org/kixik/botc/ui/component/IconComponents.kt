package org.kixik.botc.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.kixik.botc.R
import org.kixik.botc.service.Character
import org.kixik.botc.service.CharacterType

@Composable
fun GenericIcon(
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
fun CharacterIcon(
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
