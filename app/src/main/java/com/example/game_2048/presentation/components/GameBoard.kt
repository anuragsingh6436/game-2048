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

private val BoardShape = RoundedCornerShape(14.dp)
private val CellShape = RoundedCornerShape(10.dp)
private val BOARD_PADDING = 10.dp
private val CELL_SPACING = 8.dp

@Composable
fun GameBoard(
    state: GameState,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    Box(
        modifier = modifier
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

        tiles.forEach { tile ->
            key(tile.id) {
                Box(
                    modifier = Modifier.offset(
                        x = (cellSize + CELL_SPACING) * tile.col,
                        y = (cellSize + CELL_SPACING) * tile.row
                    )
                ) {
                    TileView(tile = tile, cellSize = cellSize)
                }
            }
        }
    }
}
