package com.bajrangi.game_2048.config

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around Firebase Remote Config. Initialized once at app
 * startup; exposes feature flags as a StateFlow so Compose can react.
 *
 * Failure mode is intentionally silent — defaults always apply, so the
 * app cannot be bricked by a Remote Config outage.
 */
@Singleton
class RemoteConfigManager @Inject constructor() {

    data class Flags(
        val enableBannerAds: Boolean = false,
        val forceUpdate: Boolean = false,
        val minVersionCode: Long = 0L,
    )

    private val _flags = MutableStateFlow(Flags())
    val flags: StateFlow<Flags> = _flags.asStateFlow()

    fun initialize() {
        val rc = runCatching { Firebase.remoteConfig }.getOrNull() ?: return

        rc.setConfigSettingsAsync(
            remoteConfigSettings {
                // Tighten in debug builds for fast iteration; production
                // honors the platform default (12h).
                minimumFetchIntervalInSeconds = if (BuildConfigFlag.isDebug) 0 else 3600
            }
        )

        rc.setDefaultsAsync(
            mapOf(
                KEY_ENABLE_BANNER_ADS to false,
                KEY_FORCE_UPDATE to false,
                KEY_MIN_VERSION_CODE to 0L,
            )
        )

        // Apply defaults immediately so first frame has correct values.
        publish(
            enableBannerAds = rc.getBoolean(KEY_ENABLE_BANNER_ADS),
            forceUpdate = rc.getBoolean(KEY_FORCE_UPDATE),
            minVersionCode = rc.getLong(KEY_MIN_VERSION_CODE),
            source = "defaults",
        )

        rc.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val activated = task.result
                    Log.d(TAG, "fetchAndActivate success (activated=$activated)")
                    publish(
                        enableBannerAds = rc.getBoolean(KEY_ENABLE_BANNER_ADS),
                        forceUpdate = rc.getBoolean(KEY_FORCE_UPDATE),
                        minVersionCode = rc.getLong(KEY_MIN_VERSION_CODE),
                        source = "remote",
                    )
                } else {
                    Log.w(TAG, "Remote Config fetch failed; using defaults", task.exception)
                }
            }
    }

    private fun publish(
        enableBannerAds: Boolean,
        forceUpdate: Boolean,
        minVersionCode: Long,
        source: String,
    ) {
        Log.d(
            TAG,
            "flags updated [$source] -> $KEY_ENABLE_BANNER_ADS=$enableBannerAds, " +
                "$KEY_FORCE_UPDATE=$forceUpdate, " +
                "$KEY_MIN_VERSION_CODE=$minVersionCode"
        )
        _flags.value = Flags(
            enableBannerAds = enableBannerAds,
            forceUpdate = forceUpdate,
            minVersionCode = minVersionCode,
        )
    }

    companion object {
        private const val TAG = "RemoteConfigManager"
        const val KEY_ENABLE_BANNER_ADS = "numra_enable_ads"
        const val KEY_FORCE_UPDATE = "numra_force_update"
        const val KEY_MIN_VERSION_CODE = "numra_min_version_code"
    }
}

/** Tiny indirection so we can swap with BuildConfig.DEBUG without an import cycle. */
internal object BuildConfigFlag {
    val isDebug: Boolean = com.bajrangi.game_2048.BuildConfig.DEBUG
}
