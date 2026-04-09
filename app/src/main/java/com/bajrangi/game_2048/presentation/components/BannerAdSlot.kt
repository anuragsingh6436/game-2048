package com.bajrangi.game_2048.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bajrangi.game_2048.config.AdConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Banner ad slot. Renders nothing when [enabled] is false — keeps
 * layouts safe and pays no inflation cost. When enabled, hosts an
 * AdMob anchored adaptive banner via [AndroidView] and disposes it
 * on leave. Adaptive banners fill the full screen width and Google
 * picks an optimal height per device — generally higher revenue and
 * no awkward side gaps compared to the legacy 320x50 BANNER.
 */
@Composable
fun BannerAdSlot(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!enabled) return

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp

    // Compute the adaptive size once. Re-deriving on every composition
    // is fine — it's a pure function of widthDp — but we cache so the
    // Box height is stable.
    val adSize = remember(widthDp) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }

    val adView = remember(adSize) {
        AdView(context).apply {
            setAdSize(adSize)
            adUnitId = AdConfig.getBannerAdUnitId()
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w("BannerAd", "load failed: ${error.message}")
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    // Reserve the exact height the ad will occupy so layout never shifts.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(adSize.height.dp),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
