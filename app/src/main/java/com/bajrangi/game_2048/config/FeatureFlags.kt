package com.bajrangi.game_2048.config

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureFlags @Inject constructor() {

    var enableAds: Boolean = false
        private set

    var enableAdvancedFeatures: Boolean = false
        private set

    fun setAdsEnabled(enabled: Boolean) {
        enableAds = enabled
    }

    fun setAdvancedFeaturesEnabled(enabled: Boolean) {
        enableAdvancedFeatures = enabled
    }

    fun isUndoEnabled(): Boolean = enableAdvancedFeatures

    fun shouldShowBannerAd(): Boolean = enableAds

    fun shouldShowInterstitialAd(): Boolean = enableAds
}
