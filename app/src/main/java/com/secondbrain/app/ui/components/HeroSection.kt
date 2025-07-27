package com.secondbrain.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkTheme
import com.secondbrain.app.ui.utils.responsiveHeroHeight
import com.secondbrain.app.ui.utils.responsiveContentPadding
import com.secondbrain.app.ui.utils.responsiveFontScale
import kotlinx.coroutines.delay

/**
 * Hero section for the Second Brain app with Japanese stationery aesthetics
 * Features traditional design elements, elegant typography, and subtle animations
 */
@Composable
fun HeroSection(
    onGetStartedClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Animation for entrance effect
    val fadeInAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    
    val slideUpAnimation by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "slideUp"
    )
    
    // Trigger animation on composition
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(responsiveHeroHeight())
    ) {
        // Background with traditional paper gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SparkTheme.colorScheme.paperTexture
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
        
        // Decorative sakura elements (floating)
        SakuraDecorations()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = responsiveContentPadding())
                .offset(y = slideUpAnimation.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title with traditional Japanese styling
            Text(
                text = "Second Brain",
                style = SparkThreadDesign.Typography.DisplayMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.5.sp
                ),
                color = SparkThreadDesign.Colors.Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = fadeInAnimation }
            )
            
            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
            
            // Traditional Japanese subtitle with ink brush effect
            Text(
                text = "知識の庭",
                style = SparkThreadDesign.Typography.HeadlineSmall.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.sp
                ),
                color = SparkTheme.colorScheme.sakura,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = fadeInAnimation }
            )
            
            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
            
            // Descriptive subtitle
            Text(
                text = "Your personal knowledge garden,\ncurated with Japanese elegance",
                style = SparkThreadDesign.Typography.BodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = SparkThreadDesign.Colors.MutedForeground,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = fadeInAnimation }
            )
            
            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
            
            // Enhanced get started button with traditional seal design
            GetStartedButton(
                onClick = onGetStartedClick,
                modifier = Modifier.graphicsLayer { alpha = fadeInAnimation }
            )
        }
        
        // Ink splashes decorative elements
        InkSplashDecorations()
    }
}

/**
 * Elegant get started button with traditional Japanese seal design
 */
@Composable
private fun GetStartedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )
    
    Box(
        modifier = modifier
            .size(width = 200.dp, height = 56.dp)
    ) {
        // Traditional paper background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SparkTheme.colorScheme.washiGradient,
                    shape = SparkThreadDesign.Shapes.Medium
                )
        )
        
        // Ink gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SparkTheme.colorScheme.inkGradient,
                    shape = SparkThreadDesign.Shapes.Medium
                )
        )
        
        // Seal imprint effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = SparkThreadDesign.PaperEffects.SealImprint.copy(alpha = 0.1f),
                    shape = SparkThreadDesign.Shapes.Medium
                )
        )
        
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxSize()
                .scale(buttonScale),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = SparkTheme.colorScheme.onPrimary
            ),
            shape = SparkThreadDesign.Shapes.Medium,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = SparkThreadDesign.Elevation.Card,
                pressedElevation = SparkThreadDesign.Elevation.Paper
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Small)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Begin Journey",
                    style = SparkThreadDesign.Typography.TitleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Floating sakura decorations with gentle animations
 */
@Composable
private fun SakuraDecorations() {
    val infiniteTransition = rememberInfiniteTransition(label = "sakura")
    
    // Multiple sakura petals with different animation timings
    repeat(5) { index ->
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 20000 + (index * 2000),
                    easing = LinearEasing
                )
            ),
            label = "sakuraRotation$index"
        )
        
        val verticalOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + (index * 500),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sakuraFloat$index"
        )
        
        Box(
            modifier = Modifier
                .offset(
                    x = (50 + index * 60).dp,
                    y = (30 + index * 40 + verticalOffset).dp
                )
                .size(12.dp)
                .rotate(rotation)
                .background(
                    brush = SparkTheme.colorScheme.sakuraGradient,
                    shape = CircleShape
                )
                .graphicsLayer { alpha = 0.3f }
        )
    }
}

/**
 * Subtle ink splash decorative elements
 */
@Composable
private fun InkSplashDecorations() {
    val infiniteTransition = rememberInfiniteTransition(label = "inkSplash")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "inkPulse"
    )
    
    // Top right ink splash
    Box(
        modifier = Modifier
            .offset(x = 320.dp, y = 20.dp)
            .size(40.dp)
            .scale(pulseScale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SparkTheme.colorScheme.ink.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
    
    // Bottom left ink splash
    Box(
        modifier = Modifier
            .offset(x = 20.dp, y = 250.dp)
            .size(60.dp)
            .scale(pulseScale * 0.8f)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SparkTheme.colorScheme.sakura.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}