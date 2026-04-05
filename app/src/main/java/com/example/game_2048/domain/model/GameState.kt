package com.example.game_2048.domain.model

data class GameState(
    val grid: List<List<Int>> = List(GRID_SIZE) { List(GRID_SIZE) { 0 } },
    val tiles: List<Tile> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false,
    val moveCount: Int = 0
) {
    companion object {
        const val GRID_SIZE = 4
    }
}
