package com.secondbrain.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Onboarding flow with Japanese stationery aesthetics and smooth animations
 */
@Composable
fun OnboardingFlow(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onboardingPages = listOf(
        OnboardingPage(
            title = "Welcome to Second Brain",
            subtitle = "知識の庭へようこそ",
            description = "Your personal knowledge garden where wisdom grows with traditional Japanese elegance",
            icon = Icons.Default.Star,
            gradient = SparkTheme.colorScheme.sakuraGradient
        ),
        OnboardingPage(
            title = "Collect & Organize",
            subtitle = "整理する",
            description = "Gather your bookmarks like carefully pressed flowers in a traditional Japanese album",
            icon = Icons.Default.Star,
            gradient = SparkTheme.colorScheme.goldGradient
        ),
        OnboardingPage(
            title = "Search & Discover",
            subtitle = "発見する",
            description = "Find connections between ideas with the mindfulness of traditional tea ceremony",
            icon = Icons.Default.Search,
            gradient = SparkTheme.colorScheme.washiGradient
        ),
        OnboardingPage(
            title = "Create & Share",
            subtitle = "共有する",
            description = "Share your curated collections like sharing wisdom through traditional scrolls",
            icon = Icons.Default.Share,
            gradient = SparkTheme.colorScheme.inkGradient
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background with traditional paper texture
        OnboardingBackground()
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Onboarding content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = onboardingPages[page],
                    isActive = pagerState.currentPage == page
                )
            }
            
            // Bottom navigation area
            OnboardingBottomNavigation(
                currentPage = pagerState.currentPage,
                totalPages = onboardingPages.size,
                onNextClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onComplete()
                    }
                },
                onSkipClick = onComplete,
                modifier = Modifier.padding(SparkThreadDesign.Spacing.Large)
            )
        }
        
        // Floating decorative elements
        FloatingSakuraPetals(
            count = 4,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Individual onboarding page content
 */
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible && isActive) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "contentAlpha"
    )
    
    val contentScale by animateFloatAsState(
        targetValue = if (isVisible && isActive) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "contentScale"
    )
    
    LaunchedEffect(isActive) {
        if (isActive) {
            delay(200)
            isVisible = true
        } else {
            isVisible = false
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SparkThreadDesign.Spacing.Large)
            .scale(contentScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with floating animation
        val infiniteTransition = rememberInfiniteTransition(label = "iconFloat")
        val iconOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "iconFloat"
        )
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(y = iconOffset.dp)
                .background(
                    brush = page.gradient,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Ink shadow overlay for depth
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = SparkThreadDesign.PaperEffects.InkShadow.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )
            
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = SparkTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(60.dp)
                    .graphicsLayer { alpha = contentAlpha }
            )
        }
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Huge))
        
        // Title
        Text(
            text = page.title,
            style = SparkThreadDesign.Typography.HeadlineLarge.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            ),
            color = SparkThreadDesign.Colors.Primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { alpha = contentAlpha }
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
        
        // Japanese subtitle
        Text(
            text = page.subtitle,
            style = SparkThreadDesign.Typography.TitleLarge.copy(
                fontWeight = FontWeight.Normal,
                letterSpacing = 2.sp
            ),
            color = SparkTheme.colorScheme.sakura,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { alpha = contentAlpha }
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
        
        // Description
        Text(
            text = page.description,
            style = SparkThreadDesign.Typography.BodyLarge.copy(
                lineHeight = 28.sp
            ),
            color = SparkThreadDesign.Colors.MutedForeground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .graphicsLayer { alpha = contentAlpha }
                .padding(horizontal = SparkThreadDesign.Spacing.Medium)
        )
    }
}

/**
 * Bottom navigation for onboarding
 */
@Composable
private fun OnboardingBottomNavigation(
    currentPage: Int,
    totalPages: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Small)
        ) {
            repeat(totalPages) { index ->
                val isActive = index == currentPage
                val indicatorScale by animateFloatAsState(
                    targetValue = if (isActive) 1.2f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "indicatorScale"
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(indicatorScale)
                        .background(
                            color = if (isActive) {
                                SparkThreadDesign.Colors.Primary
                            } else {
                                SparkThreadDesign.Colors.MutedForeground.copy(alpha = 0.3f)
                            },
                            shape = CircleShape
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skip button
            TextButton(
                onClick = onSkipClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SparkThreadDesign.Colors.MutedForeground
                )
            ) {
                Text(
                    text = "Skip",
                    style = SparkThreadDesign.Typography.TitleMedium
                )
            }
            
            // Next/Get Started button
            OnboardingNextButton(
                text = if (currentPage == totalPages - 1) "Get Started" else "Next",
                onClick = onNextClick
            )
        }
    }
}

/**
 * Styled next button for onboarding
 */
@Composable
private fun OnboardingNextButton(
    text: String,
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
            .scale(buttonScale)
            .clip(SparkThreadDesign.Shapes.Medium)
            .background(brush = SparkTheme.colorScheme.inkGradient)
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(
                horizontal = SparkThreadDesign.Spacing.Large,
                vertical = SparkThreadDesign.Spacing.Medium
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Small)
        ) {
            Text(
                text = text,
                style = SparkThreadDesign.Typography.TitleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = SparkTheme.colorScheme.onPrimary
            )
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = SparkTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
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
 * Onboarding background with traditional aesthetics
 */
@Composable
private fun OnboardingBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Primary gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SparkTheme.colorScheme.paperTexture
                )
        )
        
        // Washi texture overlay
        WashiPaperTexture()
        
        // Subtle overlay for content readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SparkThreadDesign.PaperEffects.WashiTexture.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * Data class for onboarding page content
 */
data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val gradient: Brush
)