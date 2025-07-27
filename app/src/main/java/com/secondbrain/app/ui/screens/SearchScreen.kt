package com.secondbrain.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    bookmarks: List<Bookmark>,
    onBookmarkClick: (Bookmark) -> Unit = {},
    onFavoriteClick: (Bookmark) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredBookmarks by remember { mutableStateOf<List<Bookmark>>(emptyList()) }
    
    // Filter bookmarks based on search query
    LaunchedEffect(searchQuery, bookmarks) {
        filteredBookmarks = if (searchQuery.isBlank()) {
            emptyList()
        } else {
            bookmarks.filter { bookmark ->
                bookmark.title.contains(searchQuery, ignoreCase = true) ||
                bookmark.description?.contains(searchQuery, ignoreCase = true) == true ||
                bookmark.url.contains(searchQuery, ignoreCase = true) ||
                bookmark.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
            }
        }
    }
    
    // Enhanced SearchScreen with Japanese paper aesthetics
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient with washi paper texture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = SparkTheme.colorScheme.washiGradient
                )
        )
        
        // Paper texture overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = SparkThreadDesign.PaperEffects.WashiTexture
                )
        )
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced search bar with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SparkThreadDesign.Spacing.Medium)
            ) {
                // Search bar background gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = SparkTheme.colorScheme.cardGradient,
                            shape = SparkThreadDesign.Shapes.Medium
                        )
                )
                
                // Paper texture for search bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = SparkThreadDesign.PaperEffects.PaperGrain,
                            shape = SparkThreadDesign.Shapes.Medium
                        )
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = SparkThreadDesign.Elevation.Card
                    ),
                    shape = SparkThreadDesign.Shapes.Medium
                ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { 
                    Text(
                        "Search bookmarks...",
                        style = SparkTheme.typography.bodyMedium,
                        color = SparkTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SparkTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = SparkTheme.colorScheme.outline
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = SparkTheme.colorScheme.sakura,
                    unfocusedIndicatorColor = SparkTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
                }
            }
        
            // Enhanced search results section
            if (searchQuery.isNotEmpty()) {
                if (filteredBookmarks.isNotEmpty()) {
                    // Results count with elegant styling
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = SparkThreadDesign.Spacing.Medium,
                                vertical = SparkThreadDesign.Spacing.Small
                            )
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        SparkTheme.colorScheme.sakura.copy(alpha = 0.1f),
                                        Color.Transparent,
                                        SparkTheme.colorScheme.gold.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = SparkThreadDesign.Shapes.Small
                            )
                    ) {
                        Text(
                            text = "${filteredBookmarks.size} results found",
                            style = SparkTheme.typography.labelMedium,
                            color = SparkTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(SparkThreadDesign.Spacing.Small)
                        )
                    }
                
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = SparkThreadDesign.Spacing.Medium,
                            vertical = SparkThreadDesign.Spacing.Small
                        ),
                        verticalArrangement = Arrangement.spacedBy(SparkThreadDesign.Spacing.Small)
                    ) {
                        items(filteredBookmarks) { bookmark ->
                            BookmarkCard(
                                bookmark = bookmark,
                                onBookmarkClick = onBookmarkClick,
                                onFavoriteClick = onFavoriteClick
                            )
                        }
                    }
                } else {
                    // No results found
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = SparkTheme.colorScheme.outline,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
                            Text(
                                text = "No bookmarks found",
                                style = SparkTheme.typography.titleMedium,
                                color = SparkTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Try searching with different keywords",
                                style = SparkTheme.typography.bodyMedium,
                                color = SparkTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                // Enhanced empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = SparkTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Medium))
                        Text(
                            text = "Search your bookmarks",
                            style = SparkTheme.typography.titleMedium,
                            color = SparkTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Start typing to find bookmarks by title, URL, or tags",
                            style = SparkTheme.typography.bodyMedium,
                            color = SparkTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

// Reuse BookmarkCard from MainActivity for consistency
@Composable
private fun BookmarkCard(
    bookmark: Bookmark,
    onBookmarkClick: (Bookmark) -> Unit = {},
    onFavoriteClick: (Bookmark) -> Unit = {}
) {
    // This would ideally be imported from the main activity or extracted to a shared component
    // For now, we'll create a simplified version
    Card(
        onClick = { onBookmarkClick(bookmark) },
        colors = CardDefaults.cardColors(
            containerColor = SparkTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = SparkThreadDesign.Elevation.Card
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(SparkThreadDesign.Spacing.Medium)
        ) {
            Text(
                text = bookmark.title,
                style = SparkTheme.typography.titleMedium,
                color = SparkTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.ExtraSmall))
            
            Text(
                text = bookmark.computedDomain,
                style = SparkTheme.typography.bodySmall,
                color = SparkTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )
            
            bookmark.description?.let { description ->
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.ExtraSmall))
                Text(
                    text = description,
                    style = SparkTheme.typography.bodyMedium,
                    color = SparkTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (bookmark.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SparkThreadDesign.Spacing.Small))
                Text(
                    text = bookmark.tags.joinToString(" • "),
                    style = SparkTheme.typography.labelSmall,
                    color = SparkTheme.colorScheme.tertiary
                )
            }
        }
    }
}