package com.example.game_2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.game_2048.presentation.GameViewModel
import com.example.game_2048.presentation.screen.GameScreen
import com.example.game_2048.ui.theme.Game2048Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Game2048Theme {
                val viewModel: GameViewModel = hiltViewModel()
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
