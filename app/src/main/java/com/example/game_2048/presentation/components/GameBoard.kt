package com.example.game_2048.presentation.components

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
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.game_2048.domain.model.GameState
import com.example.game_2048.domain.model.GameState.Companion.GRID_SIZE
import com.example.game_2048.domain.model.Tile
import com.example.game_2048.ui.theme.LocalGameColors

@Composable
fun GameBoard(
    state: GameState,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val boardPadding = 8.dp
    val cellSpacing = 6.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = gameColors.boardBackground.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(gameColors.boardBackground)
            .padding(boardPadding)
    ) {
        EmptyCellsGrid(
            cellSpacing = cellSpacing,
            emptyColor = gameColors.emptyCell
        )

        TilesOverlay(
            tiles = state.tiles,
            cellSpacing = cellSpacing
        )
    }
}

@Composable
private fun EmptyCellsGrid(
    cellSpacing: Dp,
    emptyColor: Color
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val totalSpacing = cellSpacing * (GRID_SIZE - 1)
        val cellSize = (maxWidth - totalSpacing) / GRID_SIZE

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                val offsetX = (cellSize + cellSpacing) * col
                val offsetY = (cellSize + cellSpacing) * row

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(cellSize)
                        .clip(RoundedCornerShape(12.dp))
                        .background(emptyColor)
                )
            }
        }
    }
}

@Composable
private fun TilesOverlay(
    tiles: List<Tile>,
    cellSpacing: Dp
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val totalSpacing = cellSpacing * (GRID_SIZE - 1)
        val cellSize = (maxWidth - totalSpacing) / GRID_SIZE

        tiles.forEach { tile ->
            key(tile.id) {
                val offsetX = (cellSize + cellSpacing) * tile.col
                val offsetY = (cellSize + cellSpacing) * tile.row

                Box(
                    modifier = Modifier.offset(x = offsetX, y = offsetY)
                ) {
                    TileView(
                        tile = tile,
                        cellSize = cellSize
                    )
                }
            }
        }
    }
}
