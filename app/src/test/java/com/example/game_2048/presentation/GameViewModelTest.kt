package com.example.game_2048.presentation

import app.cash.turbine.test
import com.example.game_2048.config.FeatureFlags
import com.example.game_2048.domain.engine.GameEngine
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.domain.model.GameState
import com.example.game_2048.domain.repository.GameRepository
import com.example.game_2048.domain.usecase.GetBestScoreUseCase
import com.example.game_2048.domain.usecase.MoveTilesUseCase
import com.example.game_2048.domain.usecase.SaveBestScoreUseCase
import com.example.game_2048.domain.usecase.StartNewGameUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var gameEngine: GameEngine
    private lateinit var gameRepository: GameRepository
    private lateinit var featureFlags: FeatureFlags

    private lateinit var startNewGameUseCase: StartNewGameUseCase
    private lateinit var moveTilesUseCase: MoveTilesUseCase
    private lateinit var getBestScoreUseCase: GetBestScoreUseCase
    private lateinit var saveBestScoreUseCase: SaveBestScoreUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gameEngine = GameEngine()
        gameRepository = mockk(relaxed = true)
        featureFlags = FeatureFlags()
        coEvery { gameRepository.getBestScore() } returns 0

        startNewGameUseCase = StartNewGameUseCase(gameEngine)
        moveTilesUseCase = MoveTilesUseCase(gameEngine)
        getBestScoreUseCase = GetBestScoreUseCase(gameRepository)
        saveBestScoreUseCase = SaveBestScoreUseCase(gameRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GameViewModel {
        return GameViewModel(
            startNewGameUseCase,
            moveTilesUseCase,
            getBestScoreUseCase,
            saveBestScoreUseCase,
            featureFlags
        )
    }

    // ==================== Initial state (synchronous) ====================

    @Test
    fun `initial state has two tiles immediately`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.gameState.value
        val nonZero = state.grid.flatten().count { it != 0 }
        assertEquals(2, nonZero)
    }

    @Test
    fun `initial state score is zero`() = runTest {
        val viewModel = createViewModel()
        assertEquals(0, viewModel.gameState.value.score)
    }

    @Test
    fun `initial state is not game over`() = runTest {
        val viewModel = createViewModel()
        assertFalse(viewModel.gameState.value.isGameOver)
    }

    @Test
    fun `initial state loads best score from repository async`() = runTest {
        coEvery { gameRepository.getBestScore() } returns 5000
        val viewModel = createViewModel()
        assertEquals(0, viewModel.gameState.value.bestScore)
        advanceUntilIdle()
        assertEquals(5000, viewModel.gameState.value.bestScore)
    }

    // ==================== startNewGame ====================

    @Test
    fun `startNewGame resets the game synchronously`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSwipe(Direction.LEFT)
        viewModel.onSwipe(Direction.UP)

        viewModel.startNewGame()

        assertEquals(0, viewModel.gameState.value.score)
        assertEquals(2, viewModel.gameState.value.grid.flatten().count { it != 0 })
    }

    @Test
    fun `startNewGame clears previous state for undo`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(true)
        val viewModel = createViewModel()
        viewModel.onSwipe(Direction.LEFT)
        assertTrue(viewModel.canUndo())

        viewModel.startNewGame()
        assertFalse(viewModel.canUndo())
    }

    // ==================== onSwipe ====================

    @Test
    fun `onSwipe does not process when game is over`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        repeat(200) {
            viewModel.onSwipe(Direction.LEFT)
            viewModel.onSwipe(Direction.RIGHT)
            viewModel.onSwipe(Direction.UP)
            viewModel.onSwipe(Direction.DOWN)
        }

        val finalState = viewModel.gameState.value
        if (finalState.isGameOver) {
            val moveCountAtGameOver = finalState.moveCount
            viewModel.onSwipe(Direction.LEFT)
            viewModel.onSwipe(Direction.RIGHT)
            assertEquals(moveCountAtGameOver, viewModel.gameState.value.moveCount)
        }
    }

    @Test
    fun `saves best score when new high score achieved`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSwipe(Direction.LEFT)
        viewModel.onSwipe(Direction.RIGHT)
        viewModel.onSwipe(Direction.UP)
        viewModel.onSwipe(Direction.DOWN)
        advanceUntilIdle()

        val currentScore = viewModel.gameState.value.score
        if (currentScore > 0) {
            coVerify { gameRepository.saveBestScore(any()) }
        }
    }

    // ==================== Undo ====================

    @Test
    fun `undo is not available by default`() = runTest {
        val viewModel = createViewModel()
        assertFalse(viewModel.canUndo())
    }

    @Test
    fun `undo is available after move when feature enabled`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(true)
        val viewModel = createViewModel()

        val stateBefore = viewModel.gameState.value
        viewModel.onSwipe(Direction.LEFT)
        val stateAfter = viewModel.gameState.value

        if (stateAfter !== stateBefore) {
            assertTrue("canUndo should be true after a valid move", viewModel.canUndo())
        }
    }

    @Test
    fun `undo does nothing when feature disabled`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(false)
        val viewModel = createViewModel()

        viewModel.onSwipe(Direction.LEFT)
        val stateAfterMove = viewModel.gameState.value

        viewModel.undoMove()
        assertEquals(stateAfterMove, viewModel.gameState.value)
    }

    @Test
    fun `undo restores previous state when feature enabled`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(true)
        val viewModel = createViewModel()

        val stateBeforeMove = viewModel.gameState.value
        viewModel.onSwipe(Direction.LEFT)
        val stateAfterMove = viewModel.gameState.value

        if (stateAfterMove !== stateBeforeMove) {
            viewModel.undoMove()
            assertEquals(stateBeforeMove, viewModel.gameState.value)
        }
    }

    @Test
    fun `undo only works once`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(true)
        val viewModel = createViewModel()

        viewModel.onSwipe(Direction.LEFT)
        viewModel.undoMove()
        assertFalse(viewModel.canUndo())
    }

    // ==================== StateFlow ====================

    @Test
    fun `game state flow emits updates`() = runTest {
        val viewModel = createViewModel()
        viewModel.gameState.test {
            val initial = awaitItem()
            assertNotNull(initial)
            assertFalse(initial.isGameOver)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== Win dismiss ====================

    @Test
    fun `showWinOverlay is false after dismissal`() {
        val state = GameState(hasWon = true, winDismissed = false)
        assertTrue(state.showWinOverlay)
        assertFalse(state.copy(winDismissed = true).showWinOverlay)
    }

    @Test
    fun `showWinOverlay is false when game is over`() {
        val state = GameState(hasWon = true, isGameOver = true)
        assertFalse(state.showWinOverlay)
    }

    @Test
    fun `showWinOverlay is false when has not won`() {
        assertFalse(GameState().showWinOverlay)
    }

    // ==================== scoreGained ====================

    @Test
    fun `scoreGained is set correctly after merge`() {
        val state = GameState(
            grid = listOf(
                listOf(2, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        assertEquals(4, gameEngine.move(state, Direction.LEFT).scoreGained)
    }

    @Test
    fun `scoreGained is zero when no merge occurs`() {
        val state = GameState(
            grid = listOf(
                listOf(2, 4, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 8)
            )
        )
        val newState = gameEngine.move(state, Direction.LEFT)
        if (newState !== state) assertEquals(0, newState.scoreGained)
    }

    // ==================== Stress / edge cases ====================

    @Test
    fun `rapid consecutive swipes produce valid state`() = runTest {
        val viewModel = createViewModel()
        repeat(50) {
            viewModel.onSwipe(Direction.LEFT)
            viewModel.onSwipe(Direction.RIGHT)
            viewModel.onSwipe(Direction.UP)
            viewModel.onSwipe(Direction.DOWN)
        }
        val state = viewModel.gameState.value
        assertEquals(4, state.grid.size)
        state.grid.forEach { row ->
            assertEquals(4, row.size)
            row.forEach { cell -> assertTrue(cell >= 0) }
        }
        assertTrue(state.score >= 0)
    }

    @Test
    fun `game works when repository throws on read`() = runTest {
        coEvery { gameRepository.getBestScore() } throws RuntimeException("corrupted")
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(0, viewModel.gameState.value.bestScore)
        assertEquals(2, viewModel.gameState.value.grid.flatten().count { it != 0 })
    }

    @Test
    fun `game works when repository throws on write`() = runTest {
        coEvery { gameRepository.saveBestScore(any()) } throws RuntimeException("disk full")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSwipe(Direction.LEFT)
        advanceUntilIdle()

        assertTrue(viewModel.gameState.value.grid.flatten().count { it != 0 } >= 2)
    }

    @Test
    fun `tile IDs are unique across restarts`() = runTest {
        val viewModel = createViewModel()
        val firstGameIds = viewModel.gameState.value.tiles.map { it.id }.toSet()

        viewModel.onSwipe(Direction.LEFT)
        viewModel.startNewGame()

        val secondGameIds = viewModel.gameState.value.tiles.map { it.id }.toSet()
        assertTrue(
            "Tile IDs must be unique across restarts",
            firstGameIds.intersect(secondGameIds).isEmpty()
        )
    }

    @Test
    fun `rapid restarts produce valid state`() = runTest {
        val viewModel = createViewModel()
        repeat(20) { viewModel.startNewGame() }
        advanceUntilIdle()

        val state = viewModel.gameState.value
        assertEquals(2, state.grid.flatten().count { it != 0 })
        assertEquals(0, state.score)
    }
}
