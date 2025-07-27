package com.secondbrain.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkCard
import com.secondbrain.app.ui.theme.spark.SparkTheme
import com.secondbrain.app.ui.components.SparkBottomNavigationWithEffects
import com.secondbrain.app.ui.components.FeatureType
import com.secondbrain.app.ui.components.OnboardingFlow
import com.secondbrain.app.ui.components.ResponsiveBookmarkGrid
import com.secondbrain.app.ui.components.ResponsiveCollectionGrid
import com.secondbrain.app.ui.components.ResponsiveContentView
import com.secondbrain.app.ui.components.ResponsiveContentWrapper
import com.secondbrain.app.ui.components.ResponsiveScaffold
import com.secondbrain.app.ui.components.ViewMode
import com.secondbrain.app.ui.screens.LandingPageScreen
import com.secondbrain.app.ui.screens.SearchScreen
import com.secondbrain.app.ui.utils.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            SparkTheme {
                val app = application as SecondBrainApplication
                SecondBrainApp(app)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SecondBrainApp(app: SecondBrainApplication) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Navigation state
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Landing, 1: Bookmarks, 2: Collections, 3: Search
    var showOnboarding by remember { mutableStateOf(false) } // Set to true for new users
    var isFirstLaunch by remember { mutableStateOf(true) }
    
    // Data state using repository flows
    val bookmarks by app.bookmarkRepository.getAllBookmarks().collectAsState(initial = emptyList())
    val collections by app.collectionRepository.getAllCollections().collectAsState(initial = emptyList())
    var isLoading by remember { mutableStateOf(true) }
    
    // Initialize with sample data if database is empty
    LaunchedEffect(bookmarks, collections) {
        try {
            // Initialize with sample data if database is empty
            if (collections.isEmpty() && bookmarks.isEmpty()) {
                val sampleCollections = Collection.getSampleCollections()
                sampleCollections.forEach { collection ->
                    app.collectionRepository.insertCollection(collection)
                }
                
                val sampleBookmarks = Bookmark.getSampleBookmarks()
                sampleBookmarks.forEach { bookmark ->
                    app.bookmarkRepository.insertBookmark(bookmark)
                }
            }
            
            isLoading = false
        } catch (e: Exception) {
            // Handle error gracefully
            isLoading = false
        }
    }
    
    // Bookmark actions
    fun onBookmarkClick(bookmark: Bookmark) {
        // Open the bookmark URL in a browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.url))
        context.startActivity(intent)
    }
    
    fun onBookmarkFavorite(bookmark: Bookmark) {
        coroutineScope.launch {
            val updatedBookmark = bookmark.copy(isFavorite = !bookmark.isFavorite)
            app.bookmarkRepository.updateBookmark(updatedBookmark)
        }
    }
    
    // Collection actions
    fun onCollectionClick(collection: Collection) {
        // Navigate to collection details
        // TODO: Implement collection details screen
    }
    
    // Navigation functions
    fun onGetStartedClick() {
        selectedTab = 1 // Navigate to bookmarks
    }
    
    fun onFeatureClick(feature: FeatureType) {
        when (feature) {
            FeatureType.SMART_BOOKMARKS -> selectedTab = 1
            FeatureType.COLLECTIONS -> selectedTab = 2
            FeatureType.VISUAL_SEARCH -> selectedTab = 3
            FeatureType.MINDFUL_READING -> selectedTab = 1
        }
    }
    
    fun onQuickActionClick(action: String) {
        when (action) {
            "add_bookmark" -> {
                selectedTab = 1
                // TODO: Show add bookmark dialog
            }
            "search" -> selectedTab = 3
            "collections" -> selectedTab = 2
            "favorites" -> selectedTab = 1
        }
    }
    
    // Show onboarding for first launch
    if (showOnboarding) {
        OnboardingFlow(
            onComplete = { 
                showOnboarding = false
                selectedTab = 0 // Show landing page after onboarding
            }
        )
        return
    }
    
    Scaffold(
        topBar = {
            // Enhanced TopAppBar with gradient background
            Surface(
                color = Color.Transparent,
                shadowElevation = SparkThreadDesign.Elevation.Card
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = SparkTheme.colorScheme.inkGradient
                        )
                ) {
                    // Paper texture overlay for authenticity
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = SparkThreadDesign.PaperEffects.InkShadow
                            )
                    )
                    
                    TopAppBar(
                        title = { 
                            Text(
                                "Second Brain",
                                style = SparkTheme.typography.titleLarge,
                                color = SparkTheme.colorScheme.onPrimary
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = SparkTheme.colorScheme.onPrimary
                        ),
                        actions = {
                            IconButton(onClick = { /* TODO: Open settings */ }) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = SparkTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            SparkBottomNavigationWithEffects(
                selectedTab = when (selectedTab) {
                    0 -> 0 // Landing -> Home
                    1 -> 0 // Bookmarks -> Home (they're the same for now)
                    2 -> 2 // Collections -> Collections  
                    3 -> 1 // Search -> Search
                    else -> 0
                },
                onTabSelected = { tabIndex ->
                    selectedTab = when (tabIndex) {
                        0 -> 0 // Home -> Landing
                        1 -> 3 // Search -> Search
                        2 -> 2 // Collections -> Collections
                        3 -> 1 // Favorites -> Bookmarks
                        4 -> 0 // Settings -> Landing (for now)
                        else -> 0
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab < 2) {
                // Enhanced FAB with gradient background and paper texture
                Box {
                    FloatingActionButton(
                        onClick = {
                            // TODO: Show add dialog
                        },
                        containerColor = Color.Transparent,
                        contentColor = SparkTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .background(
                                brush = SparkTheme.colorScheme.sakuraGradient,
                                shape = SparkThreadDesign.Shapes.Stamp
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = SparkThreadDesign.PaperEffects.SealImprint,
                                    shape = SparkThreadDesign.Shapes.Stamp
                                )
                                .size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = "Add",
                                tint = SparkTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        // Enhanced content area with subtle gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Subtle gradient overlay for content area depth
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SparkThreadDesign.PaperEffects.WashiTexture.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            when (selectedTab) {
                0 -> LandingPageScreen(
                    onGetStartedClick = ::onGetStartedClick,
                    onFeatureClick = ::onFeatureClick,
                    onAddBookmarkClick = { onQuickActionClick("add_bookmark") },
                    onSearchClick = { onQuickActionClick("search") },
                    onCollectionsClick = { onQuickActionClick("collections") },
                    onFavoritesClick = { onQuickActionClick("favorites") }
                )
                1 -> ResponsiveContentWrapper {
                    ResponsiveBookmarksScreen(
                        bookmarks = bookmarks,
                        isLoading = isLoading,
                        onBookmarkClick = ::onBookmarkClick,
                        onFavoriteClick = ::onBookmarkFavorite
                    )
                }
                2 -> ResponsiveContentWrapper {
                    ResponsiveCollectionsScreen(
                        collections = collections,
                        bookmarks = bookmarks,
                        isLoading = isLoading,
                        onCollectionClick = ::onCollectionClick
                    )
                }
                3 -> SearchScreen(
                    bookmarks = bookmarks,
                    onBookmarkClick = ::onBookmarkClick,
                    onFavoriteClick = ::onBookmarkFavorite,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun BookmarksScreen(
    bookmarks: List<Bookmark>,
    isLoading: Boolean,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SparkTheme.colorScheme.primary)
        }
    } else if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No bookmarks yet. Tap + to add some!")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                vertical = SparkThreadDesign.Spacing.Medium,
                horizontal = SparkThreadDesign.Spacing.Small
            ),
            verticalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium)
        ) {
            items(
                items = bookmarks,
                key = { bookmark -> bookmark.id } // Optimize recomposition with stable keys
            ) { bookmark ->
                BookmarkCard(
                    bookmark = bookmark,
                    onBookmarkClick = onBookmarkClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Composable
fun CollectionsScreen(
    collections: List<Collection>,
    bookmarks: List<Bookmark>,
    isLoading: Boolean,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SparkTheme.colorScheme.primary)
        }
    } else if (collections.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No collections yet. Tap + to add some!")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(collections) { collection ->
                val bookmarkCount = bookmarks.count { it.collectionId == collection.id }
                CollectionCard(
                    collection = collection,
                    bookmarkCount = bookmarkCount,
                    onCollectionClick = onCollectionClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkCard(
    bookmark: Bookmark,
    onBookmarkClick: (Bookmark) -> Unit = {},
    onFavoriteClick: (Bookmark) -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    
    SparkCard(
        onClick = { onBookmarkClick(bookmark) },
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onBookmarkClick(bookmark) },
                onLongClick = { /* TODO: Show context menu */ }
            ),
        shadowElevation = if (isPressed) SparkThreadDesign.Elevation.Paper else SparkThreadDesign.Elevation.Card,
        containerColor = SparkThreadDesign.Colors.Card,
        shape = SparkThreadDesign.Shapes.Medium
    ) {
        Box {
            // Enhanced paper texture background for bookmark cards
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = SparkTheme.colorScheme.washiGradient,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
            )
            
            // Subtle gold accent for premium feel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SparkThreadDesign.PaperEffects.WashiTexture,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
            )
            
            Column(
                modifier = Modifier
                    .padding(SparkThreadDesign.Spacing.Large)
                    .background(
                        brush = SparkThreadDesign.Colors.CardGradient,
                        shape = SparkThreadDesign.Shapes.Medium
                    )
                    .padding(SparkThreadDesign.Spacing.Medium)
            ) {
            // Header row with favicon and metadata - Tweet-style layout
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Favicon avatar - circular like Twitter profile
                Box(
                    modifier = Modifier
                        .size(48.dp) // Twitter-like avatar size
                        .background(
                            brush = SparkThreadDesign.Colors.WashiGradient,
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .background(
                            color = SparkThreadDesign.Colors.Background,
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = SparkTheme.colorScheme.inkGradient,
                                shape = CircleShape
                            )
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bookmark.computedDomain.take(2).uppercase().ifEmpty { 
                                bookmark.title.take(2).uppercase() 
                            },
                            style = SparkThreadDesign.Typography.TitleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = SparkTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(SparkThreadDesign.Spacing.Medium))
                
                // Title and metadata column - Tweet content area
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Title row with domain and timestamp info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = bookmark.computedDomain.ifEmpty { "bookmark" },
                            style = SparkThreadDesign.Typography.TitleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = SparkThreadDesign.Colors.Foreground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        Spacer(modifier = Modifier.width(SparkThreadDesign.Spacing.Small))
                        
                        // Domain handle-style text
                        Text(
                            text = "• ${bookmark.openCount} views",
                            style = SparkThreadDesign.Typography.BodySmall,
                            color = SparkThreadDesign.Colors.MutedForeground,
                            maxLines = 1
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                    
                    // Main bookmark title - like tweet content
                    Text(
                        text = bookmark.title,
                        style = SparkThreadDesign.Typography.BodyLarge.copy(
                            lineHeight = 22.sp
                        ),
                        color = SparkThreadDesign.Colors.Foreground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Description if available
                    bookmark.description?.let { description ->
                        if (description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                            Text(
                                text = description,
                                style = SparkThreadDesign.Typography.BodyMedium,
                                color = SparkThreadDesign.Colors.MutedForeground,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    // URL preview - styled like tweet link preview
                    Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SparkThreadDesign.Colors.Muted.copy(alpha = 0.3f),
                        shape = SparkThreadDesign.Shapes.Small
                    ) {
                        Text(
                            text = bookmark.url,
                            style = SparkThreadDesign.Typography.BodySmall,
                            color = SparkThreadDesign.Colors.Primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(
                                horizontal = SparkThreadDesign.Spacing.Medium,
                                vertical = SparkThreadDesign.Spacing.Small
                            )
                        )
                    }
                }
            }
            
            // Tags row - like tweet hashtags
            if (bookmark.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Small),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    bookmark.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = SparkTheme.colorScheme.sakuraGradient,
                                    shape = SparkThreadDesign.Shapes.Small
                                )
                                .clip(SparkThreadDesign.Shapes.Small)
                                .clickable { /* TODO: Filter by tag */ }
                        ) {
                            // Ink shadow overlay for text depth
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = SparkThreadDesign.PaperEffects.InkShadow.copy(alpha = 0.05f),
                                        shape = SparkThreadDesign.Shapes.Small
                                    )
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = SparkThreadDesign.Typography.LabelMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = SparkThreadDesign.Colors.Primary,
                                    modifier = Modifier.padding(
                                        horizontal = SparkThreadDesign.Spacing.Medium,
                                        vertical = SparkThreadDesign.Spacing.Small
                                    )
                                )
                            }
                        }
                    }
                    if (bookmark.tags.size > 3) {
                        Text(
                            text = "+${bookmark.tags.size - 3}",
                            style = SparkThreadDesign.Typography.LabelSmall,
                            color = SparkThreadDesign.Colors.MutedForeground,
                            modifier = Modifier.padding(
                                horizontal = SparkThreadDesign.Spacing.Small,
                                vertical = SparkThreadDesign.Spacing.Small
                            )
                        )
                    }
                }
            }
            
            // Action buttons row - Twitter-style bottom actions
            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Left side - read time estimate or creation date
                Text(
                    text = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                        .format(bookmark.createdAt),
                    style = SparkThreadDesign.Typography.LabelSmall,
                    color = SparkThreadDesign.Colors.MutedForeground
                )
                
                // Right side - action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Enhanced favorite button with gradient background
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                brush = if (bookmark.isFavorite) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            SparkThreadDesign.Colors.SealRed.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            SparkTheme.colorScheme.sakura.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                },
                                shape = CircleShape
                            )
                            .clickable { onFavoriteClick(bookmark) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (bookmark.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (bookmark.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (bookmark.isFavorite) SparkThreadDesign.Colors.SealRed else SparkThreadDesign.Colors.MutedForeground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Enhanced share button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        SparkTheme.colorScheme.gold.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                            .clickable { /* TODO: Share bookmark */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share bookmark",
                            tint = SparkThreadDesign.Colors.MutedForeground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Enhanced more options button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        SparkTheme.colorScheme.ink.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                            .clickable { /* TODO: Show more options */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = SparkThreadDesign.Colors.MutedForeground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
fun CollectionCard(
    collection: Collection,
    bookmarkCount: Int,
    onCollectionClick: (Collection) -> Unit = {}
) {
    SparkCard(
        onClick = { onCollectionClick(collection) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Enhanced collection icon with gradient background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = SparkTheme.colorScheme.goldGradient,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Ink shadow overlay for depth
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = SparkThreadDesign.PaperEffects.InkShadow.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
                
                // Seal imprint effect
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = SparkThreadDesign.PaperEffects.SealImprint.copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                )
                
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = SparkTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Collection info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = collection.name,
                    style = SparkTheme.typography.titleMedium,
                    color = SparkTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "$bookmarkCount bookmarks",
                    style = SparkTheme.typography.bodySmall,
                    color = SparkTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Shared indicator if applicable
            if (collection.isShared) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Shared collection",
                    tint = SparkTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Responsive screen implementations

@Composable
fun ResponsiveBookmarksScreen(
    bookmarks: List<Bookmark>,
    isLoading: Boolean,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = getAdaptiveSpacing()
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SparkTheme.colorScheme.primary)
        }
    } else if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = SparkThreadDesign.Colors.MutedForeground
                )
                Text(
                    "No bookmarks yet",
                    style = SparkTheme.typography.titleMedium,
                    color = SparkThreadDesign.Colors.Foreground
                )
                Text(
                    "Tap + to add your first bookmark!",
                    style = SparkTheme.typography.bodyMedium,
                    color = SparkThreadDesign.Colors.MutedForeground
                )
            }
        }
    } else {
        ResponsiveBookmarkGrid(
            bookmarks = bookmarks,
            onBookmarkClick = onBookmarkClick,
            onFavoriteClick = onFavoriteClick,
            modifier = modifier
        )
    }
}

@Composable
fun ResponsiveCollectionsScreen(
    collections: List<Collection>,
    bookmarks: List<Bookmark>,
    isLoading: Boolean,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = getAdaptiveSpacing()
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SparkTheme.colorScheme.primary)
        }
    } else if (collections.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = SparkThreadDesign.Colors.MutedForeground
                )
                Text(
                    "No collections yet",
                    style = SparkTheme.typography.titleMedium,
                    color = SparkThreadDesign.Colors.Foreground
                )
                Text(
                    "Create collections to organize your bookmarks",
                    style = SparkTheme.typography.bodyMedium,
                    color = SparkThreadDesign.Colors.MutedForeground
                )
            }
        }
    } else {
        ResponsiveCollectionGrid(
            collections = collections,
            bookmarks = bookmarks,
            onCollectionClick = onCollectionClick,
            modifier = modifier
        )
    }
}

