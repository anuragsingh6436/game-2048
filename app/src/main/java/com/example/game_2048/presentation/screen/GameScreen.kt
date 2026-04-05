package com.example.game_2048.presentation.screen

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.presentation.GameViewModel
import com.example.game_2048.presentation.components.GameBoard
import com.example.game_2048.presentation.components.GameOverOverlay
import com.example.game_2048.presentation.components.ScoreCard
import com.example.game_2048.ui.theme.LocalGameColors
import kotlin.math.abs

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameColors = LocalGameColors.current
    val view = LocalView.current

    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(gameColors.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Header(
            score = gameState.score,
            bestScore = gameState.bestScore,
            canUndo = viewModel.canUndo(),
            showUndoButton = viewModel.featureFlags.isUndoEnabled(),
            onRestart = { viewModel.startNewGame() },
            onUndo = { viewModel.undoMove() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Game Board with gestures
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                        },
                        onDragEnd = {
                            val minSwipeDistance = 50f
                            val absX = abs(totalDragX)
                            val absY = abs(totalDragY)

                            if (absX > minSwipeDistance || absY > minSwipeDistance) {
                                val direction = if (absX > absY) {
                                    if (totalDragX > 0) Direction.RIGHT else Direction.LEFT
                                } else {
                                    if (totalDragY > 0) Direction.DOWN else Direction.UP
                                }

                                val prevState = gameState
                                viewModel.onSwipe(direction)

                                // Haptic feedback on merge
                                if (viewModel.gameState.value.score > prevState.score) {
                                    view.performHapticFeedback(
                                        HapticFeedbackConstants.VIRTUAL_KEY
                                    )
                                }
                            }
                        }
                    )
                }
        ) {
            GameBoard(state = gameState)

            GameOverOverlay(
                isGameOver = gameState.isGameOver,
                hasWon = gameState.hasWon && !gameState.isGameOver,
                score = gameState.score,
                onRestart = { viewModel.startNewGame() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions
        Text(
            text = "Swipe to move tiles. Merge same numbers to reach 2048!",
            color = gameColors.textPrimary.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        // Move counter
        Text(
            text = "Moves: ${gameState.moveCount}",
            color = gameColors.textPrimary.copy(alpha = 0.4f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun Header(
    score: Int,
    bestScore: Int,
    canUndo: Boolean,
    showUndoButton: Boolean,
    onRestart: () -> Unit,
    onUndo: () -> Unit
) {
    val gameColors = LocalGameColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Column {
                Text(
                    text = "2048",
                    color = gameColors.textPrimary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 48.sp
                )
            }

            // Score cards
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreCard(label = "SCORE", score = score)
                ScoreCard(label = "BEST", score = bestScore)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showUndoButton) {
                ActionButton(
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    enabled = canUndo,
                    onClick = onUndo,
                    backgroundColor = gameColors.restartButton
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            ActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Game",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "New Game",
                onClick = onRestart,
                backgroundColor = gameColors.restartButton
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    backgroundColor: Color,
    text: String? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        icon()
        if (text != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
