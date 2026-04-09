package com.bajrangi.game_2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bajrangi.game_2048.BuildConfig
import com.bajrangi.game_2048.config.RemoteConfigManager
import com.bajrangi.game_2048.presentation.GameViewModel
import com.bajrangi.game_2048.presentation.components.BannerAdSlot
import com.bajrangi.game_2048.presentation.components.ForceUpdateDialog
import com.bajrangi.game_2048.presentation.components.SettingsOverlay
import com.bajrangi.game_2048.presentation.screen.GameScreen
import com.bajrangi.game_2048.presentation.screen.HowToPlayScreen
import com.bajrangi.game_2048.presentation.screen.SplashScreen
import com.bajrangi.game_2048.ui.theme.Game2048Theme
import com.bajrangi.game_2048.ui.theme.LocalGameColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private enum class Screen { Splash, Game, HowToPlay }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val flags by remoteConfigManager.flags.collectAsState()

            Game2048Theme(themeMode = themeMode) {
                var current by rememberSaveable { mutableStateOf(Screen.Splash) }
                var previous by rememberSaveable { mutableStateOf(Screen.Splash) }
                var isSoundEnabled by rememberSaveable { mutableStateOf(true) }

                fun navigate(target: Screen) {
                    previous = current
                    current = target
                }

                // Global back handling:
                //  - HowToPlay pops back to wherever it was opened from.
                //  - Game pops back to Splash instead of killing the task.
                //  - Splash falls through to the system (exit app).
                BackHandler(enabled = current != Screen.Splash) {
                    current = when (current) {
                        Screen.HowToPlay -> previous.also { previous = Screen.Splash }
                        Screen.Game -> Screen.Splash
                        Screen.Splash -> Screen.Splash
                    }
                }

                val gameColors = LocalGameColors.current

                Box(modifier = Modifier.fillMaxSize()) {
                  Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = current,
                        transitionSpec = {
                            (fadeIn(tween(450)) togetherWith fadeOut(tween(350)))
                        },
                        label = "screenTransition"
                    ) { screen ->
                        when (screen) {
                            Screen.Splash -> SplashScreen(
                                onStart = { size ->
                                    viewModel.startNewGame(size)
                                    navigate(Screen.Game)
                                },
                                isSoundEnabled = isSoundEnabled
                            )
                            Screen.Game -> GameScreen(
                                viewModel = viewModel,
                                isSoundEnabled = isSoundEnabled
                            )
                            Screen.HowToPlay -> HowToPlayScreen(
                                onBack = {
                                    current = previous
                                    previous = Screen.Splash
                                }
                            )
                        }
                    }

                    }
                    BannerAdSlot(enabled = flags.enableBannerAds)
                  }

                    if (flags.forceUpdate &&
                        BuildConfig.VERSION_CODE < flags.minVersionCode
                    ) {
                        ForceUpdateDialog()
                    }

                    // Global settings overlay — present on Splash and Game,
                    // hidden while the dedicated How-to-play screen is open
                    // (that screen has its own back affordance).
                    if (current != Screen.HowToPlay) {
                        SettingsOverlay(
                            isSoundEnabled = isSoundEnabled,
                            onToggleTheme = {
                                viewModel.toggleTheme(gameColors.isDark)
                            },
                            onToggleSound = {
                                isSoundEnabled = !isSoundEnabled
                            },
                            onHowToPlay = { navigate(Screen.HowToPlay) }
                        )
                    }
                }
            }
        }
    }
}
