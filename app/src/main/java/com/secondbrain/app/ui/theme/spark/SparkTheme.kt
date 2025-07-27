package com.secondbrain.app.ui.theme.spark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.secondbrain.app.design.SparkThreadDesign

/**
 * Spark Thread Japanese Stationery inspired theme system
 * Provides authentic Japanese stationery aesthetics with washi paper textures,
 * ink gradients, sakura accents, and traditional design elements
 */
object SparkTheme {
    val colorScheme: SparkColorScheme
        @Composable
        get() = LocalSparkColorScheme.current
    
    val typography: SparkTypographyScheme
        @Composable
        get() = LocalSparkTypography.current
    
    val shapes: SparkShapes
        @Composable
        get() = LocalSparkShapes.current
    
    val spacing: SparkSpacing
        @Composable
        get() = LocalSparkSpacing.current
}

/**
 * Spark color scheme based on Japanese stationery design
 * Features traditional colors like Sakura, Bamboo, Gold, Washi paper, Ink, and Seal Red
 */
@Immutable
data class SparkColorScheme(
    val primary: Color = SparkThreadDesign.Colors.Primary,
    val onPrimary: Color = SparkThreadDesign.Colors.PrimaryForeground,
    val secondary: Color = SparkThreadDesign.Colors.Secondary,
    val onSecondary: Color = SparkThreadDesign.Colors.SecondaryForeground,
    val tertiary: Color = SparkThreadDesign.Colors.Accent,
    val onTertiary: Color = SparkThreadDesign.Colors.AccentForeground,
    val background: Color = SparkThreadDesign.Colors.Background,
    val onBackground: Color = SparkThreadDesign.Colors.Foreground,
    val surface: Color = SparkThreadDesign.Colors.Card,
    val onSurface: Color = SparkThreadDesign.Colors.CardForeground,
    val surfaceVariant: Color = SparkThreadDesign.Colors.Muted,
    val onSurfaceVariant: Color = SparkThreadDesign.Colors.MutedForeground,
    val outline: Color = SparkThreadDesign.Colors.Border,
    val outlineVariant: Color = SparkThreadDesign.Colors.Input,
    val error: Color = SparkThreadDesign.Colors.Destructive,
    val onError: Color = SparkThreadDesign.Colors.DestructiveForeground,
    
    // Japanese-specific colors
    val sakura: Color = SparkThreadDesign.Colors.Sakura,
    val bamboo: Color = SparkThreadDesign.Colors.Bamboo,
    val gold: Color = SparkThreadDesign.Colors.Gold,
    val washi: Color = SparkThreadDesign.Colors.Washi,
    val ink: Color = SparkThreadDesign.Colors.Ink,
    val sealRed: Color = SparkThreadDesign.Colors.SealRed,
    
    // Paper textures and gradients
    val paperTexture: Brush = SparkThreadDesign.Colors.BackgroundGradient,
    val cardGradient: Brush = SparkThreadDesign.Colors.CardGradient,
    val inkGradient: Brush = SparkThreadDesign.Colors.InkGradient,
    val sakuraGradient: Brush = SparkThreadDesign.Colors.SakuraGradient,
    val goldGradient: Brush = SparkThreadDesign.Colors.GoldGradient,
    val washiGradient: Brush = SparkThreadDesign.Colors.WashiGradient
)

/**
 * Spark typography system inspired by Japanese calligraphy
 * Features elegant, readable styles with proper spacing for traditional aesthetic
 */
@Immutable
data class SparkTypographyScheme(
    val displayLarge: TextStyle = SparkThreadDesign.Typography.DisplayLarge,
    val displayMedium: TextStyle = SparkThreadDesign.Typography.DisplayMedium,
    val displaySmall: TextStyle = SparkThreadDesign.Typography.DisplaySmall,
    val headlineLarge: TextStyle = SparkThreadDesign.Typography.HeadlineLarge,
    val headlineMedium: TextStyle = SparkThreadDesign.Typography.HeadlineMedium,
    val headlineSmall: TextStyle = SparkThreadDesign.Typography.HeadlineSmall,
    val titleLarge: TextStyle = SparkThreadDesign.Typography.TitleLarge,
    val titleMedium: TextStyle = SparkThreadDesign.Typography.TitleMedium,
    val titleSmall: TextStyle = SparkThreadDesign.Typography.TitleSmall,
    val bodyLarge: TextStyle = SparkThreadDesign.Typography.BodyLarge,
    val bodyMedium: TextStyle = SparkThreadDesign.Typography.BodyMedium,
    val bodySmall: TextStyle = SparkThreadDesign.Typography.BodySmall,
    val labelLarge: TextStyle = SparkThreadDesign.Typography.LabelLarge,
    val labelMedium: TextStyle = SparkThreadDesign.Typography.LabelMedium,
    val labelSmall: TextStyle = SparkThreadDesign.Typography.LabelSmall
)

