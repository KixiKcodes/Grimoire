package org.kixik.botc.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.kixik.botc.service.GameAssignments
import org.kixik.botc.service.GameElements
import org.kixik.botc.service.RoleAssigner
import org.kixik.botc.service.Script
import org.kixik.botc.service.ScriptManager

class AssignmentPreviewViewModel : ViewModel() {
    private val roleAssigner = RoleAssigner()

    private val _assignments = MutableStateFlow<GameAssignments?>(null)
    val assignments = _assignments.asStateFlow()

    private lateinit var script: Script
    private lateinit var players: List<String>

    fun setupScriptAndPlayers(scriptId: String, playerNames: List<String>) {
        script = ScriptManager.getScriptById(scriptId) ?: return
        runAssignment()
    }

    fun runAssignment() {

    }
}
