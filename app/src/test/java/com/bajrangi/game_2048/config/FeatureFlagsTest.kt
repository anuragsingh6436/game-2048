package com.bajrangi.game_2048.config

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FeatureFlagsTest {

    private lateinit var featureFlags: FeatureFlags

    @Before
    fun setup() {
        featureFlags = FeatureFlags()
    }

    @Test
    fun `ads disabled by default`() {
        assertFalse(featureFlags.enableAds)
    }

    @Test
    fun `advanced features disabled by default`() {
        assertFalse(featureFlags.enableAdvancedFeatures)
    }

    @Test
    fun `enabling ads updates flag`() {
        featureFlags.setAdsEnabled(true)
        assertTrue(featureFlags.enableAds)
    }

    @Test
    fun `disabling ads updates flag`() {
        featureFlags.setAdsEnabled(true)
        featureFlags.setAdsEnabled(false)
        assertFalse(featureFlags.enableAds)
    }

    @Test
    fun `enabling advanced features updates flag`() {
        featureFlags.setAdvancedFeaturesEnabled(true)
        assertTrue(featureFlags.enableAdvancedFeatures)
    }

    @Test
    fun `undo disabled when advanced features off`() {
        assertFalse(featureFlags.isUndoEnabled())
    }

    @Test
    fun `undo enabled when advanced features on`() {
        featureFlags.setAdvancedFeaturesEnabled(true)
        assertTrue(featureFlags.isUndoEnabled())
    }

    @Test
    fun `banner ad not shown when ads disabled`() {
        assertFalse(featureFlags.shouldShowBannerAd())
    }

    @Test
    fun `banner ad shown when ads enabled`() {
        featureFlags.setAdsEnabled(true)
        assertTrue(featureFlags.shouldShowBannerAd())
    }

    @Test
    fun `interstitial ad not shown when ads disabled`() {
        assertFalse(featureFlags.shouldShowInterstitialAd())
    }

    @Test
    fun `interstitial ad shown when ads enabled`() {
        featureFlags.setAdsEnabled(true)
        assertTrue(featureFlags.shouldShowInterstitialAd())
    }
}
