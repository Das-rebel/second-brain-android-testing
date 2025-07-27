package com.secondbrain.app.ui.theme.spark

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.secondbrain.app.design.SparkThreadDesign

/**
 * Modern glass-morphism card component inspired by Spark Thread Stationery UI
 */
@Composable
fun SparkCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = SparkTheme.shapes.large,
    containerColor: Color = SparkTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = SparkThreadDesign.Elevation.Card,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (enabled) shadowElevation else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "elevation"
    )
    
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier
                .shadow(
                    elevation = animatedElevation,
                    shape = shape,
                    ambientColor = SparkThreadDesign.PaperEffects.PaperShadow,
                    spotColor = SparkThreadDesign.PaperEffects.SoftShadow
                ),
            enabled = enabled,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            border = border,
            interactionSource = interactionSource
        ) {
            CardContent(shape, content)
        }
    } else {
        Surface(
            modifier = modifier
                .shadow(
                    elevation = animatedElevation,
                    shape = shape,
                    ambientColor = SparkThreadDesign.PaperEffects.PaperShadow,
                    spotColor = SparkThreadDesign.PaperEffects.SoftShadow
                ),
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            border = border
        ) {
            CardContent(shape, content)
        }
    }
}

@Composable
private fun CardContent(
    shape: Shape,
    content: @Composable ColumnScope.() -> Unit
) {
    // Enhanced Japanese paper texture with gradient layers
    Box {
        // Primary card gradient background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = SparkTheme.colorScheme.cardGradient,
                    shape = shape
                )
        )
        
        // Washi paper texture overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = SparkTheme.colorScheme.washiGradient,
                    shape = shape
                )
        )
        
        // Paper grain effect for authentic texture
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = SparkThreadDesign.PaperEffects.PaperGrain,
                    shape = shape
                )
        )
        
        // Subtle washi texture overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = SparkThreadDesign.PaperEffects.WashiTexture,
                    shape = shape
                )
        )
        
        // Main content with enhanced padding
        Column(
            modifier = Modifier.padding(SparkThreadDesign.Spacing.Large),
            content = content
        )
    }
}