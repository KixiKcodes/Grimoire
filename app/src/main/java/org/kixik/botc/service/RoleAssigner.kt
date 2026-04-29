package org.kixik.botc.service

import kotlin.math.roundToInt

data class GameAssignments(
    val playerRoles: Map<String, Character> = emptyMap(),
    val fabled: Character? = null,
    val loric: Character? = null,
    val activeJinxes: List<Jinx> = emptyList(),
    val bluffRoles: List<Character> = emptyList(),
    val amnesiacRole: Character? = null,
    val drunkFake: Character? = null,
    val lunaticFake: Character? = null,
    val marionetteFake: Character? = null,
    val grandmotherTarget: String? = null,
    val evilTwinTarget: String? = null,
    val bootleggerHouseRule: String? = null
)

data class RoleCount(
    val townsfolk: Int,
    val outsiders: Int,
    val minions: Int,
    val demons: Int
)

data class ChosenRoles(
    val playerRoles: List<Character>,
    val bluffRoles: List<Character>,
    val amnesiacRole: Character?,
    val drunkFake: Character?,
    val lunaticFake: Character?,
    val marionetteFake: Character?,
)

class RoleAssigner {
    val players: List<String> = TODO("Get list from setup screen.")

    val roleCountTable: Map<Int, RoleCount> = mapOf(
        5 to RoleCount(4, 0, 0, 1),
        6 to RoleCount(4, 0, 1, 1),
        7 to RoleCount(4, 1, 1, 1),
        8 to RoleCount(5, 1, 1, 1),
        9 to RoleCount(5, 2, 1, 1),
        10 to RoleCount(5, 2, 2, 1),
        11 to RoleCount(6, 2, 2, 1),
        12 to RoleCount(6, 3, 2, 1)
    )

    fun legionRoles(script: Script, players: List<String>, legion: Character): ChosenRoles {
        val playerCount = players.size
        val legionCount = 1.coerceAtLeast((playerCount * 0.66).roundToInt())
        val townsfolkCount = playerCount - legionCount
        val selectedTownsfolk = script.townsfolk.shuffled().take(townsfolkCount)
        val rolesInplay = selectedTownsfolk + List(legionCount) { legion }
        val bluffRoles = script.townsfolk.filterNot { it in selectedTownsfolk }
        return ChosenRoles(
            playerRoles = rolesInplay.shuffled(),
            bluffRoles = bluffRoles,
            amnesiacRole = null,
            drunkFake = null,
            lunaticFake = null,
            marionetteFake = null
        )
    }

    fun pickRoles(script: Script, players: List<String>): ChosenRoles {
        var (townsfolkCount, outsiderCount, minionCount, demonCount) = roleCountTable[players.size]!!

        val demon: Character = script.demons.random()
        if (demon.id == "legion")
            return legionRoles(script, players, demon)
        val selectedMinions = script.minions.shuffled().take(minionCount)
        if (selectedMinions.any { it.id == "baron"}) {
            outsiderCount++;
            townsfolkCount--;
        }
        val selectedOutsiders = script.outsiders.shuffled().take(outsiderCount)
        val selectedTownsfolk = script.townsfolk.shuffled().take(townsfolkCount)

        var amnesiacRole: Character? = null
        if (selectedTownsfolk.any { it.id == "amnesiac" }) {
            val remainingTownsfolk = script.townsfolk - selectedTownsfolk.toSet()
            amnesiacRole = remainingTownsfolk.random()
        }
        var drunkFake: Character? = null
        if (selectedOutsiders.any { it.id == "drunk" }) {
            val remainingTownsfolk = script.townsfolk - selectedTownsfolk.toSet()
            drunkFake = remainingTownsfolk.random()
        }
        var marionetteFake: Character? = null
        if (selectedMinions.any { it.id == "marionette" }) {
            val remainingTownsfolk = script.townsfolk - selectedTownsfolk.toSet()
            marionetteFake = remainingTownsfolk.random()
        }
        var lunaticFake: Character? = null
        if (selectedOutsiders.any { it.id == "lunatic" })
            lunaticFake = script.demons.random()
        val rolesInplay: List<Character> = selectedTownsfolk + selectedOutsiders + selectedMinions + demon
        val goodTeam: List<Character> = selectedTownsfolk + selectedOutsiders
        val allGoodRoles = script.townsfolk + script.outsiders
        val bluffRoles: MutableList<Character> = allGoodRoles.filterNot { it in goodTeam }.toMutableList()
        if (drunkFake != null)
            bluffRoles -= drunkFake
        if (marionetteFake != null)
            bluffRoles -= marionetteFake
        if (amnesiacRole != null)
            bluffRoles -= amnesiacRole
        return ChosenRoles(
            playerRoles = rolesInplay.shuffled(),
            bluffRoles = bluffRoles,
            amnesiacRole = amnesiacRole,
            drunkFake = drunkFake,
            lunaticFake = lunaticFake,
            marionetteFake = marionetteFake
        )
    }

    fun assignRoles(players: List<String>, playerRoles: List<Character>): Map<String, Character> {
        val shuffledPlayers = players.shuffled()
        val shuffledRoles = playerRoles.shuffled()
        return shuffledPlayers.zip(shuffledRoles).toMap()
    }
}
