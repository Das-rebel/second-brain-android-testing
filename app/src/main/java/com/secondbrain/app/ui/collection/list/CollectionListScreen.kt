package com.secondbrain.app.ui.collection.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.secondbrain.app.R
import com.secondbrain.app.data.model.BookmarkCollection
import com.secondbrain.app.ui.components.*
import com.secondbrain.app.ui.theme.backgroundColor
import com.secondbrain.app.ui.theme.onBackgroundColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CollectionListScreen(
    onNavigateToCollection: (Long) -> Unit,
    onNavigateToCreateCollection: () -> Unit,
    onNavigateToShareCollection: (Long) -> Unit,
    viewModel: CollectionListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showMoreMenuForCollection by remember { mutableStateOf<BookmarkCollection?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showFollowDialog by remember { mutableStateOf(false) }
    var showUnfollowDialog by remember { mutableStateOf(false) }
    var shareUrlInput by remember { mutableStateOf(TextFieldValue()) }
    
    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CollectionListEvent.NavigateToCollection -> 
                    onNavigateToCollection(event.collectionId)
                is CollectionListEvent.NavigateToCreateCollection -> 
                    onNavigateToCreateCollection()
                is CollectionListEvent.NavigateToShareCollection -> 
                    onNavigateToShareCollection(event.collectionId)
                is CollectionListEvent.ShowMessage -> {
                    // Show snackbar
                }
                is CollectionListEvent.ShowError -> {
                    // Show error snackbar
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.collections)) },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Handle sort */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleAction(CollectionListAction.CreateCollection) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Collection")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor())
        ) {
            when {
                state.isLoading && state.collections.isEmpty() -> {
                    LoadingView()
                }
                state.error != null && state.collections.isEmpty() -> {
                    ErrorView(
                        message = state.error,
                        onRetry = { viewModel.handleAction(CollectionListAction.Refresh) }
                    )
                }
                state.isEmpty -> {
                    EmptyView(
                        message = "No collections yet. Tap + to create one!",
                        icon = Icons.Default.Folder
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.filteredCollections) { collection ->
                            CollectionItem(
                                collection = collection,
                                onItemClick = { 
                                    viewModel.handleAction(CollectionListAction.CollectionClick(it)) 
                                },
                                onMoreClick = { collection ->
                                    showMoreMenuForCollection = collection
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // More options menu
    showMoreMenuForCollection?.let { collection ->
        DropdownMenu(
            expanded = true,
            onDismissRequest = { showMoreMenuForCollection = null }
        ) {
            DropdownMenuItem(
                text = { Text("View") },
                onClick = {
                    viewModel.handleAction(CollectionListAction.CollectionClick(collection))
                    showMoreMenuForCollection = null
                },
                leadingIcon = {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                }
            )
            
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    // Handle edit
                    showMoreMenuForCollection = null
                },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            )
            
            if (collection.isShared) {
                DropdownMenuItem(
                    text = { Text("Stop Sharing") },
                    onClick = {
                        viewModel.handleAction(CollectionListAction.UnfollowCollection(collection.id))
                        showMoreMenuForCollection = null
                    },
                    leadingIcon = {
                        Icon(Icons.Default.LinkOff, contentDescription = null)
                    }
                )
            } else {
                DropdownMenuItem(
                    text = { Text("Share") },
                    onClick = {
                        viewModel.handleAction(CollectionListAction.ShareCollection(collection))
                        showMoreMenuForCollection = null
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                )
            }
            
            Divider()
            
            DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    showMoreMenuForCollection = collection
                    showDeleteDialog = true
                    showMoreMenuForCollection = null
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && showMoreMenuForCollection != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Collection") },
            text = { Text("Are you sure you want to delete this collection? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMoreMenuForCollection?.let {
                            viewModel.handleAction(CollectionListAction.DeleteCollection(it))
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Follow collection dialog
    if (showFollowDialog) {
        AlertDialog(
            onDismissRequest = { showFollowDialog = false },
            title = { Text("Follow Shared Collection") },
            text = {
                Column {
                    Text("Enter the share URL or code:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shareUrlInput,
                        onValueChange = { shareUrlInput = it },
                        label = { Text("Share URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (shareUrlInput.text.isNotBlank()) {
                            viewModel.handleAction(CollectionListAction.FollowCollection(shareUrlInput.text))
                            showFollowDialog = false
                            shareUrlInput = TextFieldValue()
                        }
                    },
                    enabled = shareUrlInput.text.isNotBlank()
                ) {
                    Text("Follow")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showFollowDialog = false
                    shareUrlInput = TextFieldValue()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Unfollow confirmation dialog
    if (showUnfollowDialog && showMoreMenuForCollection != null) {
        AlertDialog(
            onDismissRequest = { showUnfollowDialog = false },
            title = { Text("Unfollow Collection") },
            text = { 
                Text("Are you sure you want to unfollow this collection? You won't see updates to it anymore.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMoreMenuForCollection?.let {
                            viewModel.handleAction(CollectionListAction.UnfollowCollection(it.id))
                        }
                        showUnfollowDialog = false
                    }
                ) {
                    Text("Unfollow")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnfollowDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = onBackgroundColor()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyView(
    message: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = onBackgroundColor().copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = onBackgroundColor().copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
