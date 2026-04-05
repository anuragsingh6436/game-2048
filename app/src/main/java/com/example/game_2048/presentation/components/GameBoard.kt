package com.example.game_2048.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.game_2048.domain.model.GameState
import com.example.game_2048.domain.model.GameState.Companion.GRID_SIZE
import com.example.game_2048.domain.model.Tile
import com.example.game_2048.ui.theme.LocalGameColors
import kotlinx.coroutines.launch
import kotlin.math.min

private val BoardShape = RoundedCornerShape(14.dp)
private val CellShape = RoundedCornerShape(10.dp)
private val BOARD_PADDING = 10.dp
private val CELL_SPACING = 8.dp

private const val SLIDE_DURATION_MS = 100

@Composable
fun GameBoard(
    state: GameState,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow behind the board
        BoardGlow(glowColor = gameColors.glowBoard)

        // The board itself
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = 4.dp,
                    shape = BoardShape,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.06f)
                )
                .clip(BoardShape)
                .background(gameColors.boardBackground)
                .padding(BOARD_PADDING)
        ) {
            EmptyCellsGrid(emptyColor = gameColors.emptyCell)

            TilesOverlay(tiles = state.tiles)
        }
    }
}

@Composable
private fun BoardGlow(glowColor: Color) {
    val gameColors = LocalGameColors.current
    val alpha = if (gameColors.isDark) 0.25f else 0.35f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = min(size.width, size.height) * 0.58f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = alpha),
                    glowColor.copy(alpha = alpha * 0.3f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = radius
            ),
            radius = radius,
            center = Offset(cx, cy)
        )
    }
}

@Composable
private fun EmptyCellsGrid(emptyColor: Color) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val totalSpacing = CELL_SPACING * (GRID_SIZE - 1)
        val cellSize = (maxWidth - totalSpacing) / GRID_SIZE

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (cellSize + CELL_SPACING) * col,
                            y = (cellSize + CELL_SPACING) * row
                        )
                        .size(cellSize)
                        .clip(CellShape)
                        .background(emptyColor)
                )
            }
        }
    }
}

@Composable
private fun TilesOverlay(tiles: List<Tile>) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val totalSpacing = CELL_SPACING * (GRID_SIZE - 1)
        val cellSize = (maxWidth - totalSpacing) / GRID_SIZE
        val step = cellSize + CELL_SPACING

        tiles.forEach { tile ->
            key(tile.id) {
                AnimatedTile(tile = tile, cellSize = cellSize, step = step)
            }
        }
    }
}

@Composable
private fun AnimatedTile(
    tile: Tile,
    cellSize: Dp,
    step: Dp
) {
    // Animate column and row indices as floats
    // New tiles start at their current position (no slide)
    // Moved tiles start at their previous position and slide to current
    val startCol = if (tile.isNew) tile.col.toFloat() else tile.previousCol.toFloat()
    val startRow = if (tile.isNew) tile.row.toFloat() else tile.previousRow.toFloat()

    val animCol = remember(tile.id) { Animatable(startCol) }
    val animRow = remember(tile.id) { Animatable(startRow) }

    LaunchedEffect(tile.id) {
        if (!tile.isNew && (tile.previousCol != tile.col || tile.previousRow != tile.row)) {
            launch {
                animCol.animateTo(
                    tile.col.toFloat(),
                    tween(SLIDE_DURATION_MS, easing = FastOutSlowInEasing)
                )
            }
            launch {
                animRow.animateTo(
                    tile.row.toFloat(),
                    tween(SLIDE_DURATION_MS, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Box(
        modifier = Modifier.offset(
            x = step * animCol.value,
            y = step * animRow.value
        )
    ) {
        TileView(tile = tile, cellSize = cellSize)
    }
}
