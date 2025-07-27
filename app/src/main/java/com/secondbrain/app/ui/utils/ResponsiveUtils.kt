package com.secondbrain.app.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive design utilities for Second Brain Android app
 * Implements adaptive layouts for different screen sizes with Japanese stationery aesthetics
 */

/**
 * Screen size classification for responsive design
 */
enum class ScreenSize {
    Compact,    // Phones in portrait
    Medium,     // Phones in landscape, small tablets
    Expanded    // Large tablets, desktops
}

/**
 * Get current screen size classification
 */
@Composable
fun getScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    return when {
        screenWidth < 600 -> ScreenSize.Compact
        screenWidth < 840 -> ScreenSize.Medium
        else -> ScreenSize.Expanded
    }
}

/**
 * Get adaptive column count for grid layouts
 */
@Composable
fun getAdaptiveColumns(): Int {
    return when (getScreenSize()) {
        ScreenSize.Compact -> 1
        ScreenSize.Medium -> 2
        ScreenSize.Expanded -> 3
    }
}

/**
 * Get adaptive column count for specific content types
 */
@Composable
fun getAdaptiveColumns(contentType: ContentType): Int {
    val screenSize = getScreenSize()
    
    return when (contentType) {
        ContentType.BookmarkCard -> when (screenSize) {
            ScreenSize.Compact -> 1
            ScreenSize.Medium -> 2
            ScreenSize.Expanded -> 3
        }
        ContentType.CollectionCard -> when (screenSize) {
            ScreenSize.Compact -> 2
            ScreenSize.Medium -> 3
            ScreenSize.Expanded -> 4
        }
        ContentType.FeatureCard -> when (screenSize) {
            ScreenSize.Compact -> 1
            ScreenSize.Medium -> 2
            ScreenSize.Expanded -> 2
        }
        ContentType.QuickAction -> when (screenSize) {
            ScreenSize.Compact -> 2
            ScreenSize.Medium -> 4
            ScreenSize.Expanded -> 4
        }
    }
}

/**
 * Content types for adaptive layouts
 */
enum class ContentType {
    BookmarkCard,
    CollectionCard,
    FeatureCard,
    QuickAction
}

/**
 * Get adaptive spacing based on screen size
 */
@Composable
fun getAdaptiveSpacing(): AdaptiveSpacing {
    val screenSize = getScreenSize()
    
    return when (screenSize) {
        ScreenSize.Compact -> AdaptiveSpacing.Compact
        ScreenSize.Medium -> AdaptiveSpacing.Medium
        ScreenSize.Expanded -> AdaptiveSpacing.Expanded
    }
}

/**
 * Adaptive spacing values for different screen sizes
 */
data class AdaptiveSpacing(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
    val contentPadding: Dp,
    val screenMargin: Dp
) {
    companion object {
        val Compact = AdaptiveSpacing(
            extraSmall = 4.dp,
            small = 8.dp,
            medium = 16.dp,
            large = 24.dp,
            extraLarge = 32.dp,
            contentPadding = 16.dp,
            screenMargin = 16.dp
        )
        
        val Medium = AdaptiveSpacing(
            extraSmall = 6.dp,
            small = 12.dp,
            medium = 20.dp,
            large = 28.dp,
            extraLarge = 40.dp,
            contentPadding = 20.dp,
            screenMargin = 24.dp
        )
        
        val Expanded = AdaptiveSpacing(
            extraSmall = 8.dp,
            small = 16.dp,
            medium = 24.dp,
            large = 32.dp,
            extraLarge = 48.dp,
            contentPadding = 24.dp,
            screenMargin = 32.dp
        )
    }
}

/**
 * Get adaptive typography scaling
 */
@Composable
fun getAdaptiveTypography(): AdaptiveTypography {
    val screenSize = getScreenSize()
    
    return when (screenSize) {
        ScreenSize.Compact -> AdaptiveTypography.Compact
        ScreenSize.Medium -> AdaptiveTypography.Medium
        ScreenSize.Expanded -> AdaptiveTypography.Expanded
    }
}

/**
 * Adaptive typography scaling for different screen sizes
 */
