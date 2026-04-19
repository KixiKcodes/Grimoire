package org.kixik.botc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.kixik.botc.ui.theme.BotCgamemasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BotCgamemasterTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

object Routes {
    const val MAINMENU = "main_menu"
    const val SETUPGGAME = "setup_game"
    const val VIEWSCRIPTS = "view_scripts"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAINMENU,
        modifier = modifier
    ) {
        composable(Routes.MAINMENU) {
            MainMenu(
                onSetupGameClick = { navController.navigate(Routes.SETUPGGAME) },
                onViewScriptsClick = { navController.navigate(Routes.VIEWSCRIPTS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETUPGGAME) {
            SetupGameScreen(onBackClick = { navController.navigate(Routes.MAINMENU) })
        }
        composable(Routes.VIEWSCRIPTS) {
            ViewScriptsScreen(onBackClick = { navController.navigate(Routes.MAINMENU) })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBackClick = { navController.navigate(Routes.MAINMENU) })
        }
    }
}

@Composable
fun MainMenu(
    onSetupGameClick: () -> Unit,
    onViewScriptsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_game),
            contentDescription = "Title Image",
            modifier = Modifier.scale(2f)
        )
        Text("Gamemaster Utility App", fontSize = 18.sp, color = Color.White)
        Column(
            modifier = Modifier.width(intrinsicSize = IntrinsicSize.Max)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSetupGameClick,
            ) {
                Text("Set Up Game", fontSize = 16.sp)
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onViewScriptsClick,
            ) {
                Text("View Scripts", fontSize = 16.sp)
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSettingsClick,
            ) {
                Text("Settings", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SetupGameScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Up Game Screen")
        Button(onClick = onBackClick) {
            Text("Back", fontSize = 16.sp)
        }
    }
}

@Composable
fun ViewScriptsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("View Scripts Screen")
        Button(onClick = onBackClick) {
            Text("Back", fontSize = 16.sp)
        }
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Settings Screen")
        Button(onClick = onBackClick) {
            Text("Back", fontSize = 16.sp)
        }
    }
}
