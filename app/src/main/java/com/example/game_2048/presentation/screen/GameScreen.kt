package com.example.game_2048.presentation.screen

import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Header ──────────────────────────────────────────────
        Header(
            score = gameState.score,
            bestScore = gameState.bestScore,
            canUndo = viewModel.canUndo(),
            showUndoButton = viewModel.featureFlags.isUndoEnabled(),
            onRestart = { viewModel.startNewGame() },
            onUndo = { viewModel.undoMove() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Board ───────────────────────────────────────────────
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
                            val threshold = 50f
                            val absX = abs(totalDragX)
                            val absY = abs(totalDragY)

                            if (absX > threshold || absY > threshold) {
                                val direction = if (absX > absY) {
                                    if (totalDragX > 0) Direction.RIGHT else Direction.LEFT
                                } else {
                                    if (totalDragY > 0) Direction.DOWN else Direction.UP
                                }

                                val prevScore = gameState.score
                                viewModel.onSwipe(direction)

                                if (viewModel.gameState.value.score > prevScore) {
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

        Spacer(modifier = Modifier.height(28.dp))

        // ── Footer hint ─────────────────────────────────────────
        Text(
            text = "Join the numbers and get to 2048",
            color = gameColors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.1.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // ── Move counter ────────────────────────────────────────
        if (gameState.moveCount > 0) {
            Text(
                text = "${gameState.moveCount} moves",
                color = gameColors.textSecondary.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────

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

    // Row 1: Title + Scores
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "2048",
            color = gameColors.textPrimary,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-2).sp,
            lineHeight = 52.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ScoreCard(label = "SCORE", score = score)
            ScoreCard(label = "BEST", score = bestScore)
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Row 2: Subtitle + Actions
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Swipe to play",
            color = gameColors.textSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showUndoButton) {
                IconActionButton(
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    enabled = canUndo,
                    onClick = onUndo,
                    backgroundColor = gameColors.restartButton
                )
            }

            ActionButton(
                text = "New Game",
                onClick = onRestart,
                backgroundColor = gameColors.restartButton
            )
        }
    }
}

// ─── Buttons ─────────────────────────────────────────────────────────

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun IconActionButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    backgroundColor: Color,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
