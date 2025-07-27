package com.secondbrain.app.ui.theme.spark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.secondbrain.app.design.SparkThreadDesign

/**
 * Modern top app bar with glass morphism effect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SparkTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = SparkTheme.colorScheme.onBackground,
        navigationIconContentColor = SparkTheme.colorScheme.onBackground,
        actionIconContentColor = SparkTheme.colorScheme.onBackground
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    Box {
        // Glass morphism background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SparkThreadDesign.Colors.Background,
                            SparkThreadDesign.Colors.Background.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        // Top app bar content
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}

/**
 * Large top app bar with modern design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SparkLargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = SparkTheme.colorScheme.onBackground,
        navigationIconContentColor = SparkTheme.colorScheme.onBackground,
        actionIconContentColor = SparkTheme.colorScheme.onBackground
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    Box {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SparkThreadDesign.Colors.Primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Large top app bar content
        LargeTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}

/**
 * Center-aligned top app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SparkCenterAlignedTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = SparkTheme.colorScheme.onBackground,
        navigationIconContentColor = SparkTheme.colorScheme.onBackground,
        actionIconContentColor = SparkTheme.colorScheme.onBackground
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    Box {
        // Glass morphism background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SparkThreadDesign.Colors.Background,
                            SparkThreadDesign.Colors.Background.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        // Center-aligned top app bar content
        CenterAlignedTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}