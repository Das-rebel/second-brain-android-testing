package com.secondbrain.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkCard
import com.secondbrain.app.ui.theme.spark.SparkTheme
import com.secondbrain.app.ui.utils.*

/**
 * Responsive card components for Second Brain Android app
 * Adapts to different screen sizes with Japanese stationery aesthetics
 */

/**
 * Responsive bookmark card that adapts to grid or list layout
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResponsiveBookmarkCard(
    bookmark: Bookmark,
    onBookmarkClick: (Bookmark) -> Unit,
    onFavoriteClick: (Bookmark) -> Unit,
    modifier: Modifier = Modifier,
    isGridLayout: Boolean = false
) {
    val spacing = getAdaptiveSpacing()
    val typography = getAdaptiveTypography()
    val cardSize = if (isGridLayout) getAdaptiveCardSize(ContentType.BookmarkCard) else null
    
    SparkCard(
        onClick = { onBookmarkClick(bookmark) },
        modifier = modifier
            .then(if (cardSize != null) Modifier.width(cardSize) else Modifier.fillMaxWidth())
            .combinedClickable(
                onClick = { onBookmarkClick(bookmark) },
                onLongClick = { /* TODO: Show context menu */ }
            ),
        containerColor = SparkThreadDesign.Colors.Card,
        shape = SparkThreadDesign.Shapes.Medium,
        shadowElevation = SparkThreadDesign.Elevation.Card
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            // Header: Avatar + Domain + Metadata
            ResponsiveBookmarkHeader(
                bookmark = bookmark,
                typography = typography,
                spacing = spacing,
                isGridLayout = isGridLayout
            )
            
            // Content: Title + Description
            ResponsiveBookmarkContent(
                bookmark = bookmark,
                typography = typography,
                spacing = spacing,
                isGridLayout = isGridLayout
            )
            
            // URL Preview (only in grid layout or larger screens)
            if (isGridLayout || getScreenSize() != ScreenSize.Compact) {
                ResponsiveBookmarkUrlPreview(
                    bookmark = bookmark,
                    spacing = spacing
                )
            }
            
            // Tags (limited in grid layout)
            if (bookmark.tags.isNotEmpty()) {
                ResponsiveBookmarkTags(
                    tags = bookmark.tags,
                    spacing = spacing,
                    maxTags = if (isGridLayout) 2 else 3
                )
            }
            
            // Actions: Favorite + Share + More
            ResponsiveBookmarkActions(
                bookmark = bookmark,
                onFavoriteClick = onFavoriteClick,
                spacing = spacing
            )
        }
    }
}

/**
 * Bookmark card header with avatar and metadata
 */
@Composable
private fun ResponsiveBookmarkHeader(
    bookmark: Bookmark,
    typography: AdaptiveTypography,
    spacing: AdaptiveSpacing,
    isGridLayout: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar (domain favicon or initials)
        Box(
            modifier = Modifier
                .size(if (isGridLayout) 40.dp else 48.dp)
                .background(
                    brush = SparkThreadDesign.Colors.WashiGradient,
                    shape = CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bookmark.computedDomain.take(1).uppercase(),
                style = SparkTheme.typography.titleMedium.copy(
                    fontSize = (SparkTheme.typography.titleMedium.fontSize.value * typography.titleScale).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SparkThreadDesign.Colors.Primary
            )
        }
        
        Spacer(modifier = Modifier.width(spacing.medium))
        
        // Domain + metadata
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = bookmark.computedDomain,
                style = SparkTheme.typography.titleSmall.copy(
                    fontSize = (SparkTheme.typography.titleSmall.fontSize.value * typography.titleScale).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = SparkThreadDesign.Colors.Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (!isGridLayout) {
                Text(
                    text = "${bookmark.openCount} views",
                    style = SparkTheme.typography.bodySmall.copy(
                        fontSize = (SparkTheme.typography.bodySmall.fontSize.value * typography.bodyScale).sp
                    ),
                    color = SparkThreadDesign.Colors.MutedForeground
                )
            }
        }
        
        // Timestamp (only in list layout)
        if (!isGridLayout) {
            Text(
                text = "2h", // TODO: Format actual timestamp
                style = SparkTheme.typography.bodySmall.copy(
                    fontSize = (SparkTheme.typography.bodySmall.fontSize.value * typography.bodyScale).sp
                ),
                color = SparkThreadDesign.Colors.MutedForeground
            )
        }
    }
}

/**
 * Bookmark card content with title and description
 */
