package org.kixik.botc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.kixik.botc.service.GameElements
import org.kixik.botc.service.Script
import org.kixik.botc.service.ScriptManager

class ViewScriptsViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val gameElements = GameElements(appContext)
    private val scriptManager = ScriptManager(appContext, gameElements)

    private val _scripts = MutableStateFlow<List<Script>>(emptyList())
    val scripts = _scripts.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _scripts.value = scriptManager.scripts
            } finally {
                _isLoading.value = false
            }
        }
    }
}

