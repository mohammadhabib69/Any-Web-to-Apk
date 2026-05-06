package com.example.poki

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║                           MAIN ACTIVITY                                    ║
 * ║                                                                            ║
 * ║  This is the main (and only) Activity of the app.                          ║
 * ║  It sets up the WebView, progress bar, pull-to-refresh, and ads.           ║
 * ║  All configuration is read from Config.kt — no edits needed here.          ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
class MainActivity : AppCompatActivity() {

    // ── Views ────────────────────────────────────────────────────────────────
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var bannerAdContainer: FrameLayout

    // ── Ad Manager ───────────────────────────────────────────────────────────
    private lateinit var admob: Admob

    // ══════════════════════════════════════════════════════════════════════════
    //  LIFECYCLE — onCreate
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ── Bind views ───────────────────────────────────────────────────────
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        bannerAdContainer = findViewById(R.id.bannerAdContainer)

        // ── Setup components ─────────────────────────────────────────────────
        setupWebView()
        setupSwipeRefresh()
        setupAds()

        // ── Load the content based on mode ───────────────────────────────────
        loadContent()
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  WEBVIEW SETUP
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            // JavaScript — required for most modern websites
            javaScriptEnabled = Config.ENABLE_JAVASCRIPT

            // DOM Storage — required for localStorage/sessionStorage
            domStorageEnabled = Config.ENABLE_DOM_STORAGE

            // File access — required for loading local HTML (Mode 2)
            allowFileAccess = Config.ALLOW_FILE_ACCESS

            // Allow content access from file URLs (for local mode)
            allowContentAccess = true

            // Enable zooming with pinch gestures
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false  // Hide the +/- zoom buttons

            // Fit content to screen width
            useWideViewPort = true
            loadWithOverviewMode = true

            // Enable mixed content (HTTP resources on HTTPS pages)
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // Media playback
            mediaPlaybackRequiresUserGesture = false

            // Custom user agent (if specified in Config)
            if (Config.CUSTOM_USER_AGENT.isNotBlank()) {
                userAgentString = Config.CUSTOM_USER_AGENT
            }
        }

        // ── WebViewClient: handle page loading events ────────────────────────
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Show progress bar when page starts loading
                progressBar.visibility = View.VISIBLE
                progressBar.progress = 0
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Hide progress bar and stop refresh spinner
                animateProgressTo(100)
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                swipeRefreshLayout.isRefreshing = false
            }

            // Keep all navigation inside the WebView (don't open external browser)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false  // false = let WebView handle it
            }
        }

        // ── WebChromeClient: handle progress updates ─────────────────────────
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                    animateProgressTo(newProgress)
                } else {
                    // Animate to 100 then hide
                    animateProgressTo(100)
                    progressBar.postDelayed({
                        progressBar.visibility = View.GONE
                    }, 300)  // Brief delay so the user sees it complete
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PROGRESS BAR ANIMATION
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Smoothly animates the progress bar to [target] value.
     * Uses a decelerate interpolator for a Chrome-like feel.
     */
    private fun animateProgressTo(target: Int) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, target)
        animator.duration = 250
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SWIPE-TO-REFRESH SETUP
    // ══════════════════════════════════════════════════════════════════════════

    private fun setupSwipeRefresh() {
        if (!Config.ENABLE_PULL_TO_REFRESH) {
            swipeRefreshLayout.isEnabled = false
            return
        }

        // Set the spinner colors from Config
        swipeRefreshLayout.setColorSchemeColors(
            Color.parseColor("#${Config.REFRESH_COLOR_1}"),
            Color.parseColor("#${Config.REFRESH_COLOR_2}"),
            Color.parseColor("#${Config.REFRESH_COLOR_3}")
        )

        // Reload the WebView on swipe-down
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
        }

        // Only allow pull-to-refresh when scrolled to the top (prevents conflict with page scroll)
        webView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            swipeRefreshLayout.isEnabled = Config.ENABLE_PULL_TO_REFRESH && scrollY == 0
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADS SETUP
    // ══════════════════════════════════════════════════════════════════════════

    private fun setupAds() {
        admob = Admob(this)
        admob.initialize()
        admob.loadBanner(bannerAdContainer)
        admob.startInterstitialCycle()
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CONTENT LOADING
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Loads content based on the mode selected in [Config].
     *   true  → Load from [Config.WEBSITE_URL]
     *   false → Load from [Config.LOCAL_HTML_PATH]
     */
    private fun loadContent() {
        val url = if (Config.USE_REMOTE_URL) {
            Config.WEBSITE_URL
        } else {
            Config.LOCAL_HTML_PATH
        }
        webView.loadUrl(url)
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BACK BUTTON NAVIGATION
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Pressing the hardware/system back button navigates back in the WebView history.
     * If there is no history left, the default behavior (exit app) is used.
     */
    @Suppress("DEPRECATION")
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LIFECYCLE CALLBACKS
    // ══════════════════════════════════════════════════════════════════════════

    override fun onResume() {
        super.onResume()
        webView.onResume()
        admob.onResume()
    }

    override fun onPause() {
        webView.onPause()
        admob.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        admob.onDestroy()
        super.onDestroy()
    }
}