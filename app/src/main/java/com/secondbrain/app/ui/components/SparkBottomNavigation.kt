package com.secondbrain.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkTheme

/**
 * Bottom navigation component matching TwitterSidebar from Spark Thread UI
 * Features backdrop blur, active state animations, and traditional Japanese design
 */
@Composable
fun SparkBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = SparkThreadDesign.Colors.BackgroundGradient,
                alpha = 0.95f
            )
            .padding(
                horizontal = SparkThreadDesign.Spacing.Medium,
                vertical = SparkThreadDesign.Spacing.Small
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            SparkNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            
            // Search Tab  
            SparkNavItem(
                icon = Icons.Default.Search,
                label = "Search",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            
            // Collections Tab
            SparkNavItem(
                icon = Icons.Default.Star,
                label = "Collections",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            
            // Favorites Tab
            SparkNavItem(
                icon = Icons.Default.Favorite,
                label = "Favorites", 
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
            
            // Settings Tab
            SparkNavItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

/**
 * Individual navigation item with Spark Thread styling
 * Includes scale animation, color transitions, and pulse effects
 */
@Composable
private fun SparkNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.6f,
        animationSpec = tween(durationMillis = 200),
        label = "alpha"
    )
    
    // Optimized pulse effect for selected state
    val pulseScale by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.0f,
        animationSpec = if (isSelected) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else {
            tween(durationMillis = 150)
        },
        label = "pulse"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(SparkThreadDesign.Spacing.Small),
        contentAlignment = Alignment.Center
    ) {
        // Enhanced background highlight for selected state
        if (isSelected) {
            // Primary gradient background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = SparkTheme.colorScheme.sakuraGradient,
                        shape = CircleShape
                    )
            )
            
            // Ink shadow overlay for depth
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = SparkThreadDesign.PaperEffects.InkShadow.copy(alpha = 0.1f + pulseScale * 0.05f),
                        shape = CircleShape
                    )
            )
            
            // Seal imprint effect for authentic traditional feel
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = SparkThreadDesign.PaperEffects.SealImprint.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) {
                    SparkThreadDesign.Colors.Primary
                } else {
                    SparkThreadDesign.Colors.MutedForeground
                }.copy(alpha = alpha),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = label,
                style = SparkThreadDesign.Typography.LabelSmall,
                color = if (isSelected) {
                    SparkThreadDesign.Colors.Primary
                } else {
                    SparkThreadDesign.Colors.MutedForeground
                }.copy(alpha = alpha),
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Enhanced bottom navigation with traditional Japanese paper effects
 */
@Composable
fun SparkBottomNavigationWithEffects(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = SparkThreadDesign.Elevation.Card
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = SparkTheme.colorScheme.washiGradient,
                    alpha = 0.98f
                )
                .padding(
                    top = SparkThreadDesign.Spacing.Small,
                    bottom = SparkThreadDesign.Spacing.Medium,
                    start = SparkThreadDesign.Spacing.Medium,
                    end = SparkThreadDesign.Spacing.Medium
                )
        ) {
            // Enhanced paper texture layers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = SparkTheme.colorScheme.cardGradient,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
            )
            
            // Paper grain effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = SparkThreadDesign.PaperEffects.PaperGrain,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
            )
            
            // Washi texture overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = SparkThreadDesign.PaperEffects.WashiTexture,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
            )
            
            // Subtle gold accent for premium feel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SparkTheme.colorScheme.gold.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        shape = SparkThreadDesign.Shapes.Medium
                    )
            )
            
            SparkBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    }
}