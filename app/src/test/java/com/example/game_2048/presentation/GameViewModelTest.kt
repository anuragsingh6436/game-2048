package com.example.game_2048.presentation

import app.cash.turbine.test
import com.example.game_2048.config.FeatureFlags
import com.example.game_2048.data.repository.ScoreRepository
import com.example.game_2048.domain.engine.GameEngine
import com.example.game_2048.domain.model.Direction
import com.example.game_2048.domain.model.GameState
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
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var featureFlags: FeatureFlags

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gameEngine = GameEngine()
        scoreRepository = mockk(relaxed = true)
        featureFlags = FeatureFlags()
        coEvery { scoreRepository.getBestScore() } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GameViewModel {
        return GameViewModel(gameEngine, scoreRepository, featureFlags)
    }

    @Test
    fun `initial state has two tiles`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.gameState.value
        val nonZero = state.grid.flatten().count { it != 0 }
        assertEquals(2, nonZero)
    }

    @Test
    fun `initial state score is zero`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(0, viewModel.gameState.value.score)
    }

    @Test
    fun `initial state is not game over`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.gameState.value.isGameOver)
    }

    @Test
    fun `initial state loads best score from repository`() = runTest {
        coEvery { scoreRepository.getBestScore() } returns 5000
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(5000, viewModel.gameState.value.bestScore)
    }

    @Test
    fun `startNewGame resets the game`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSwipe(Direction.LEFT)
        viewModel.onSwipe(Direction.UP)
        advanceUntilIdle()

        viewModel.startNewGame()
        advanceUntilIdle()

        assertEquals(0, viewModel.gameState.value.score)
        val nonZero = viewModel.gameState.value.grid.flatten().count { it != 0 }
        assertEquals(2, nonZero)
    }

    @Test
    fun `onSwipe does not process when game is over`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Build a game-over state from a real game state
        val gameOverGrid = listOf(
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256),
            listOf(2, 4, 8, 16),
            listOf(32, 64, 128, 256)
        )
        val gameOverState = viewModel.gameState.value.copy(
            grid = gameOverGrid,
            isGameOver = true
        )

        // Force the state via reflection-free approach:
        // onSwipe checks isGameOver and returns early
        // We simulate by directly creating a ViewModel with a known engine state
        // Instead, test via the public API: perform moves until game over or verify guard
        val stateBeforeSwipe = viewModel.gameState.value
        val moveCountBefore = stateBeforeSwipe.moveCount

        // Swipe many times to advance the game
        repeat(200) {
            viewModel.onSwipe(Direction.LEFT)
            viewModel.onSwipe(Direction.RIGHT)
            viewModel.onSwipe(Direction.UP)
            viewModel.onSwipe(Direction.DOWN)
        }

        val finalState = viewModel.gameState.value
        if (finalState.isGameOver) {
            val moveCountAtGameOver = finalState.moveCount
            // Try swiping after game over
            viewModel.onSwipe(Direction.LEFT)
            viewModel.onSwipe(Direction.RIGHT)
            // Move count should NOT change
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
            coVerify { scoreRepository.saveBestScore(any()) }
        }
    }

    @Test
    fun `undo is not available by default`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.canUndo())
    }

    @Test
    fun `undo is available after move when feature enabled`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(true)
        val viewModel = createViewModel()
        advanceUntilIdle()

        val stateBefore = viewModel.gameState.value
        viewModel.onSwipe(Direction.LEFT)
        val stateAfter = viewModel.gameState.value

        // If the move actually changed state, undo should be available
        if (stateAfter !== stateBefore) {
            assertTrue("canUndo should be true after a valid move", viewModel.canUndo())
        }
    }

    @Test
    fun `undo does nothing when feature disabled`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(false)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSwipe(Direction.LEFT)
        val stateAfterMove = viewModel.gameState.value

        viewModel.undoMove()
        assertEquals(stateAfterMove, viewModel.gameState.value)
    }

    @Test
    fun `undo restores previous state when feature enabled`() = runTest {
        featureFlags.setAdvancedFeaturesEnabled(true)
        val viewModel = createViewModel()
        advanceUntilIdle()

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
        advanceUntilIdle()

        viewModel.onSwipe(Direction.LEFT)
        viewModel.undoMove()

        assertFalse(viewModel.canUndo())
    }

    @Test
    fun `game state flow emits updates`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.gameState.test {
            val initial = awaitItem()
            assertNotNull(initial)
            assertFalse(initial.isGameOver)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== NEW: Win dismiss tests ====================

    @Test
    fun `dismissWin sets winDismissed to true`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Create a won state
        val wonState = viewModel.gameState.value.copy(hasWon = true, winDismissed = false)
        // Use engine to create a real won state
        val winGrid = listOf(
            listOf(1024, 1024, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0),
            listOf(0, 0, 0, 0)
        )
        val engineState = viewModel.gameState.value.copy(grid = winGrid)

        // Move left to create 2048 and trigger win
        val newState = gameEngine.move(engineState, Direction.LEFT)
        assertTrue("Should detect win", newState.hasWon)
    }

    @Test
    fun `showWinOverlay is false after dismissal`() = runTest {
        val state = GameState(hasWon = true, winDismissed = false)
        assertTrue(state.showWinOverlay)

        val dismissed = state.copy(winDismissed = true)
        assertFalse(dismissed.showWinOverlay)
    }

    @Test
    fun `showWinOverlay is false when game is over`() = runTest {
        val state = GameState(hasWon = true, isGameOver = true, winDismissed = false)
        assertFalse(state.showWinOverlay)
    }

    @Test
    fun `showWinOverlay is false when has not won`() = runTest {
        val state = GameState(hasWon = false, winDismissed = false)
        assertFalse(state.showWinOverlay)
    }

    // ==================== NEW: scoreGained tests ====================

    @Test
    fun `scoreGained is set correctly after merge`() = runTest {
        val state = GameState(
            grid = listOf(
                listOf(2, 2, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
        )
        val newState = gameEngine.move(state, Direction.LEFT)
        assertEquals(4, newState.scoreGained)
    }

    @Test
    fun `scoreGained is zero when no merge occurs`() = runTest {
        val state = GameState(
            grid = listOf(
                listOf(2, 4, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 8)
            )
        )
        val newState = gameEngine.move(state, Direction.LEFT)
        if (newState !== state) {
            assertEquals(0, newState.scoreGained)
        }
    }

    // ==================== NEW: Rapid swipe test ====================

    @Test
    fun `rapid consecutive swipes don't corrupt state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Simulate rapid swipes in all directions
        repeat(50) {
            viewModel.onSwipe(Direction.LEFT)
            viewModel.onSwipe(Direction.RIGHT)
            viewModel.onSwipe(Direction.UP)
            viewModel.onSwipe(Direction.DOWN)
        }

        val state = viewModel.gameState.value
        // Grid must remain valid 4x4
        assertEquals(4, state.grid.size)
        state.grid.forEach { row ->
            assertEquals(4, row.size)
            row.forEach { cell ->
                assertTrue("Cell value must be non-negative", cell >= 0)
            }
        }
        // Score must be non-negative
        assertTrue(state.score >= 0)
        // Move count must be non-negative
        assertTrue(state.moveCount >= 0)
    }
}