/**
 * Spark shapes system with traditional paper aesthetics
 * Soft, organic curves that mimic traditional Japanese paper documents
 */
@Immutable
data class SparkShapes(
    val extraSmall: RoundedCornerShape = SparkThreadDesign.Shapes.ExtraSmall,
    val small: RoundedCornerShape = SparkThreadDesign.Shapes.Small,
    val medium: RoundedCornerShape = SparkThreadDesign.Shapes.Medium,
    val large: RoundedCornerShape = SparkThreadDesign.Shapes.Large,
    val extraLarge: RoundedCornerShape = SparkThreadDesign.Shapes.ExtraLarge,
    val stamp: RoundedCornerShape = SparkThreadDesign.Shapes.Stamp
)

/**
 * Spark spacing system with harmonious Japanese proportions
 * Based on traditional Japanese design principles for balanced layouts
 */
@Immutable
data class SparkSpacing(
    val extraSmall: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.ExtraSmall,
    val small: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.Small,
    val medium: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.Medium,
    val large: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.Large,
    val extraLarge: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.ExtraLarge,
    val huge: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.Huge,
    val massive: androidx.compose.ui.unit.Dp = SparkThreadDesign.Spacing.Massive
)

// Composition locals
val LocalSparkColorScheme = staticCompositionLocalOf { SparkColorScheme() }
val LocalSparkTypography = staticCompositionLocalOf { SparkTypographyScheme() }
val LocalSparkShapes = staticCompositionLocalOf { SparkShapes() }
val LocalSparkSpacing = staticCompositionLocalOf { SparkSpacing() }

/**
 * Main Spark theme composable with Japanese stationery aesthetics
 * Provides authentic paper textures, ink gradients, and traditional design elements
 */
@Composable
fun SparkTheme(
    darkTheme: Boolean = false,
    colorScheme: SparkColorScheme = SparkColorScheme(),
    typography: SparkTypographyScheme = SparkTypographyScheme(),
    shapes: SparkShapes = SparkShapes(),
    spacing: SparkSpacing = SparkSpacing(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSparkColorScheme provides colorScheme,
        LocalSparkTypography provides typography,
        LocalSparkShapes provides shapes,
        LocalSparkSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = colorScheme.primary,
                    onPrimary = colorScheme.onPrimary,
                    secondary = colorScheme.sakura,
                    onSecondary = colorScheme.ink,
                    tertiary = colorScheme.gold,
                    onTertiary = colorScheme.ink,
                    background = colorScheme.ink,
                    onBackground = colorScheme.washi,
                    surface = colorScheme.primary,
                    onSurface = colorScheme.onPrimary,
                    surfaceVariant = colorScheme.surfaceVariant,
                    onSurfaceVariant = colorScheme.onSurfaceVariant,
                    outline = colorScheme.outline,
                    error = colorScheme.sealRed,
                    onError = colorScheme.washi
                )
            } else {
                lightColorScheme(
                    primary = colorScheme.primary,
                    onPrimary = colorScheme.onPrimary,
                    secondary = colorScheme.sakura,
                    onSecondary = colorScheme.ink,
                    tertiary = colorScheme.gold,
                    onTertiary = colorScheme.ink,
                    background = colorScheme.background,
                    onBackground = colorScheme.onBackground,
                    surface = colorScheme.surface,
                    onSurface = colorScheme.onSurface,
                    surfaceVariant = colorScheme.washi,
                    onSurfaceVariant = colorScheme.onSurfaceVariant,
                    outline = colorScheme.outline,
                    error = colorScheme.sealRed,
                    onError = colorScheme.onError
                )
            }
        ) {
            // Enhanced gradient background with paper texture overlay
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Primary gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (darkTheme) {
                                colorScheme.inkGradient
                            } else {
                                colorScheme.paperTexture
                            }
                        )
                )
                
                // Paper texture overlay for authentic washi feel
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = if (darkTheme) {
                                SparkThreadDesign.PaperEffects.InkShadow
                            } else {
                                SparkThreadDesign.PaperEffects.WashiTexture
                            }
                        )
                )
                
                // Content with subtle paper grain effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = SparkThreadDesign.PaperEffects.PaperGrain
                        )
                ) {
                    content()
                }
            }
        }
    }
}