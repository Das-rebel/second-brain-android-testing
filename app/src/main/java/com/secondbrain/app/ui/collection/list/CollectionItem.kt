package com.secondbrain.app.ui.collection.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.secondbrain.app.data.model.BookmarkCollection
import com.secondbrain.app.ui.theme.cardBackgroundColor
import com.secondbrain.app.ui.theme.onBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionItem(
    collection: BookmarkCollection,
    onItemClick: (BookmarkCollection) -> Unit,
    onMoreClick: (BookmarkCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor(isDark),
            contentColor = onBackgroundColor(isDark)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onItemClick(collection) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Collection Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(android.graphics.Color.parseColor(collection.color))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = collection.icon?.let { getIconForName(it) } ?: Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Collection Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = collection.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (!collection.description.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = collection.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = onBackgroundColor(isDark).copy(alpha = 0.7f)
                        )
                    }
                }
                
                // More Options
                IconButton(
                    onClick = { onMoreClick(collection) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }
            
            // Footer
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Item Count
                Text(
                    text = "${collection.itemCount} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = onBackgroundColor(isDark).copy(alpha = 0.6f)
                )
                
                // Shared Indicator
                if (collection.isShared) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Shared",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shared",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getIconForName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "folder" -> Icons.Default.Folder
        "bookmark" -> Icons.Default.Bookmark
        "star" -> Icons.Default.Star
        "favorite" -> Icons.Default.Favorite
        "work" -> Icons.Default.Work
        "school" -> Icons.Default.School
        "home" -> Icons.Default.Home
        "shopping_cart" -> Icons.Default.ShoppingCart
        "flight" -> Icons.Default.Flight
        "restaurant" -> Icons.Default.Restaurant
        "local_movies" -> Icons.Default.Movie
        "music_note" -> Icons.Default.MusicNote
        "sports" -> Icons.Default.SportsSoccer
        "fitness" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Folder
    }
}
