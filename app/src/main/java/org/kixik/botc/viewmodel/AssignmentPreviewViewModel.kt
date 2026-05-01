package org.kixik.botc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.kixik.botc.service.GameData
import org.kixik.botc.service.GameElements
import org.kixik.botc.service.RoleAssigner
import org.kixik.botc.service.Script
import org.kixik.botc.service.ScriptManager

class AssignmentPreviewViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val gameElements = GameElements(appContext)
    private val scriptManager = ScriptManager(appContext, gameElements)
    private val roleAssigner = RoleAssigner(gameElements)

    private lateinit var script: Script
    private lateinit var players: List<String>

    private val _gameData = MutableStateFlow<GameData?>(null)
    val gameData = _gameData.asStateFlow()

    fun setupScriptAndPlayers(scriptId: String, playerNames: List<String>) {
        script = scriptManager.getScriptById(scriptId) ?: return
        players = playerNames
        runAssignment()
    }

    fun runAssignment() {
        _gameData.value = roleAssigner.generateGameData(players, script)
    }
}