data class AdaptiveTypography(
    val displayScale: Float,
    val headlineScale: Float,
    val titleScale: Float,
    val bodyScale: Float,
    val labelScale: Float
) {
    companion object {
        val Compact = AdaptiveTypography(
            displayScale = 1.0f,
            headlineScale = 1.0f,
            titleScale = 1.0f,
            bodyScale = 1.0f,
            labelScale = 1.0f
        )
        
        val Medium = AdaptiveTypography(
            displayScale = 1.1f,
            headlineScale = 1.05f,
            titleScale = 1.05f,
            bodyScale = 1.0f,
            labelScale = 1.0f
        )
        
        val Expanded = AdaptiveTypography(
            displayScale = 1.2f,
            headlineScale = 1.1f,
            titleScale = 1.1f,
            bodyScale = 1.05f,
            labelScale = 1.05f
        )
    }
}

/**
 * Get maximum content width for large screens
 */
@Composable
fun getMaxContentWidth(): Dp {
    return when (getScreenSize()) {
        ScreenSize.Compact -> Dp.Unspecified
        ScreenSize.Medium -> 800.dp
        ScreenSize.Expanded -> 1200.dp
    }
}

/**
 * Get adaptive card size for grid layouts
 */
@Composable
fun getAdaptiveCardSize(contentType: ContentType): Dp {
    val screenSize = getScreenSize()
    val spacing = getAdaptiveSpacing()
    
    return when (contentType) {
        ContentType.BookmarkCard -> when (screenSize) {
            ScreenSize.Compact -> Dp.Unspecified // Full width
            ScreenSize.Medium -> 320.dp
            ScreenSize.Expanded -> 360.dp
        }
        ContentType.CollectionCard -> when (screenSize) {
            ScreenSize.Compact -> 160.dp
            ScreenSize.Medium -> 180.dp
            ScreenSize.Expanded -> 200.dp
        }
        ContentType.FeatureCard -> when (screenSize) {
            ScreenSize.Compact -> Dp.Unspecified
            ScreenSize.Medium -> 280.dp
            ScreenSize.Expanded -> 320.dp
        }
        ContentType.QuickAction -> when (screenSize) {
            ScreenSize.Compact -> 140.dp
            ScreenSize.Medium -> 160.dp
            ScreenSize.Expanded -> 180.dp
        }
    }
}

/**
 * Check if current orientation is landscape
 */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Check if device should use side navigation instead of bottom navigation
 */
@Composable
fun shouldUseSideNavigation(): Boolean {
    val screenSize = getScreenSize()
    val isLandscapeMode = isLandscape()
    
    return screenSize == ScreenSize.Expanded || 
           (screenSize == ScreenSize.Medium && isLandscapeMode)
}

/**
 * Get adaptive hero height for landing page
 */
@Composable
fun getAdaptiveHeroHeight(): Dp {
    val screenSize = getScreenSize()
    val isLandscapeMode = isLandscape()
    
    return when {
        screenSize == ScreenSize.Compact && !isLandscapeMode -> 300.dp
        screenSize == ScreenSize.Compact && isLandscapeMode -> 200.dp
        screenSize == ScreenSize.Medium -> 350.dp
        screenSize == ScreenSize.Expanded -> 400.dp
        else -> 300.dp
    }
}

/**
 * Get responsive hero height (alias for getAdaptiveHeroHeight)
 */
@Composable
fun responsiveHeroHeight(): Dp = getAdaptiveHeroHeight()

/**
 * Get responsive content padding
 */
@Composable
fun responsiveContentPadding(): Dp {
    return getAdaptiveSpacing().contentPadding
}

/**
 * Get responsive card width for feature cards
 */
@Composable
fun responsiveCardWidth(): Dp {
    val screenSize = getScreenSize()
    
    return when (screenSize) {
        ScreenSize.Compact -> 280.dp
        ScreenSize.Medium -> 320.dp
        ScreenSize.Expanded -> 360.dp
    }
}

/**
 * Get responsive font scale for typography
 */
@Composable
fun responsiveFontScale(): Float {
    return getAdaptiveTypography().bodyScale
}

/**
 * Get responsive number of columns for quick actions
 */
@Composable
fun responsiveQuickActionColumns(): Int {
    return getAdaptiveColumns(ContentType.QuickAction)
}

/**
 * Get responsive maximum content width
 */
@Composable
fun responsiveMaxContentWidth(): Dp {
    return getMaxContentWidth()
}

/**
 * Remember screen size (composable alternative to getScreenSize)
 */
@Composable
fun rememberScreenSize(): ScreenSize {
    return getScreenSize()
}