package com.bajrangi.game_2048.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScoreDao {

    @Query("SELECT bestScore FROM scores WHERE id = 1")
    suspend fun getBestScore(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(score: ScoreEntity)
}
