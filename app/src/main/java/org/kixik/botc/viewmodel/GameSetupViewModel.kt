package org.kixik.botc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kixik.botc.service.GameElements
import org.kixik.botc.service.Script
import org.kixik.botc.service.ScriptManager

class GameSetupViewModel(application: Application) : AndroidViewModel(application) {
	private val appContext = application.applicationContext
	private val gameElements = GameElements(appContext)
	private val scriptManager = ScriptManager(appContext, gameElements)

    private val _scripts = MutableStateFlow<List<Script>>(emptyList())
    val scripts = _scripts.asStateFlow()

    private val _selectedScript = MutableStateFlow<Script?>(null)
    val selectedScript = _selectedScript.asStateFlow()

    private val _playerList = MutableStateFlow<List<String>>(emptyList())
    val playerList = _playerList.asStateFlow()

	init {
        viewModelScope.launch(Dispatchers.IO) {
            _scripts.value = scriptManager.scripts.sortedBy { it.name }
        }
	}

    fun setSelectedScript(script: Script?) {
        _selectedScript.value = script
    }

    fun addPlayer() {
        _playerList.update { it + "" }
    }

    fun removePlayer(index: Int) {
        _playerList.update { list ->
            list.filterIndexed { i, _ -> i != index }
        }
    }

    fun updatePlayerName(index: Int, name: String) {
        _playerList.update { list ->
            list.mapIndexed { i, previousPlayerName ->
                if (i == index) name else previousPlayerName
            }
        }
    }
}
