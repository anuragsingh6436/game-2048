package com.bajrangi.game_2048

import android.app.Application
import com.bajrangi.game_2048.config.RemoteConfigManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Game2048Application : Application() {

    @Inject lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate() {
        super.onCreate()
        // Safe: failures inside initialize() fall back to defaults.
        runCatching { remoteConfigManager.initialize() }
        // AdMob init is async and safe to call once. Failures are non-fatal.
        runCatching { MobileAds.initialize(this) {} }
    }
}
