package com.secondbrain.app.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Japanese Stationery Design System - Exact replica of Spark Thread UI
 * Features: Washi paper textures, ink gradients, sakura accents, traditional aesthetic
 */
object SparkThreadDesign {
    
    // Japanese Stationery Color Palette - Based on Spark Thread CSS
    object Colors {
        // Base Paper Colors - Traditional Japanese stationery
        val Background = Color(0xFFFBF9F5)      // Warm cream paper (hsl(45, 25%, 97%))
        val Foreground = Color(0xFF1A1412)      // Deep ink black (hsl(220, 40%, 25%))
        val Card = Color(0xFFFFFDF7)            // Paper white
        val CardForeground = Color(0xFF1A1412)  // Ink on paper
        val Popover = Color(0xFFFFFDF7)         // Paper white
        val PopoverForeground = Color(0xFF1A1412) // Ink on paper
        
        // Primary Colors - Deep Ink Blue
        val Primary = Color(0xFF2D3748)         // Deep ink (hsl(220, 40%, 25%))
        val PrimaryForeground = Color(0xFFFBF9F5) // Paper on ink
        
        // Secondary Colors - Muted Paper Tones  
        val Secondary = Color(0xFFF2F0EA)       // Soft paper (hsl(45, 25%, 92%))
        val SecondaryForeground = Color(0xFF1A1412) // Ink on soft paper
        
        // Muted Colors - Subtle Washi Tones
        val Muted = Color(0xFFF2F0EA)           // Muted paper
        val MutedForeground = Color(0xFF6B645C) // Faded ink (hsl(45, 10%, 40%))
        
        // Accent Colors - Traditional Japanese Palette
        val Accent = Color(0xFFF2F0EA)          // Accent paper
        val AccentForeground = Color(0xFF1A1412) // Ink on accent
        
        // Destructive Colors - Seal Red
        val Destructive = Color(0xFFDC2626)     // Traditional seal red
        val DestructiveForeground = Color(0xFFFBF9F5) // Paper on red
        
        // Border and Input Colors
        val Border = Color(0xFFE8E4DB)          // Soft paper edge (hsl(45, 25%, 88%))
        val Input = Color(0xFFE8E4DB)           // Input paper edge
        val Ring = Color(0xFF2D3748)            // Ink ring focus
        
        // Special Japanese Colors
        val Sakura = Color(0xFFE2A3A3)          // Cherry blossom pink
        val Bamboo = Color(0xFF8FBC8F)          // Bamboo green  
        val Gold = Color(0xFFD4AF37)            // Traditional gold
        val Washi = Color(0xFFF7F3E9)           // Washi paper texture
        val Ink = Color(0xFF1A202C)             // Pure ink black
        val SealRed = Color(0xFFB85450)         // Seal stamp red
        
        // Gradient Backgrounds - Paper Textures
        val BackgroundGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFBF9F5), // Top cream
                Color(0xFFF7F3E9)  // Bottom washi
            )
        )
        
        val CardGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFDF7), // Paper white
                Color(0xFFFBF9F5)  // Cream edge
            )
        )
        
        val InkGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF2D3748), // Deep ink
                Color(0xFF1A202C)  // Pure ink
            )
        )
        
        val SakuraGradient = Brush.radialGradient(
            colors = listOf(
                Color(0xFFE2A3A3), // Sakura pink
                Color(0xFFD4969C)  // Deeper sakura
            )
        )
        
        val GoldGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFD4AF37), // Gold
                Color(0xFFB8941F)  // Deep gold
            )
        )
        
        val WashiGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFF7F3E9), // Washi beige
                Color(0xFFF2F0EA)  // Soft paper
            )
        )
    }
    
    // Japanese Calligraphy-inspired Typography - Elegant and readable
    object Typography {
        
        val DisplayLarge = TextStyle(
            fontWeight = FontWeight.Light,       // Elegant brush strokes
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = (-0.5).sp
        )
        
        val DisplayMedium = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.25).sp
        )
        
        val DisplaySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp,
            lineHeight = 38.sp,
            letterSpacing = 0.sp
        )
        
        val HeadlineLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        )
        
        val HeadlineMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        )
        
        val HeadlineSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        )
        
        val TitleLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        )
        
        val TitleMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        )
        
        val TitleSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
        
        val BodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        )
        
        val BodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
        
        val BodySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.1.sp
        )
        
        val LabelLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
        
        val LabelMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.1.sp
        )
        
        val LabelSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.1.sp
        )
    }
    
    // Traditional Paper Shapes - Soft, organic curves
    object Shapes {
        val ExtraSmall = RoundedCornerShape(6.dp)   // Soft corners
        val Small = RoundedCornerShape(12.dp)       // Traditional paper feel
        val Medium = RoundedCornerShape(16.dp)      // Gentle curves
        val Large = RoundedCornerShape(20.dp)       // Paper document corners
        val ExtraLarge = RoundedCornerShape(28.dp)  // Large paper sheets
        val Stamp = RoundedCornerShape(50.dp)       // Traditional stamp/seal
    }
    
    // Japanese Spacing System - Harmonious proportions
    object Spacing {
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 16.dp
        val Large = 24.dp
        val ExtraLarge = 32.dp
        val Huge = 48.dp
        val Massive = 64.dp
    }
    
    // Paper Elevation and Depth - Stacked paper effect
    object Elevation {
        val None = 0.dp
        val Paper = 1.dp           // Single sheet
        val Card = 2.dp            // Card on paper
        val Floating = 4.dp        // Floating paper
        val Modal = 8.dp           // Modal paper overlay
        val Maximum = 16.dp        // High stacked papers
    }
    
    // Traditional Paper Effects - Washi textures and ink shadows
    object PaperEffects {
        val SoftShadow = Color(0x08000000)      // Very subtle shadow
        val PaperShadow = Color(0x12000000)     // Paper depth shadow
        val InkShadow = Color(0x1A2D3748)       // Ink color shadow
        val WashiTexture = Color(0x05D4AF37)    // Subtle gold texture overlay
        val SealImprint = Color(0x1AB85450)     // Seal stamp impression
        val PaperGrain = Color(0x03000000)      // Paper texture grain
    }
}

