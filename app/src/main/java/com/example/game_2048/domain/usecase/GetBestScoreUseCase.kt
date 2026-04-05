package com.example.game_2048.domain.usecase

import com.example.game_2048.domain.repository.GameRepository
import javax.inject.Inject

class GetBestScoreUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): Int = repository.getBestScore()
}
