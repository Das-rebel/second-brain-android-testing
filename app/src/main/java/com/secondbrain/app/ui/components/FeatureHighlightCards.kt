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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkTheme
import com.secondbrain.app.ui.utils.responsiveCardWidth
import com.secondbrain.app.ui.utils.responsiveContentPadding
import kotlinx.coroutines.delay

/**
 * Feature highlight cards showcasing app capabilities with Japanese aesthetics
 */
@Composable
fun FeatureHighlightSection(
    onFeatureClick: (FeatureType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val features = listOf(
        FeatureData(
            type = FeatureType.SMART_BOOKMARKS,
            title = "Smart Bookmarks",
            description = "Organize knowledge with AI-powered tagging and instant search",
            icon = Icons.Default.Star,
            gradient = SparkTheme.colorScheme.sakuraGradient
        ),
        FeatureData(
            type = FeatureType.COLLECTIONS,
            title = "Curated Collections",
            description = "Create themed collections like a traditional Japanese library",
            icon = Icons.Default.Star,
            gradient = SparkTheme.colorScheme.goldGradient
        ),
        FeatureData(
            type = FeatureType.VISUAL_SEARCH,
            title = "Visual Discovery",
            description = "Find connections between ideas with elegant visual search",
            icon = Icons.Default.Search,
            gradient = SparkTheme.colorScheme.washiGradient
        ),
        FeatureData(
            type = FeatureType.MINDFUL_READING,
            title = "Mindful Reading",
            description = "Distraction-free reading with traditional paper aesthetics",
            icon = Icons.Default.Star,
            gradient = SparkTheme.colorScheme.inkGradient
        )
    )
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section header
        FeatureSectionHeader()
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
        
        // Feature cards in horizontal scroll
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = responsiveContentPadding()),
            horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium)
        ) {
            itemsIndexed(features) { index, feature ->
                FeatureHighlightCard(
                    feature = feature,
                    onClick = { onFeatureClick(feature.type) },
                    animationDelay = index * 200L
                )
            }
        }
    }
}

/**
 * Individual feature highlight card with traditional paper design
 */
@Composable
fun FeatureHighlightCard(
    feature: FeatureData,
    onClick: () -> Unit,
    animationDelay: Long = 0L,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    // Animation states
    val cardScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isVisible -> 1.0f
            else -> 0.8f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )
    
    val cardAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "cardAlpha"
    )
    
    // Trigger animation with delay
    LaunchedEffect(Unit) {
        delay(animationDelay)
        isVisible = true
    }
    
    // Floating animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "iconFloat")
    val iconOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconFloat"
    )
    
    Card(
        modifier = modifier
            .width(responsiveCardWidth())
            .height(200.dp)
            .scale(cardScale)
            .graphicsLayer { alpha = cardAlpha }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        shape = SparkThreadDesign.Shapes.Large,
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
            
            // Feature-specific gradient accent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                SparkTheme.colorScheme.sakura.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(300f, 300f)
                        )
                    )
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SparkThreadDesign.Spacing.Large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Feature icon with floating animation
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .offset(y = iconOffset.dp)
                        .background(
                            brush = feature.gradient,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Ink shadow overlay for depth
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = SparkThreadDesign.PaperEffects.InkShadow.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )
                    
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = null,
                        tint = SparkTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
                
                // Feature title
                Text(
                    text = feature.title,
                    style = SparkThreadDesign.Typography.TitleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = SparkThreadDesign.Colors.Primary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                
                // Feature description
                Text(
                    text = feature.description,
                    style = SparkThreadDesign.Typography.BodyMedium.copy(
                        lineHeight = 20.sp
                    ),
                    color = SparkThreadDesign.Colors.MutedForeground,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Decorative element at bottom
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(
                            brush = feature.gradient,
                            shape = SparkThreadDesign.Shapes.Small
                        )
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
 * Section header for features
 */
@Composable
private fun FeatureSectionHeader() {
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
                    brush = SparkTheme.colorScheme.inkGradient,
                    shape = SparkThreadDesign.Shapes.Small
                )
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
        
        Text(
            text = "Discover Features",
            style = SparkThreadDesign.Typography.HeadlineSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            ),
            color = SparkThreadDesign.Colors.Primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
        
        Text(
            text = "Thoughtfully crafted tools for your knowledge journey",
            style = SparkThreadDesign.Typography.BodyLarge,
            color = SparkThreadDesign.Colors.MutedForeground,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Data class for feature information
 */
data class FeatureData(
    val type: FeatureType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: Brush
)

/**
 * Enum for different feature types
 */
enum class FeatureType {
    SMART_BOOKMARKS,
    COLLECTIONS,
    VISUAL_SEARCH,
    MINDFUL_READING
}