@Composable
private fun ResponsiveBookmarkContent(
    bookmark: Bookmark,
    typography: AdaptiveTypography,
    spacing: AdaptiveSpacing,
    isGridLayout: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
    ) {
        // Title
        Text(
            text = bookmark.title,
            style = SparkTheme.typography.titleLarge.copy(
                fontSize = (SparkTheme.typography.titleLarge.fontSize.value * typography.titleScale).sp,
                fontWeight = FontWeight.Medium
            ),
            color = SparkThreadDesign.Colors.Foreground,
            maxLines = if (isGridLayout) 2 else 3,
            overflow = TextOverflow.Ellipsis
        )
        
        // Description (limited in grid layout)
        bookmark.description?.let { description ->
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = SparkTheme.typography.bodyMedium.copy(
                        fontSize = (SparkTheme.typography.bodyMedium.fontSize.value * typography.bodyScale).sp
                    ),
                    color = SparkThreadDesign.Colors.MutedForeground,
                    maxLines = if (isGridLayout) 2 else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * URL preview component
 */
@Composable
private fun ResponsiveBookmarkUrlPreview(
    bookmark: Bookmark,
    spacing: AdaptiveSpacing
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SparkThreadDesign.Shapes.Small,
        color = SparkThreadDesign.Colors.Secondary
    ) {
        Text(
            text = bookmark.url,
            style = SparkTheme.typography.bodySmall,
            color = SparkThreadDesign.Colors.Primary,
            modifier = Modifier.padding(spacing.small),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Responsive bookmark tags
 */
@Composable
private fun ResponsiveBookmarkTags(
    tags: List<String>,
    spacing: AdaptiveSpacing,
    maxTags: Int = 3
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        modifier = Modifier.fillMaxWidth()
    ) {
        tags.take(maxTags).forEach { tag ->
            Surface(
                shape = SparkThreadDesign.Shapes.Small,
                color = SparkThreadDesign.Colors.Sakura.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "#$tag",
                    style = SparkTheme.typography.labelSmall,
                    color = SparkThreadDesign.Colors.Primary,
                    modifier = Modifier.padding(
                        horizontal = spacing.small,
                        vertical = spacing.extraSmall
                    )
                )
            }
        }
        
        if (tags.size > maxTags) {
            Surface(
                shape = SparkThreadDesign.Shapes.Small,
                color = SparkThreadDesign.Colors.Muted
            ) {
                Text(
                    text = "+${tags.size - maxTags}",
                    style = SparkTheme.typography.labelSmall,
                    color = SparkThreadDesign.Colors.MutedForeground,
                    modifier = Modifier.padding(
                        horizontal = spacing.small,
                        vertical = spacing.extraSmall
                    )
                )
            }
        }
    }
}

/**
 * Bookmark action buttons
 */
@Composable
private fun ResponsiveBookmarkActions(
    bookmark: Bookmark,
    onFavoriteClick: (Bookmark) -> Unit,
    spacing: AdaptiveSpacing
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Favorite button
        IconButton(
            onClick = { onFavoriteClick(bookmark) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (bookmark.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (bookmark.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (bookmark.isFavorite) SparkThreadDesign.Colors.SealRed else SparkThreadDesign.Colors.MutedForeground,
                modifier = Modifier.size(18.dp)
            )
        }
        
        // Share button
        IconButton(
            onClick = { /* TODO: Implement share */ },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share bookmark",
                tint = SparkThreadDesign.Colors.MutedForeground,
                modifier = Modifier.size(18.dp)
            )
        }
        
        // More options button
        IconButton(
            onClick = { /* TODO: Show more options */ },
            modifier = Modifier.size(32.dp)
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

/**
 * Responsive collection card for grid layouts
 */
@Composable
fun ResponsiveCollectionCard(
    collection: Collection,
    bookmarkCount: Int,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = getAdaptiveSpacing()
    val typography = getAdaptiveTypography()
    val cardSize = getAdaptiveCardSize(ContentType.CollectionCard)
    
    SparkCard(
        onClick = { onCollectionClick(collection) },
        modifier = modifier
            .size(cardSize)
            .aspectRatio(1f),
        containerColor = SparkThreadDesign.Colors.Card,
        shape = SparkThreadDesign.Shapes.Medium,
        shadowElevation = SparkThreadDesign.Elevation.Card
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Collection icon with color
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = SparkThreadDesign.Colors.GoldGradient,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Collection info
            Column {
                Text(
                    text = collection.name,
                    style = SparkTheme.typography.titleMedium.copy(
                        fontSize = (SparkTheme.typography.titleMedium.fontSize.value * typography.titleScale).sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SparkThreadDesign.Colors.Foreground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(spacing.extraSmall))
                
                Text(
                    text = "$bookmarkCount bookmarks",
                    style = SparkTheme.typography.bodySmall.copy(
                        fontSize = (SparkTheme.typography.bodySmall.fontSize.value * typography.bodyScale).sp
                    ),
                    color = SparkThreadDesign.Colors.MutedForeground
                )
            }
        }
    }
}