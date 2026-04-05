package com.example.game_2048.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.ui.theme.AccentGold
import com.example.game_2048.ui.theme.LocalGameColors
import com.example.game_2048.ui.theme.RestartButtonLight

@Composable
fun GameOverOverlay(
    isGameOver: Boolean,
    hasWon: Boolean,
    score: Int,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    AnimatedVisibility(
        visible = isGameOver || hasWon,
        enter = fadeIn(animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (gameColors.isDark) {
                        Color.Black.copy(alpha = 0.75f)
                    } else {
                        Color.White.copy(alpha = 0.75f)
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (hasWon) "You Win!" else "Game Over!",
                    color = if (hasWon) AccentGold else gameColors.textPrimary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Score: $score",
                    color = gameColors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(gameColors.restartButton)
                        .clickable(onClick = onRestart)
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Try Again",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
