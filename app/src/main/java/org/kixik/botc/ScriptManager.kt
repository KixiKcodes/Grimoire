package org.kixik.botc

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.decodeFromStream

@Serializable
data class ScriptMeta(val id: String, val author: String, val name: String)

@Serializable
data class ScriptFile(val meta: ScriptMeta, val characterIds: List<String>)

data class Script(
    val name: String,
    val author: String,
    val townsfolk: List<Character>,
    val outsiders: List<Character>,
    val minions: List<Character>,
    val demons: List<Character>,
    val travellers: List<Character>,
    val fabled: Character?,
    val loric: Character?,
    val jinxes: List<Jinx>
)

class ScriptManager(private val context: Context, private val gameElements: GameElements) {
    val scripts: List<Script> by lazy { loadScripts() }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadScripts(): List<Script> {
        val scriptFiles = context.assets.list("game_scripts")?.filter { it.endsWith(".json") }.orEmpty()

        return scriptFiles.mapNotNull { fileName ->
            val path = "game_scripts/$fileName"

            val rawArray = try {
                context.assets.open(path).use { input ->
                    Json.decodeFromStream<List<kotlinx.serialization.json.JsonElement>>(input)
                }
            } catch (_: Exception) {
                return@mapNotNull null
            }

            if (rawArray.isEmpty()) return@mapNotNull null

            // Parse metadata object at index 0
            val meta = try {
                Json.decodeFromJsonElement<ScriptMeta>(rawArray.first())
            } catch (_: Exception) {
                return@mapNotNull null
            }

            // Parse remaining elements as string ids
            val ids = rawArray.drop(1).mapNotNull { element ->
                try {
                    Json.decodeFromJsonElement<String>(element)
                } catch (_: Exception) {
                    null
                }
            }

            toScript(
                scriptFile = ScriptFile(meta = meta, characterIds = ids),
                allCharacters = gameElements.characters,
                allJinxes = gameElements.jinxes
            )
        }
    }

    private fun toScript(
        scriptFile: ScriptFile,
        allCharacters: List<Character>,
        allJinxes: List<Jinx>
    ): Script {
        val byId = allCharacters.associateBy { it.id }

        val townsfolk = mutableListOf<Character>()
        val outsiders = mutableListOf<Character>()
        val minions = mutableListOf<Character>()
        val demons = mutableListOf<Character>()
        val travellers = mutableListOf<Character>()
        var fabled: Character? = null
        var loric: Character? = null

        scriptFile.characterIds.forEach { id ->
            val character = byId[id] ?: return@forEach
            when (character.type) {
                CharacterType.TOWNSFOLK -> townsfolk += character
                CharacterType.OUTSIDER -> outsiders += character
                CharacterType.MINION -> minions += character
                CharacterType.DEMON -> demons += character
                CharacterType.TRAVELLER -> travellers += character
                CharacterType.FABLED -> fabled = character
                CharacterType.LORIC -> loric = character
            }
        }

        val base = Script(
            name = scriptFile.meta.name,
            author = scriptFile.meta.author,
            townsfolk = townsfolk,
            outsiders = outsiders,
            minions = minions,
            demons = demons,
            travellers = travellers,
            fabled = fabled,
            loric = loric,
            jinxes = emptyList()
        )

        return base.copy(jinxes = computeJinxes(base, allJinxes))
    }

    private fun computeJinxes(script: Script, allJinxes: List<Jinx>): List<Jinx> {
        if (script.fabled != null && script.fabled.id != "djinn")
            return emptyList()

        val present = buildSet {
            addAll(script.townsfolk)
            addAll(script.outsiders)
            addAll(script.minions)
            addAll(script.demons)
            addAll(script.travellers)
        }

        return allJinxes.filter { jinx ->
            val (a, b) = jinx.characters
            a in present && b in present
        }
    }
}
