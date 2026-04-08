package com.bajrangi.game_2048.domain.usecase

import com.bajrangi.game_2048.domain.repository.GameRepository
import javax.inject.Inject

class SaveBestScoreUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(score: Int) = repository.saveBestScore(score)
}
