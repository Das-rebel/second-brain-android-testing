package com.secondbrain.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkTheme
import com.secondbrain.app.ui.utils.responsiveContentPadding
import com.secondbrain.app.ui.utils.responsiveQuickActionColumns
import com.secondbrain.app.ui.utils.ScreenSize
import com.secondbrain.app.ui.utils.rememberScreenSize
import kotlinx.coroutines.delay

/**
 * Quick action buttons section for common operations with Japanese aesthetics
 */
@Composable
fun QuickActionSection(
    onAddBookmarkClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCollectionsClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val quickActions = listOf(
        QuickActionData(
            type = QuickActionType.ADD_BOOKMARK,
            title = "Add Bookmark",
            description = "Save a new link",
            icon = Icons.Default.Add,
            gradient = SparkTheme.colorScheme.sakuraGradient,
            onClick = onAddBookmarkClick
        ),
        QuickActionData(
            type = QuickActionType.SEARCH,
            title = "Search",
            description = "Find knowledge",
            icon = Icons.Default.Search,
            gradient = SparkTheme.colorScheme.goldGradient,
            onClick = onSearchClick
        ),
        QuickActionData(
            type = QuickActionType.COLLECTIONS,
            title = "Collections",
            description = "Browse library",
            icon = Icons.Default.Star,
            gradient = SparkTheme.colorScheme.washiGradient,
            onClick = onCollectionsClick
        ),
        QuickActionData(
            type = QuickActionType.FAVORITES,
            title = "Favorites",
            description = "Saved gems",
            icon = Icons.Default.Favorite,
            gradient = SparkTheme.colorScheme.inkGradient,
            onClick = onFavoritesClick
        )
    )
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section header
        QuickActionSectionHeader()
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
        
        // Quick action buttons - responsive layout
        val screenSize = rememberScreenSize()
        
        when (screenSize) {
            ScreenSize.Compact -> {
                // Two rows for compact screens
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = responsiveContentPadding()),
                    verticalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium)
                    ) {
                        quickActions.take(2).forEachIndexed { index, action ->
                            QuickActionButton(
                                action = action,
                                animationDelay = index * 100L,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium)
                    ) {
                        quickActions.drop(2).forEachIndexed { index, action ->
                            QuickActionButton(
                                action = action,
                                animationDelay = (index + 2) * 100L,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            else -> {
                // Single row for medium and expanded screens
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = responsiveContentPadding()),
                    horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium)
                ) {
                    quickActions.forEachIndexed { index, action ->
                        QuickActionButton(
                            action = action,
                            animationDelay = index * 100L,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual quick action button with traditional paper design and haptic feedback
 */
@Composable
fun QuickActionButton(
    action: QuickActionData,
    animationDelay: Long = 0L,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    
    // Animation states
    val buttonScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isVisible -> 1.0f
            else -> 0.7f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )
    
    val buttonAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "buttonAlpha"
    )
    
    // Trigger animation with delay
    LaunchedEffect(Unit) {
        delay(animationDelay)
        isVisible = true
    }
    
    // Floating animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
    val iconPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )
    
    Card(
        modifier = modifier
            .height(120.dp)
            .scale(buttonScale)
            .graphicsLayer { alpha = buttonAlpha }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isPressed = true
                action.onClick()
            },
        shape = SparkThreadDesign.Shapes.Medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = SparkThreadDesign.Elevation.Card,
            pressedElevation = SparkThreadDesign.Elevation.Paper
        )
    ) {
        Box {
            // Traditional paper background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = SparkTheme.colorScheme.cardGradient
                    )
            )
            
            // Washi texture overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = SparkThreadDesign.PaperEffects.WashiTexture
                    )
            )
            
            // Action-specific gradient accent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SparkTheme.colorScheme.sakura.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            radius = 100f
                        )
                    )
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SparkThreadDesign.Spacing.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Action icon with pulsing animation
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(iconPulse)
                        .background(
                            brush = action.gradient,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Ink shadow overlay for depth
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = SparkThreadDesign.PaperEffects.InkShadow.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )
                    
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = SparkTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                
                // Action title
                Text(
                    text = action.title,
                    style = SparkThreadDesign.Typography.TitleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    ),
                    color = SparkThreadDesign.Colors.Primary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Action description
                Text(
                    text = action.description,
                    style = SparkThreadDesign.Typography.LabelSmall.copy(
                        fontSize = 10.sp
                    ),
                    color = SparkThreadDesign.Colors.MutedForeground,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

/**
 * Section header for quick actions
 */
@Composable
private fun QuickActionSectionHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SparkThreadDesign.Spacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Traditional separator with ink design
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(3.dp)
                .background(
                    brush = SparkTheme.colorScheme.sakuraGradient,
                    shape = SparkThreadDesign.Shapes.Small
                )
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
        
        Text(
            text = "Quick Actions",
            style = SparkThreadDesign.Typography.HeadlineSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            ),
            color = SparkThreadDesign.Colors.Primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
        
        Text(
            text = "Common tasks at your fingertips",
            style = SparkThreadDesign.Typography.BodyLarge,
            color = SparkThreadDesign.Colors.MutedForeground,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Data class for quick action information
 */
data class QuickActionData(
    val type: QuickActionType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: Brush,
    val onClick: () -> Unit
)

/**
 * Enum for different quick action types
 */
enum class QuickActionType {
    ADD_BOOKMARK,
    SEARCH,
    COLLECTIONS,
    FAVORITES
}