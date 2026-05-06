plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.poki"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.poki"   // ← Replace with your own package name for production
        minSdk = 24                          // ← Supports Android 7.0+ (covers ~97% of devices)
        targetSdk = 35
        versionCode = 1                      // ← Increment for each Play Store release
        versionName = "1.0"                  // ← Your app version string

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Uses the default debug signing config automatically
        }
        release {
            isMinifyEnabled = true           // ← Shrinks & obfuscates code for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Ensure native libraries are extracted (fixes installation issues on some devices)
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // ── Core Android ─────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // ── SwipeRefreshLayout — Chrome-style pull-to-refresh ────────────────────
    implementation(libs.androidx.swiperefreshlayout)

    // ── Google AdMob — Banner & Interstitial ads ─────────────────────────────
    implementation(libs.play.services.ads)

    // ── Testing ──────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}