package com.secondbrain.app

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner for Android instrumentation tests.
 * This runner enables Hilt dependency injection in tests and provides
 * additional test configuration.
 */
class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    override fun onCreate() {
        super.onCreate()
        
        // Configure test environment
        configureTestEnvironment()
    }

    private fun configureTestEnvironment() {
        // Set system properties for testing
        System.setProperty("dexmaker.dexcache", targetContext.cacheDir.path)
        
        // Configure network timeouts for testing
        System.setProperty("http.keepAlive", "false")
        System.setProperty("http.maxConnections", "1")
        
        // Disable animations for more reliable testing
        try {
            val settings = android.provider.Settings.Global::class.java
            val animationScaleField = settings.getDeclaredField("ANIMATOR_DURATION_SCALE")
            animationScaleField.isAccessible = true
            
            val transitionScaleField = settings.getDeclaredField("TRANSITION_ANIMATION_SCALE")
            transitionScaleField.isAccessible = true
            
            val windowScaleField = settings.getDeclaredField("WINDOW_ANIMATION_SCALE")
            windowScaleField.isAccessible = true
            
            // Note: In practice, these would be set via ADB commands or test orchestration
            // This is for demonstration of test environment setup
        } catch (e: Exception) {
            // Settings modification might not be available in all test environments
            android.util.Log.d("TestRunner", "Animation scale modification not available: ${e.message}")
        }
    }
}