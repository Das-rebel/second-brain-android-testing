package com.secondbrain.app.ui.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import com.secondbrain.app.R
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.ui.components.ConfirmationDialog
import com.secondbrain.app.ui.components.LoadingIndicator
import com.secondbrain.app.ui.theme.spacing
import kotlinx.coroutines.flow.StateFlow
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkDetailScreen(
    bookmarkId: Long,
    onBackClick: () -> Unit,
    onBookmarkDeleted: () -> Unit,
    viewModel: BookmarkDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Load bookmark when the screen is first displayed
    LaunchedEffect(bookmarkId) {
        viewModel.loadBookmark(bookmarkId)
    }
    
    // Handle side effects
    LaunchedEffect(uiState.isBookmarkDeleted) {
        if (uiState.isBookmarkDeleted) {
            onBookmarkDeleted()
        }
    }
    
    // Show error message if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // In a real app, you would show a Snackbar with the error message
            println("Error: $error")
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bookmark_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    // Delete button
                    IconButton(
                        onClick = { viewModel.showDeleteConfirmation() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_bookmark)
                        )
                    }
                    
                    // Save button
                    IconButton(
                        onClick = { viewModel.saveBookmark() },
                        enabled = uiState.isEditMode && !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                    
                    // Edit button
                    if (!uiState.isEditMode) {
                        IconButton(
                            onClick = { viewModel.toggleEditMode() },
                            enabled = !uiState.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                
                uiState.bookmark != null -> {
                    BookmarkDetailContent(
                        uiState = uiState,
                        onTitleChange = { viewModel.updateTitle(it) },
                        onUrlChange = { viewModel.updateUrl(it) },
                        onDescriptionChange = { viewModel.updateDescription(it) },
                        onTagsChange = { viewModel.updateTags(it) },
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onToggleArchive = { viewModel.toggleArchive() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    // Show error state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.failed_to_load_bookmark),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                        
                        Button(onClick = { viewModel.loadBookmark(bookmarkId) }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.delete_bookmark),
            message = stringResource(R.string.are_you_sure_you_want_to_delete_this_bookmark),
            onConfirm = { viewModel.deleteBookmark() },
            onDismiss = { viewModel.dismissDeleteConfirmation() }
        )
    }
}

@Composable
private fun BookmarkDetailContent(
    uiState: BookmarkDetailUiState,
    onTitleChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookmark = uiState.bookmark ?: return
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(MaterialTheme.spacing.medium)
    ) {
        // Favicon and title/url row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
        ) {
            // Favicon
            val faviconUrl = bookmark.faviconUrl?.let { 
                if (it.startsWith("http")) it else "https://www.google.com/s2/favicons?domain=${URL(bookmark.url).host}"
            } ?: "https://www.google.com/s2/favicons?domain=${URL(bookmark.url).host}"
            
            // You would use Coil to load the favicon in a real app
            // For now, we'll just show a placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            
            // Title and URL
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isEditMode) {
                    OutlinedTextField(
                        value = uiState.editableTitle,
                        onValueChange = onTitleChange,
                        label = { Text(stringResource(R.string.title)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                } else {
                    Text(
                        text = bookmark.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                
                if (uiState.isEditMode) {
                    OutlinedTextField(
                        value = uiState.editableUrl,
                        onValueChange = onUrlChange,
                        label = { Text(stringResource(R.string.url)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        )
                    )
                } else {
                    Text(
                        text = bookmark.url,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Favorite button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (bookmark.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (bookmark.isFavorite) {
                        stringResource(R.string.remove_from_favorites)
                    } else {
                        stringResource(R.string.add_to_favorites)
                    },
                    tint = if (bookmark.isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        
        // Description
        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small)
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
        
        if (uiState.isEditMode) {
            OutlinedTextField(
                value = uiState.editableDescription ?: "",
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.add_a_description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false,
                maxLines = 5
            )
        } else {
            Text(
                text = bookmark.description ?: stringResource(R.string.no_description),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
            )
        }
        
        // Tags
        Text(
            text = stringResource(R.string.tags),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small)
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
        
        if (uiState.isEditMode) {
            // In a real app, you would implement a tag input field with chips
            // For now, we'll just show a simple text input
            OutlinedTextField(
                value = uiState.editableTags.joinToString(", "),
                onValueChange = { 
                    val tags = it.split(",")
                        .map { tag -> tag.trim() }
                        .filter { tag -> tag.isNotBlank() }
                    onTagsChange(tags)
                },
                label = { Text(stringResource(R.string.add_tags_separated_by_commas)) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            if (bookmark.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                ) {
                    bookmark.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = { /* Handle tag click */ },
                            label = { Text("#$tag") }
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_tags),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                )
            }
        }
        
        // Metadata section
        Text(
            text = stringResource(R.string.details),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.small)
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            ) {
                // Created at
                DetailRow(
                    label = stringResource(R.string.created),
                    value = bookmark.createdAt.toString(),
                    icon = Icons.Default.Schedule
                )
                
                Divider(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small))
                
                // Last updated
                DetailRow(
                    label = stringResource(R.string.last_updated),
                    value = bookmark.updatedAt.toString(),
                    icon = Icons.Default.Update
                )
                
                Divider(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small))
                
                // Last opened
                DetailRow(
                    label = stringResource(R.string.last_opened),
                    value = bookmark.lastOpened?.toString() ?: stringResource(R.string.never),
                    icon = Icons.Default.Visibility
                )
                
                Divider(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small))
                
                // Open count
                DetailRow(
                    label = stringResource(R.string.times_opened),
                    value = bookmark.openCount.toString(),
                    icon = Icons.Default.TrendingUp
                )
            }
        }
        
        // Archive button
        Button(
            onClick = onToggleArchive,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (bookmark.isArchived) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
                contentColor = if (bookmark.isArchived) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.medium)
        ) {
            Icon(
                imageVector = if (bookmark.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = if (bookmark.isArchived) {
                    stringResource(R.string.unarchive)
                } else {
                    stringResource(R.string.archive)
                }
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
