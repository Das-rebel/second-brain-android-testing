package com.secondbrain.app.ui.bookmark

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.secondbrain.app.R
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.ui.components.BulkActionButton
import com.secondbrain.app.ui.components.LoadingIndicator
import com.secondbrain.app.ui.components.SelectionTopAppBar
import com.secondbrain.app.ui.components.WindowSizeClass
import com.secondbrain.app.ui.theme.spacing
import com.secondbrain.app.ui.utils.showBulkDeleteConfirmationDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookmarkListScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    onNavigateToBookmark: (Long) -> Unit = {},
    onNavigateToAddBookmark: () -> Unit = {},
    windowSizeClass: WindowSizeClass = WindowSizeClass.Compact,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BookmarkEvent.NavigateToBookmark -> onNavigateToBookmark(event.bookmarkId)
                is BookmarkEvent.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                is BookmarkEvent.ShowError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.error,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                is BookmarkEvent.NavigateToEditBookmark -> {
                    // Handle navigation to edit screen if needed
                }
                is BookmarkEvent.ShareBookmark -> {
                    // Handle sharing if needed
                }
                else -> {}
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (uiState.isSelectionMode) {
                SelectionTopAppBar(
                    selectedCount = uiState.selectedBookmarkIds.size,
                    onSelectAll = { 
                        if (uiState.selectedBookmarkIds.size == uiState.filteredBookmarks.size) {
                            viewModel.handleAction(BookmarkAction.ClearSelection)
                        } else {
                            viewModel.handleAction(BookmarkAction.ToggleSelectAll)
                        }
                    },
                    onCancel = { viewModel.handleAction(BookmarkAction.CancelSelection) },
                    actions = {
                        // Favorite/Unfavorite
                        BulkActionButton(
                            icon = if (uiState.selectedBookmarks.all { it.isFavorite }) {
                                Icons.Outlined.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (uiState.selectedBookmarks.all { it.isFavorite }) {
                                stringResource(R.string.action_unfavorite)
                            } else {
                                stringResource(R.string.action_favorite)
                            },
                            onClick = {
                                viewModel.handleAction(
                                    BookmarkAction.PerformBulkAction(
                                        if (uiState.selectedBookmarks.all { it.isFavorite }) {
                                            BulkAction.Unfavorite
                                        } else {
                                            BulkAction.Favorite
                                        }
                                    )
                                )
                            }
                        )
                        
                        // Archive/Unarchive
                        BulkActionButton(
                            icon = if (uiState.selectedBookmarks.all { it.isArchived }) {
                                Icons.Outlined.Unarchive
                            } else {
                                Icons.Outlined.Archive
                            },
                            contentDescription = if (uiState.selectedBookmarks.all { it.isArchived }) {
                                stringResource(R.string.action_unarchive)
                            } else {
                                stringResource(R.string.action_archive)
                            },
                            onClick = {
                                viewModel.handleAction(
                                    BookmarkAction.PerformBulkAction(
                                        if (uiState.selectedBookmarks.all { it.isArchived }) {
                                            BulkAction.Unarchive
                                        } else {
                                            BulkAction.Archive
                                        }
                                    )
                                )
                            }
                        )
                        
                        // Delete
                        BulkActionButton(
                            icon = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            onClick = {
                                scope.launch {
                                    val confirmed = showBulkDeleteConfirmationDialog(
                                        context = context,
                                        count = uiState.selectedBookmarkIds.size
                                    )
                                    if (confirmed) {
                                        viewModel.handleAction(
                                            BookmarkAction.PerformBulkAction(BulkAction.Delete)
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.bookmarks)) },
                    actions = {
                        IconButton(onClick = { viewModel.handleAction(BookmarkAction.StartSelection) }) {
                            Icon(
                                imageVector = Icons.Filled.SelectAll,
                                contentDescription = stringResource(R.string.select)
                            )
                        }
                        IconButton(onClick = onNavigateToAddBookmark) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_bookmark)
                            )
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }
                uiState.filteredBookmarks.isEmpty() -> {
                    EmptyBookmarks(
                        onAddBookmark = onNavigateToAddBookmark,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    BookmarkList(
                        bookmarks = uiState.filteredBookmarks,
                        onToggleFavorite = { bookmark ->
                            viewModel.handleAction(
                                BookmarkAction.ToggleFavorite(bookmark.id, !bookmark.isFavorite)
                            )
                        },
                        onToggleArchive = { bookmark ->
                            viewModel.handleAction(
                                BookmarkAction.ToggleArchive(bookmark.id, !bookmark.isArchived)
                            )
                        },
                        onDeleteBookmark = { bookmark ->
                            scope.launch {
                                val confirmed = showBulkDeleteConfirmationDialog(
                                    context = context,
                                    count = 1
                                )
                                if (confirmed) {
                                    viewModel.handleAction(BookmarkAction.Delete(bookmark.id))
                                }
                            }
                        },
                        onShareBookmark = { bookmark ->
                            viewModel.handleAction(BookmarkAction.Share(bookmark.id))
                        },
                        onEditBookmark = { bookmark ->
                            viewModel.handleAction(BookmarkAction.Edit(bookmark.id))
                        },
                        onBookmarkClick = { bookmark ->
                            if (uiState.isSelectionMode) {
                                viewModel.handleAction(BookmarkAction.ToggleSelect(bookmark.id))
                            } else {
                                viewModel.handleAction(BookmarkAction.Open(bookmark.id))
                            }
                        },
                        onBookmarkLongClick = { bookmark ->
                            viewModel.handleAction(BookmarkAction.StartSelection)
                            viewModel.handleAction(BookmarkAction.ToggleSelect(bookmark.id))
                        },
                        isSelectionMode = uiState.isSelectionMode,
                        selectedBookmarkIds = uiState.selectedBookmarkIds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Show error message if any
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun BookmarkList(
    bookmarks: List<Bookmark>,
    onToggleFavorite: (Bookmark) -> Unit,
    onToggleArchive: (Bookmark) -> Unit,
    onDeleteBookmark: (Bookmark) -> Unit,
    onShareBookmark: (Bookmark) -> Unit,
    onEditBookmark: (Bookmark) -> Unit,
    onBookmarkClick: (Bookmark) -> Unit = {},
    onBookmarkLongClick: (Bookmark) -> Unit = {},
    isSelectionMode: Boolean = false,
    selectedBookmarkIds: Set<Long> = emptySet(),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        items(bookmarks, key = { it.id }) { bookmark ->
            val isSelected = selectedBookmarkIds.contains(bookmark.id)
            
            BookmarkItem(
                bookmark = bookmark,
                onClick = { onBookmarkClick(bookmark) },
                onLongClick = { onBookmarkLongClick(bookmark) },
                onToggleFavorite = { onToggleFavorite(bookmark) },
                onToggleArchive = { onToggleArchive(bookmark) },
                onDelete = { onDeleteBookmark(bookmark) },
                onShare = { onShareBookmark(bookmark) },
                onEdit = { onEditBookmark(bookmark) },
                isSelected = isSelected,
                isSelectionMode = isSelectionMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onToggleFavorite: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showCopyConfirmation by remember { mutableStateOf(false) }

    // Card background color based on selection
    val cardColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = {
            if (isSelectionMode) {
                onLongClick()
            } else {
                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.small)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onLongClick() },
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                )
            }
            
            // Bookmark content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = MaterialTheme.spacing.small)
            ) {
                // Title row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Favicon
                    bookmark.faviconUrl?.let { faviconUrl ->
                        AsyncImage(
                            model = faviconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    
                    // Title
                    Text(
                        text = bookmark.title.ifEmpty { stringResource(R.string.untitled) },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Favorite button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (bookmark.isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (bookmark.isFavorite) {
                                stringResource(R.string.action_unfavorite)
                            } else {
                                stringResource(R.string.action_favorite)
                            },
                            tint = if (bookmark.isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                
                // URL
                Text(
                    text = bookmark.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                // Description (if available)
                bookmark.description?.takeIf { it.isNotBlank() }?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                // Tags (if available)
                if (bookmark.tags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        bookmark.tags.forEach { tag ->
                            SuggestionChip(
                                onClick = { /* Handle tag click */ },
                                label = { Text(tag) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }
            
            // Menu button (only show when not in selection mode)
            if (!isSelectionMode) {
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.more_options)
                        )
                    }
                    
                    // Dropdown menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        // Edit option
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit_bookmark)) },
                            onClick = {
                                onEdit?.invoke()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            }
                        )
                        
                        // Share option
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.share)) },
                            onClick = {
                                onShare?.invoke()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Share,
                                    contentDescription = null
                                )
                            }
                        )
                        
                        // Copy link option
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.copy_link)) },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(bookmark.url))
                                showCopyConfirmation = true
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.ContentCopy,
                                    contentDescription = null
                                )
                            }
                        )
                        
                        // Archive/Unarchive option
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (bookmark.isArchived) {
                                        stringResource(R.string.action_unarchive)
                                    } else {
                                        stringResource(R.string.action_archive)
                                    }
                                )
                            },
                            onClick = {
                                onToggleArchive()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (bookmark.isArchived) {
                                        Icons.Outlined.Unarchive
                                    } else {
                                        Icons.Outlined.Archive
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                        
                        // Delete option
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    stringResource(R.string.delete_bookmark),
                                    color = MaterialTheme.colorScheme.error
                                ) 
                            },
                            onClick = {
                                showDeleteConfirmation = true
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.confirm_delete_bookmark)) },
            text = { Text(stringResource(R.string.are_you_sure_delete_bookmark)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete?.invoke()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(
                        stringResource(android.R.string.ok),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
    
    // Show feedback when link is copied
    if (showCopyConfirmation) {
        LaunchedEffect(showCopyConfirmation) {
            kotlinx.coroutines.delay(2000)
            showCopyConfirmation = false
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = stringResource(R.string.link_copied),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyBookmarks(
    onAddBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.no_bookmarks),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.no_bookmarks_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddBookmark,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(R.string.add_bookmark))
        }
    }
}