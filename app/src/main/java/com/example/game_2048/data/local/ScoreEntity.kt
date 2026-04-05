package com.example.game_2048.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey val id: Int = 1,
    val bestScore: Int = 0
)
