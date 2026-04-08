package com.bajrangi.game_2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.bajrangi.game_2048.presentation.GameViewModel
import com.bajrangi.game_2048.presentation.screen.GameScreen
import com.bajrangi.game_2048.ui.theme.Game2048Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsState()

            Game2048Theme(themeMode = themeMode) {
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
