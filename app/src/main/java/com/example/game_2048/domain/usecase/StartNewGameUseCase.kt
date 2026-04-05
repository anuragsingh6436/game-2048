package com.example.game_2048.domain.usecase

import com.example.game_2048.domain.engine.GameEngine
import com.example.game_2048.domain.model.GameState
import javax.inject.Inject

class StartNewGameUseCase @Inject constructor(
    private val engine: GameEngine
) {
    operator fun invoke(): GameState = engine.createInitialState()
}
