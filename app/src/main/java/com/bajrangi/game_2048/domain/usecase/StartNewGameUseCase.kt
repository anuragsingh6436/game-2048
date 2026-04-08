package com.bajrangi.game_2048.domain.usecase

import com.bajrangi.game_2048.domain.engine.GameEngine
import com.bajrangi.game_2048.domain.model.GameState
import javax.inject.Inject

class StartNewGameUseCase @Inject constructor(
    private val engine: GameEngine
) {
    operator fun invoke(): GameState = engine.createInitialState()
}
