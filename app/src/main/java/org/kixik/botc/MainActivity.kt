package org.kixik.botc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.kixik.botc.ui.screen.AssignmentPreviewScreen
import org.kixik.botc.ui.screen.MainMenu
import org.kixik.botc.ui.screen.ScriptsScreen
import org.kixik.botc.ui.screen.SetupGameScreen
import org.kixik.botc.ui.theme.BotCgamemasterTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BotCgamemasterTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val destination = backStackEntry?.destination
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (destination?.hasRoute<MainMenu>() == false) CenterAlignedTopAppBar(
                            title = { Text(getScreenTitle(destination)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { navController.popBackStack() }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun getScreenTitle(destination: NavDestination?): String {
    return when {
        destination?.hasRoute<SetupGame>() == true -> "Set Up Game"
        destination?.hasRoute<AssignmentPreview>() == true -> "Assignment Preview"
        else -> ""
    }
}

@Serializable
data object MainMenu
@Serializable
data object SetupGame
@Serializable
data class AssignmentPreview(val scriptId: String, val players: List<String>)

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainMenu,
        modifier = modifier
    ) {
        composable<MainMenu> {
            MainMenu(
                onSetupGameClick = { navController.navigate(SetupGame) },
                onViewScriptsClick = {  }
            )
        }
        composable<SetupGame> {
            SetupGameScreen(onAssignRoles = { scriptId, players ->
                navController.navigate(AssignmentPreview(scriptId, players))
            })
        }
        composable<AssignmentPreview> {
            val route = it.toRoute<AssignmentPreview>()
            AssignmentPreviewScreen(scriptId = route.scriptId, players = route.players)
        }
    }
}
