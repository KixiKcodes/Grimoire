package org.kixik.botc.service

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.kixik.botc.R

enum class CharacterTeam {
    GOOD,
    EVIL,
    NEUTRAL
}

@Serializable
enum class CharacterType(val team: CharacterTeam) {
    @SerialName("townsfolk") TOWNSFOLK(CharacterTeam.GOOD),
    @SerialName("outsider") OUTSIDER(CharacterTeam.GOOD),
    @SerialName("minion") MINION(CharacterTeam.EVIL),
    @SerialName("demon") DEMON(CharacterTeam.EVIL),
    @SerialName("traveller") TRAVELLER(CharacterTeam.NEUTRAL),
    @SerialName("fabled") FABLED(CharacterTeam.NEUTRAL),
    @SerialName("loric") LORIC(CharacterTeam.NEUTRAL)
}

@Serializable
data class CharacterData(val id: String, val name: String, @SerialName("desc") val description: String)

data class Character(val id: String, val name: String, val type: CharacterType, val description: String)

@Serializable
data class JinxData(val a: String, val b: String, @SerialName("desc") val description: String)

data class Jinx(val characters: Pair<Character, Character>, val description: String)

class GameElements(val context: Context) {
    val characters: List<Character> by lazy { loadCharacters() }
    val jinxes: List<Jinx> by lazy { loadJinxes() }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadCharacters(): List<Character> {
        val inputStream = context.resources.openRawResource(R.raw.characters)
        val parsedCharacterData = Json.decodeFromStream<Map<CharacterType, List<CharacterData>>>(inputStream)
        return parsedCharacterData.flatMap { (type, characterDataList) ->
            characterDataList.map { characterData ->
                Character(
                    id = characterData.id,
                    name = characterData.name,
                    type = type,
                    description = characterData.description
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadJinxes(): List<Jinx> {
        val inputStream = context.resources.openRawResource(R.raw.jinxes)
        val parsedJinxData = Json.decodeFromStream<List<JinxData>>(inputStream)
        return parsedJinxData.map { jinxData ->
            Jinx(
                characters = Pair(
                    characters.first { it.id == jinxData.a },
                    characters.first { it.id == jinxData.b }
                ),
                description = jinxData.description
            )
        }
    }
}
