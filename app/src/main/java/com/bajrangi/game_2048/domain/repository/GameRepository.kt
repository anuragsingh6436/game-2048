package com.bajrangi.game_2048.domain.repository

interface GameRepository {
    suspend fun getBestScore(): Int
    suspend fun saveBestScore(score: Int)
}
