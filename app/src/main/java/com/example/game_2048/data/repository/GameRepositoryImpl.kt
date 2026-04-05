package com.example.game_2048.data.repository

import com.example.game_2048.data.local.ScoreDao
import com.example.game_2048.data.local.ScoreEntity
import com.example.game_2048.domain.repository.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val scoreDao: ScoreDao
) : GameRepository {

    override suspend fun getBestScore(): Int {
        return scoreDao.getBestScore() ?: 0
    }

    override suspend fun saveBestScore(score: Int) {
        val current = scoreDao.getBestScore() ?: 0
        if (score > current) {
            scoreDao.upsert(ScoreEntity(bestScore = score))
        }
    }
}