// Keep ModernDesignSystem as an alias for backward compatibility
typealias ModernDesignSystem = SparkThreadDesign

/**
 * Create Japanese stationery color scheme using our design system
 */
@Composable
fun sparkThreadColorScheme(
    darkTheme: Boolean = false
): ColorScheme {
    return if (darkTheme) {
        // Dark theme with ink-dominant colors
        darkColorScheme(
            primary = SparkThreadDesign.Colors.Primary,
            onPrimary = SparkThreadDesign.Colors.PrimaryForeground,
            primaryContainer = SparkThreadDesign.Colors.Ink,
            onPrimaryContainer = SparkThreadDesign.Colors.Background,
            secondary = SparkThreadDesign.Colors.Sakura,
            onSecondary = SparkThreadDesign.Colors.Foreground,
            secondaryContainer = SparkThreadDesign.Colors.Secondary,
            onSecondaryContainer = SparkThreadDesign.Colors.SecondaryForeground,
            tertiary = SparkThreadDesign.Colors.Gold,
            onTertiary = SparkThreadDesign.Colors.Foreground,
            background = SparkThreadDesign.Colors.Ink,
            onBackground = SparkThreadDesign.Colors.Background,
            surface = SparkThreadDesign.Colors.Primary,
            onSurface = SparkThreadDesign.Colors.PrimaryForeground,
            surfaceVariant = SparkThreadDesign.Colors.Muted,
            onSurfaceVariant = SparkThreadDesign.Colors.MutedForeground,
            error = SparkThreadDesign.Colors.Destructive,
            onError = SparkThreadDesign.Colors.DestructiveForeground
        )
    } else {
        // Light theme with cream paper colors
        lightColorScheme(
            primary = SparkThreadDesign.Colors.Primary,
            onPrimary = SparkThreadDesign.Colors.PrimaryForeground,
            primaryContainer = SparkThreadDesign.Colors.Secondary,
            onPrimaryContainer = SparkThreadDesign.Colors.SecondaryForeground,
            secondary = SparkThreadDesign.Colors.Sakura,
            onSecondary = SparkThreadDesign.Colors.Foreground,
            secondaryContainer = SparkThreadDesign.Colors.Washi,
            onSecondaryContainer = SparkThreadDesign.Colors.Foreground,
            tertiary = SparkThreadDesign.Colors.Gold,
            onTertiary = SparkThreadDesign.Colors.Foreground,
            background = SparkThreadDesign.Colors.Background,
            onBackground = SparkThreadDesign.Colors.Foreground,
            surface = SparkThreadDesign.Colors.Card,
            onSurface = SparkThreadDesign.Colors.CardForeground,
            surfaceVariant = SparkThreadDesign.Colors.Muted,
            onSurfaceVariant = SparkThreadDesign.Colors.MutedForeground,
            error = SparkThreadDesign.Colors.Destructive,
            onError = SparkThreadDesign.Colors.DestructiveForeground
        )
    }
}

/**
 * Create Japanese calligraphy typography using our design system
 */
@Composable
fun sparkThreadTypography(): androidx.compose.material3.Typography {
    return androidx.compose.material3.Typography(
        displayLarge = SparkThreadDesign.Typography.DisplayLarge,
        displayMedium = SparkThreadDesign.Typography.DisplayMedium,
        displaySmall = SparkThreadDesign.Typography.DisplaySmall,
        headlineLarge = SparkThreadDesign.Typography.HeadlineLarge,
        headlineMedium = SparkThreadDesign.Typography.HeadlineMedium,
        headlineSmall = SparkThreadDesign.Typography.HeadlineSmall,
        titleLarge = SparkThreadDesign.Typography.TitleLarge,
        titleMedium = SparkThreadDesign.Typography.TitleMedium,
        titleSmall = SparkThreadDesign.Typography.TitleSmall,
        bodyLarge = SparkThreadDesign.Typography.BodyLarge,
        bodyMedium = SparkThreadDesign.Typography.BodyMedium,
        bodySmall = SparkThreadDesign.Typography.BodySmall,
        labelLarge = SparkThreadDesign.Typography.LabelLarge,
        labelMedium = SparkThreadDesign.Typography.LabelMedium,
        labelSmall = SparkThreadDesign.Typography.LabelSmall
    )
}

/**
 * Traditional paper shapes using our design system
 */
@Composable
fun sparkThreadShapes(): Shapes {
    return Shapes(
        extraSmall = SparkThreadDesign.Shapes.ExtraSmall,
        small = SparkThreadDesign.Shapes.Small,
        medium = SparkThreadDesign.Shapes.Medium,
        large = SparkThreadDesign.Shapes.Large,
        extraLarge = SparkThreadDesign.Shapes.ExtraLarge
    )
}

// Backward compatibility
@Composable
fun modernColorScheme(darkTheme: Boolean = false) = sparkThreadColorScheme(darkTheme)

@Composable  
fun modernTypography() = sparkThreadTypography()

@Composable
fun modernShapes() = sparkThreadShapes()