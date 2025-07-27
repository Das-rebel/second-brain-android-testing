package com.secondbrain.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.components.*
import com.secondbrain.app.ui.theme.spark.SparkTheme
import com.secondbrain.app.ui.utils.responsiveContentPadding
import com.secondbrain.app.ui.utils.responsiveMaxContentWidth
import kotlinx.coroutines.delay

/**
 * Main landing page screen with hero section, features, and quick actions
 * Features parallax scrolling, smooth animations, and Japanese stationery aesthetics
 */
@Composable
fun LandingPageScreen(
    onGetStartedClick: () -> Unit = {},
    onFeatureClick: (FeatureType) -> Unit = {},
    onAddBookmarkClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCollectionsClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    
    // Parallax effect calculation
    val firstVisibleItemIndex by derivedStateOf { listState.firstVisibleItemIndex }
    val firstVisibleItemScrollOffset by derivedStateOf { listState.firstVisibleItemScrollOffset }
    
    val heroParallaxOffset = remember(firstVisibleItemIndex, firstVisibleItemScrollOffset) {
        if (firstVisibleItemIndex == 0) {
            firstVisibleItemScrollOffset * 0.5f
        } else {
            0f
        }
    }
    
    val backgroundParallaxOffset = remember(firstVisibleItemIndex, firstVisibleItemScrollOffset) {
        if (firstVisibleItemIndex == 0) {
            firstVisibleItemScrollOffset * 0.3f
        } else {
            0f
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Animated background with parallax effect
        LandingPageBackground(
            parallaxOffset = backgroundParallaxOffset
        )
        
        // Main content with smooth scroll and responsive width
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .widthIn(max = responsiveMaxContentWidth())
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
            // Hero section with parallax effect
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = heroParallaxOffset
                        }
                ) {
                    HeroSection(
                        onGetStartedClick = onGetStartedClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Decorative transition element
            item {
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
                TraditionalBorder(
                    thickness = 4f,
                    modifier = Modifier.padding(horizontal = responsiveContentPadding())
                )
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
            }
            
            // Quick actions section
            item {
                QuickActionSection(
                    onAddBookmarkClick = onAddBookmarkClick,
                    onSearchClick = onSearchClick,
                    onCollectionsClick = onCollectionsClick,
                    onFavoritesClick = onFavoritesClick,
                    modifier = Modifier.padding(vertical = SparkThreadDesign.Spacing.Large)
                )
            }
            
            // Decorative spacer
            item {
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
            }
            
            // Feature highlights section
            item {
                FeatureHighlightSection(
                    onFeatureClick = onFeatureClick,
                    modifier = Modifier.padding(vertical = SparkThreadDesign.Spacing.Large)
                )
            }
            
            // Traditional stamp decoration
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SparkThreadDesign.Spacing.Huge),
                    contentAlignment = Alignment.Center
                ) {
                    TraditionalStamp(
                        text = "智",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
            
            // Welcome message section
            item {
                WelcomeMessageSection()
            }
            
            // Bottom spacing for navigation
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
            }
        }
        
        // Floating decorative elements
        FloatingDecorations()
    }
}

/**
 * Animated background with traditional paper textures and parallax effect
 */
@Composable
private fun LandingPageBackground(
    parallaxOffset: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = parallaxOffset
            }
    ) {
        // Primary gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SparkTheme.colorScheme.paperTexture
                )
        )
        
        // Washi texture overlay with animation
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            WashiPaperTexture()
        }
        
        // Subtle gradient overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SparkThreadDesign.PaperEffects.WashiTexture.copy(alpha = 0.3f),
                            Color.Transparent,
                            SparkThreadDesign.PaperEffects.WashiTexture.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * Floating decorative elements that move independently
 */
@Composable
private fun FloatingDecorations() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Floating sakura petals
        FloatingSakuraPetals(
            count = 6,
            modifier = Modifier.fillMaxSize()
        )
        
        // Ink splash decorations
        InkSplashDecorations(
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Welcome message section with elegant typography
 */
@Composable
private fun WelcomeMessageSection() {
    var isVisible by remember { mutableStateOf(false) }
    
    val fadeInAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "welcomeFadeIn"
    )
    
    LaunchedEffect(Unit) {
        delay(500)
        isVisible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = responsiveContentPadding())
            .graphicsLayer { alpha = fadeInAnimation },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Traditional separator
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(3.dp)
                .background(
                    brush = SparkTheme.colorScheme.goldGradient,
                    shape = SparkThreadDesign.Shapes.Small
                )
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
        
        // Welcome title
        Text(
            text = "Welcome to Your Knowledge Garden",
            style = SparkThreadDesign.Typography.HeadlineMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                letterSpacing = 1.sp
            ),
            color = SparkThreadDesign.Colors.Primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
        
        // Descriptive text
        Text(
            text = "Cultivate your digital knowledge with the mindfulness and beauty of traditional Japanese stationery. Every bookmark, every collection, every search is designed to bring you closer to understanding and wisdom.",
            style = SparkThreadDesign.Typography.BodyLarge.copy(
                lineHeight = 26.sp
            ),
            color = SparkThreadDesign.Colors.MutedForeground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Large))
        
        // Japanese quote
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = SparkThreadDesign.Shapes.Medium,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = SparkTheme.colorScheme.washiGradient,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
                    .padding(SparkThreadDesign.Spacing.Large)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\"知識は力なり\"",
                        style = SparkThreadDesign.Typography.TitleLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                            letterSpacing = 2.sp
                        ),
                        color = SparkTheme.colorScheme.sakura,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                    
                    Text(
                        text = "Knowledge is power",
                        style = SparkThreadDesign.Typography.BodyMedium,
                        color = SparkThreadDesign.Colors.MutedForeground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}