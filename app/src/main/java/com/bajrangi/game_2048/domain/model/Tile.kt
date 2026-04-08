package com.bajrangi.game_2048.domain.model

import androidx.compose.runtime.Immutable

@Immutable
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
