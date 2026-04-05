package com.example.game_2048.domain.model

data class Tile(
    val id: Long,
    val value: Int,
    val row: Int,
    val col: Int,
    val previousRow: Int = row,
    val previousCol: Int = col,
    val mergedFrom: Boolean = false,
    val isNew: Boolean = false
)
