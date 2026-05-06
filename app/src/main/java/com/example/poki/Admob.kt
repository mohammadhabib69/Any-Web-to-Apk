package com.example.poki

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║                          AdMob MANAGER                                     ║
 * ║                                                                            ║
 * ║  Handles both Banner and Interstitial ads.                                 ║
 * ║  If Ad Unit IDs in Config.kt are empty, ads are fully disabled.            ║
 * ║  This file does NOT need to be edited — configure everything in Config.kt. ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
class Admob(private val activity: Activity) {

    companion object {
        private const val TAG = "Admob"
    }

    // ── State ────────────────────────────────────────────────────────────────
    private var bannerAdView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    private val handler = Handler(Looper.getMainLooper())
    private var interstitialRunnable: Runnable? = null

    // ── Computed flags ───────────────────────────────────────────────────────
    private val isBannerEnabled: Boolean
        get() = Config.ADMOB_BANNER_ID.isNotBlank()

    private val isInterstitialEnabled: Boolean
        get() = Config.ADMOB_INTERSTITIAL_ID.isNotBlank()

    // ══════════════════════════════════════════════════════════════════════════
    //  INITIALIZATION
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Call this once from MainActivity.onCreate().
     * Initializes the Mobile Ads SDK only if at least one ad type is enabled.
     */
    fun initialize() {
        if (!isBannerEnabled && !isInterstitialEnabled) {
            Log.d(TAG, "All Ad Unit IDs are empty — ads are fully disabled.")
            return
        }

        try {
            MobileAds.initialize(activity) { initStatus ->
                Log.d(TAG, "Mobile Ads SDK initialized: $initStatus")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Mobile Ads SDK", e)
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BANNER AD
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Loads and displays a banner ad in the provided [container].
     * If [Config.ADMOB_BANNER_ID] is blank, the container is hidden and no ad loads.
     *
     * @param container The FrameLayout in your layout where the banner will be placed.
     */
    fun loadBanner(container: FrameLayout) {
        if (!isBannerEnabled) {
            container.visibility = View.GONE
            Log.d(TAG, "Banner ad disabled — ADMOB_BANNER_ID is empty.")
            return
        }

        container.visibility = View.VISIBLE

        bannerAdView = AdView(activity).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = Config.ADMOB_BANNER_ID
        }

        container.removeAllViews()
        container.addView(bannerAdView)

        val adRequest = AdRequest.Builder().build()
        bannerAdView?.loadAd(adRequest)
        Log.d(TAG, "Banner ad loading with ID: ${Config.ADMOB_BANNER_ID}")
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INTERSTITIAL AD
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Starts the interstitial ad cycle.
     * Loads the first interstitial and schedules periodic display based on
     * [Config.INTERSTITIAL_INTERVAL_SECONDS].
     *
     * If [Config.ADMOB_INTERSTITIAL_ID] is blank, this does nothing.
     */
    fun startInterstitialCycle() {
        if (!isInterstitialEnabled) {
            Log.d(TAG, "Interstitial ad disabled — ADMOB_INTERSTITIAL_ID is empty.")
            return
        }

        loadInterstitial()

        val intervalMillis = Config.INTERSTITIAL_INTERVAL_SECONDS * 1000L
        interstitialRunnable = object : Runnable {
            override fun run() {
                showInterstitial()
                handler.postDelayed(this, intervalMillis)
            }
        }
        handler.postDelayed(interstitialRunnable!!, intervalMillis)
        Log.d(TAG, "Interstitial cycle started — interval: ${Config.INTERSTITIAL_INTERVAL_SECONDS}s")
    }

    /**
     * Loads a new interstitial ad from the network.
     */
    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            Config.ADMOB_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial ad loaded successfully.")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial dismissed — preloading next.")
                            interstitialAd = null
                            loadInterstitial()  // Preload the next one
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial displayed.")
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.e(TAG, "Interstitial failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows the interstitial ad if one is loaded; otherwise logs and skips.
     */
    private fun showInterstitial() {
        val ad = interstitialAd
        if (ad != null) {
            ad.show(activity)
        } else {
            Log.d(TAG, "Interstitial not ready — skipping this cycle.")
            loadInterstitial()  // Try loading again for next cycle
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LIFECYCLE MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    /** Call from Activity.onResume() */
    fun onResume() {
        bannerAdView?.resume()
    }

    /** Call from Activity.onPause() */
    fun onPause() {
        bannerAdView?.pause()
    }

    /** Call from Activity.onDestroy() to clean up all ad resources */
    fun onDestroy() {
        // Stop the interstitial timer
        interstitialRunnable?.let { handler.removeCallbacks(it) }
        interstitialRunnable = null

        // Destroy the banner
        bannerAdView?.destroy()
        bannerAdView = null

        // Clear interstitial reference
        interstitialAd = null

        Log.d(TAG, "Admob resources cleaned up.")
    }
}
