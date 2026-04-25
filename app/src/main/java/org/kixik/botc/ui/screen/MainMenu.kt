package org.kixik.botc.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kixik.botc.R

@Composable
fun MainMenu(
    onSetupGameClick: () -> Unit,
    onViewScriptsClick: () -> Unit,
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
        }
    }
}
