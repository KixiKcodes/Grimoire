package org.kixik.botc

import android.os.Bundle
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
                val route = backStackEntry?.destination?.route ?: MainMenu
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (route != MainMenu) CenterAlignedTopAppBar(
                            title = { Text(getScreenTitle(route)) },
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

fun getScreenTitle(route: Any): String {
    return when (route) {
        is SetupGame -> "Set Up Game"
        is AssignmentPreview -> "Assignment Preview"
        else -> ""
    }
}

@Serializable
object MainMenu
@Serializable
object SetupGame
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
