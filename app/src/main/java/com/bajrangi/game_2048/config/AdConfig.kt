package com.bajrangi.game_2048.config

import android.util.Log
import com.bajrangi.game_2048.BuildConfig

/**
 * Single source of truth for AdMob unit IDs.
 *
 * Debug builds always serve Google's public test ad — never real ads —
 * to comply with AdMob policy and prevent account bans from invalid
 * impressions during development. Release builds serve the real unit.
 *
 * To go to production: replace [REAL_BANNER_ID] with the unit ID from
 * the AdMob console, and replace the App ID in AndroidManifest.xml.
 */
object AdConfig {

    // Google's official test banner unit. Safe to use anywhere, anytime.
    private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"

    private const val REAL_BANNER_ID = "ca-app-pub-6081894851925140/7777272060"

    fun getBannerAdUnitId(): String {
        val id = if (BuildConfig.DEBUG) TEST_BANNER_ID else REAL_BANNER_ID
        Log.d(TAG, "banner ad unit -> $id (debug=${BuildConfig.DEBUG})")
        return id
    }

    private const val TAG = "AdConfig"
}
