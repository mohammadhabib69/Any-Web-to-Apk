package com.example.poki

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║                        APP CONFIGURATION FILE                              ║
 * ║                                                                            ║
 * ║  This file contains ALL configurable options for the app.                  ║
 * ║  Edit the values below to customize your WebView app.                      ║
 * ║  No other files need to be modified for basic configuration.               ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
object Config {

    // ══════════════════════════════════════════════════════════════════════════
    //  SECTION 1: LOADING MODE
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Set to true  → Mode 1: Load a LIVE WEBSITE from a URL (see [WEBSITE_URL] below).
     * Set to false → Mode 2: Load LOCAL HTML files from the assets/web/ folder.
     *
     * Example:
     *   true  → The app will load "https://example.com" (or whatever URL you set).
     *   false → The app will load "file:///android_asset/web/index.html".
     */
    const val USE_REMOTE_URL: Boolean = true  // ← Change to false for local HTML mode

    // ══════════════════════════════════════════════════════════════════════════
    //  SECTION 2: REMOTE URL (Mode 1)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * The full URL of the website to load when [USE_REMOTE_URL] is true.
     * Replace this with your own website URL.
     *
     * Examples:
     *   "https://www.google.com"
     *   "https://yoursite.com"
     *   "https://your-web-app.vercel.app"
     */
    const val WEBSITE_URL: String = "https://www.poki.com"  // ← Replace with your website URL

    // ══════════════════════════════════════════════════════════════════════════
    //  SECTION 3: LOCAL HTML PATH (Mode 2)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * The path to load when [USE_REMOTE_URL] is false.
     * This points to a file inside the app's assets/ folder.
     *
     * Default structure expected inside assets/:
     *   assets/
     *   └── web/
     *       ├── index.html       ← Main entry point
     *       ├── css/
     *       │   └── style.css    ← Your stylesheets
     *       ├── js/
     *       │   └── app.js       ← Your JavaScript
     *       └── img/             ← Your images (optional)
     *
     * Only change this if your index file is named differently or in a different subfolder.
     */
    const val LOCAL_HTML_PATH: String = "file:///android_asset/web/index.html"  // ← Typically no change needed

    // ══════════════════════════════════════════════════════════════════════════
    //  SECTION 4: AdMob CONFIGURATION
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Your AdMob BANNER Ad Unit ID.
     * The banner ad is displayed permanently at the bottom of the screen.
     *
     * → Leave EMPTY ("") to completely disable banner ads (no crashes).
     * → For TESTING, use Google's test ID: "ca-app-pub-3940256099942544/6300978111"
     * → For PRODUCTION, replace with your real Ad Unit ID from AdMob console.
     */
    const val ADMOB_BANNER_ID: String = ""  // ← Paste your Banner Ad Unit ID here

    /**
     * Your AdMob INTERSTITIAL (full-screen) Ad Unit ID.
     * The interstitial ad is shown periodically based on [INTERSTITIAL_INTERVAL_SECONDS].
     *
     * → Leave EMPTY ("") to completely disable interstitial ads (no crashes).
     * → For TESTING, use Google's test ID: "ca-app-pub-3940256099942544/1033173712"
     * → For PRODUCTION, replace with your real Ad Unit ID from AdMob console.
     */
    const val ADMOB_INTERSTITIAL_ID: String = ""  // ← Paste your Interstitial Ad Unit ID here

    /**
     * Time interval (in SECONDS) between interstitial ad displays.
     * The first interstitial will be shown after this many seconds from app launch,
     * and then again every [INTERSTITIAL_INTERVAL_SECONDS] seconds after that.
     *
     * Recommended: 60–180 seconds. Too frequent = bad user experience.
     * This value is ignored if [ADMOB_INTERSTITIAL_ID] is empty.
     */
    const val INTERSTITIAL_INTERVAL_SECONDS: Int = 90  // ← Change to your desired interval in seconds

    // ══════════════════════════════════════════════════════════════════════════
    //  SECTION 5: WEBVIEW BEHAVIOR
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Allow JavaScript execution in the WebView.
     * Most modern websites require this to be true.
     * Only set to false if you are loading a purely static HTML page.
     */
    const val ENABLE_JAVASCRIPT: Boolean = true  // ← true = enable JS (recommended)

    /**
     * Allow DOM storage (localStorage / sessionStorage) in the WebView.
     * Required by most modern web apps (React, Vue, Angular, etc.).
     */
    const val ENABLE_DOM_STORAGE: Boolean = true  // ← true = enable DOM storage (recommended)

    /**
     * Allow the WebView to access local files (file:// protocol).
     * Must be true for Mode 2 (local HTML) to work.
     * Safe to leave true even in Mode 1.
     */
    const val ALLOW_FILE_ACCESS: Boolean = true  // ← true = allow file access (required for local mode)

    /**
     * Set the user agent string for the WebView.
     * Leave EMPTY ("") to use the default Android WebView user agent.
     * Some websites serve different content based on the user agent.
     *
     * Example: "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120.0"
     */
    const val CUSTOM_USER_AGENT: String = ""  // ← Leave empty for default, or set a custom UA string

    // ══════════════════════════════════════════════════════════════════════════
    //  SECTION 6: PULL-TO-REFRESH
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Enable or disable the Chrome-style pull-to-refresh gesture.
     * When enabled, users can swipe down from the top to refresh the page.
     */
    const val ENABLE_PULL_TO_REFRESH: Boolean = true  // ← true = enable pull-to-refresh

    /**
     * The color scheme for the pull-to-refresh spinner.
     * Provide hex color values (without #). Up to three colors for animated rotation.
     */
    const val REFRESH_COLOR_1: String = "4285F4"  // ← Google Blue
    const val REFRESH_COLOR_2: String = "EA4335"  // ← Google Red
    const val REFRESH_COLOR_3: String = "34A853"  // ← Google Green
}
