package com.secondbrain.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.ui.utils.*

/**
 * Responsive grid layouts for Second Brain Android app
 * Implements adaptive layouts with Japanese stationery aesthetics
 */

/**
 * Responsive bookmark grid that adapts to screen size
 */
@Composable
fun ResponsiveBookmarkGrid(
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    modifier: Modifier = Modifier,
    forceGridLayout: Boolean = false
) {
    val screenSize = getScreenSize()
    val spacing = getAdaptiveSpacing()
    val columns = getAdaptiveColumns(ContentType.BookmarkCard)
    
    // Use grid layout for larger screens or when explicitly forced
    val useGridLayout = forceGridLayout || screenSize != ScreenSize.Compact
    
    if (useGridLayout && bookmarks.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.contentPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            items(
                items = bookmarks,
                key = { bookmark -> bookmark.id }
            ) { bookmark ->
                ResponsiveBookmarkCard(
                    bookmark = bookmark,
                    onBookmarkClick = onBookmarkClick,
                    onFavoriteClick = onFavoriteClick,
                    isGridLayout = true
                )
            }
        }
    } else {
        // Fallback to list layout for compact screens
        ResponsiveBookmarkList(
            bookmarks = bookmarks,
            onBookmarkClick = onBookmarkClick,
            onFavoriteClick = onFavoriteClick,
            modifier = modifier
        )
    }
}

/**
 * Responsive bookmark list for compact screens
 */
@Composable
fun ResponsiveBookmarkList(
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = getAdaptiveSpacing()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.contentPadding),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        items(
            items = bookmarks,
            key = { bookmark -> bookmark.id }
        ) { bookmark ->
            ResponsiveBookmarkCard(
                bookmark = bookmark,
                onBookmarkClick = onBookmarkClick,
                onFavoriteClick = onFavoriteClick,
                isGridLayout = false
            )
        }
    }
}

/**
 * Responsive collection grid with adaptive columns
 */
@Composable
fun ResponsiveCollectionGrid(
    collections: List<Collection>,
    bookmarks: List<Bookmark>,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = getAdaptiveSpacing()
    val columns = getAdaptiveColumns(ContentType.CollectionCard)
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.contentPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        items(
            items = collections,
            key = { collection -> collection.id }
        ) { collection ->
            val bookmarkCount = bookmarks.count { it.collectionId == collection.id }
            ResponsiveCollectionCard(
                collection = collection,
                bookmarkCount = bookmarkCount,
                onCollectionClick = onCollectionClick
            )
        }
    }
}

/**
 * Staggered grid for mixed content types
 */
@Composable
fun ResponsiveStaggeredGrid(
    bookmarks: List<Bookmark>,
    collections: List<Collection>,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = getAdaptiveSpacing()
    val columns = getAdaptiveColumns()
    
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.contentPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalItemSpacing = spacing.medium
    ) {
        // Collections first (more compact)
        items(
            items = collections,
            key = { collection -> "collection_${collection.id}" }
        ) { collection ->
            val bookmarkCount = bookmarks.count { it.collectionId == collection.id }
            ResponsiveCollectionCard(
                collection = collection,
                bookmarkCount = bookmarkCount,
                onCollectionClick = onCollectionClick,
                modifier = Modifier.height(120.dp) // Fixed height for staggered grid
            )
        }
        
        // Bookmarks after collections
        items(
            items = bookmarks,
            key = { bookmark -> "bookmark_${bookmark.id}" }
        ) { bookmark ->
            ResponsiveBookmarkCard(
                bookmark = bookmark,
                onBookmarkClick = onBookmarkClick,
                onFavoriteClick = onFavoriteClick,
                isGridLayout = true
            )
        }
    }
}

/**
 * Responsive layout switcher with view mode options
 */
@Composable
fun ResponsiveContentView(
    bookmarks: List<Bookmark>,
    collections: List<Collection>,
    viewMode: ViewMode,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.BookmarkList -> {
            ResponsiveBookmarkList(
                bookmarks = bookmarks,
                onBookmarkClick = onBookmarkClick,
                onFavoriteClick = onFavoriteClick,
                modifier = modifier
            )
        }
        ViewMode.BookmarkGrid -> {
            ResponsiveBookmarkGrid(
                bookmarks = bookmarks,
                onBookmarkClick = onBookmarkClick,
                onFavoriteClick = onFavoriteClick,
                modifier = modifier,
                forceGridLayout = true
            )
        }
        ViewMode.CollectionGrid -> {
            ResponsiveCollectionGrid(
                collections = collections,
                bookmarks = bookmarks,
                onCollectionClick = onCollectionClick,
                modifier = modifier
            )
        }
        ViewMode.MixedStaggered -> {
            ResponsiveStaggeredGrid(
                bookmarks = bookmarks,
                collections = collections,
                onBookmarkClick = onBookmarkClick,
                onFavoriteClick = onFavoriteClick,
                onCollectionClick = onCollectionClick,
                modifier = modifier
            )
        }
    }
}

/**
 * View mode options for responsive layouts
 */
enum class ViewMode {
    BookmarkList,    // Traditional list view
    BookmarkGrid,    // Grid view for bookmarks
    CollectionGrid,  // Grid view for collections
    MixedStaggered   // Staggered grid with mixed content
}

/**
 * Content wrapper with maximum width constraints for large screens
 */
@Composable
fun ResponsiveContentWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val maxWidth = getMaxContentWidth()
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (maxWidth != androidx.compose.ui.unit.Dp.Unspecified) {
            // Large screen: center content with max width
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = maxWidth)
                        .fillMaxHeight()
                ) {
                    content()
                }
            }
        } else {
            // Small screen: full width
            content()
        }
    }
}

/**
 * Adaptive scaffold that uses side navigation for large screens
 */
@Composable
fun ResponsiveScaffold(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val useSideNav = shouldUseSideNavigation()
    val spacing = getAdaptiveSpacing()
    
    if (useSideNav) {
        // Large screen: side navigation
        Row(modifier = Modifier.fillMaxSize()) {
            // Side navigation
            ResponsiveSideNavigation(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.width(240.dp)
            )
            
            // Main content
            androidx.compose.material3.Scaffold(
                topBar = topBar,
                floatingActionButton = floatingActionButton,
                modifier = Modifier.weight(1f)
            ) { paddingValues ->
                ResponsiveContentWrapper {
                    content(paddingValues)
                }
            }
        }
    } else {
        // Small screen: bottom navigation
        androidx.compose.material3.Scaffold(
            topBar = topBar,
            bottomBar = {
                SparkBottomNavigationWithEffects(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected
                )
            },
            floatingActionButton = floatingActionButton
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

/**
 * Side navigation for large screens
 */
@Composable
private fun ResponsiveSideNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // This would be implemented with NavigationRail or custom side nav
    // For now, using a placeholder
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // Side navigation items would go here
        // This is a simplified implementation
    }
}