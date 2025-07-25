package com.secondbrain.app.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.BookmarkFilter
import com.secondbrain.app.domain.usecase.BookmarkUseCases
import com.secondbrain.app.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Bookmark screen.
 */
data class BookmarkUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedBookmark: Bookmark? = null,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val filter: BookmarkFilter = BookmarkFilter(),
    val isSelectionMode: Boolean = false,
    val selectedBookmarkIds: Set<Long> = emptySet()
) {
    val filteredBookmarks: List<Bookmark>
        get() = bookmarks
        
    val selectedBookmarks: List<Bookmark>
        get() = bookmarks.filter { selectedBookmarkIds.contains(it.id) }
}

/**
 * Represents different bulk actions that can be performed on selected bookmarks.
 */
sealed class BulkAction {
    object Delete : BulkAction()
    object Archive : BulkAction()
    object Unarchive : BulkAction()
    object Favorite : BulkAction()
    object Unfavorite : BulkAction()
    object AddToCollection : BulkAction()
}

/**
 * Actions that can be performed on the Bookmark screen.
 */
sealed class BookmarkAction {
    data class LoadBookmarks(val collectionId: Long) : BookmarkAction()
    object Refresh : BookmarkAction()
    data class Search(val query: String) : BookmarkAction()
    data class Filter(val filter: BookmarkFilter) : BookmarkAction()
    data class ToggleFavorite(val bookmarkId: Long, val isFavorite: Boolean) : BookmarkAction()
    data class ToggleArchive(val bookmarkId: Long, val isArchived: Boolean) : BookmarkAction()
    data class Delete(val bookmarkId: Long) : BookmarkAction()
    data class Open(val bookmarkId: Long) : BookmarkAction()
    data class Share(val bookmarkId: Long) : BookmarkAction()
    data class Edit(val bookmarkId: Long) : BookmarkAction()
    object Create : BookmarkAction()
    object ClearError : BookmarkAction()
    
    // Selection actions
    data class ToggleSelect(val bookmarkId: Long) : BookmarkAction()
    object ToggleSelectAll : BookmarkAction()
    object ClearSelection : BookmarkAction()
    object StartSelection : BookmarkAction()
    object CancelSelection : BookmarkAction()
    
    // Bulk actions
    data class PerformBulkAction(val action: BulkAction) : BookmarkAction()
}

/**
 * Events that can be emitted by the ViewModel.
 */
sealed class BookmarkEvent {
    data class NavigateToBookmark(val bookmarkId: Long) : BookmarkEvent()
    object NavigateToCreateBookmark : BookmarkEvent()
    data class ShowMessage(val message: String) : BookmarkEvent()
    data class ShowError(val error: String) : BookmarkEvent()
    data class ShareBookmark(val bookmark: Bookmark) : BookmarkEvent()
    data class NavigateToEditBookmark(val bookmark: Bookmark) : BookmarkEvent()
}

/**
 * ViewModel for managing bookmark-related UI state and business logic.
 */
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val useCases: BookmarkUseCases,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()
    
    private val _bulkActionState = MutableStateFlow<BulkAction?>(null)
    val bulkActionState: StateFlow<BulkAction?> = _bulkActionState.asStateFlow()
    
    init {
        loadBookmarks(1) // Default collection ID
    }

    private val _events = MutableSharedFlow<BookmarkEvent>()
    val events: SharedFlow<BookmarkEvent> = _events.asSharedFlow()

    private var currentCollectionId: Long = -1L

    /**
     * Handle actions from the UI.
     */
    fun handleAction(action: BookmarkAction) {
        when (action) {
            is BookmarkAction.LoadBookmarks -> loadBookmarks(action.collectionId)
            is BookmarkAction.Refresh -> refreshBookmarks()
            is BookmarkAction.Search -> updateSearchQuery(action.query)
            is BookmarkAction.Filter -> updateFilter(action.filter)
            is BookmarkAction.ToggleFavorite -> toggleFavorite(action.bookmarkId, action.isFavorite)
            is BookmarkAction.ToggleArchive -> archiveBookmark(action.bookmarkId, action.isArchived)
            is BookmarkAction.Delete -> deleteBookmark(action.bookmarkId)
            is BookmarkAction.Open -> openBookmark(action.bookmarkId)
            is BookmarkAction.Share -> shareBookmark(action.bookmarkId)
            is BookmarkAction.Edit -> editBookmark(action.bookmarkId)
            BookmarkAction.Create -> createBookmark()
            BookmarkAction.ClearError -> clearError()
            
            // Selection actions
            is BookmarkAction.ToggleSelect -> toggleBookmarkSelection(action.bookmarkId)
            BookmarkAction.ToggleSelectAll -> toggleSelectAll()
            BookmarkAction.ClearSelection -> clearSelection()
            BookmarkAction.StartSelection -> startSelection()
            BookmarkAction.CancelSelection -> cancelSelection()
            
            // Bulk actions
            is BookmarkAction.PerformBulkAction -> performBulkAction(action.action)
        }
    }

    /**
     * Load bookmarks for a specific collection.
     */
    private fun loadBookmarks(collectionId: Long) {
        currentCollectionId = collectionId
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Create filter with collection ID and current search/filter state
                val currentState = _uiState.value
                val filter = currentState.filter.copy(
                    collectionId = collectionId,
                    searchQuery = currentState.searchQuery.takeIf { it.isNotBlank() }
                )
                
                // Observe bookmarks from the domain layer with filtering
                useCases.getFilteredBookmarks(filter)
                    .collect { bookmarks ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                bookmarks = bookmarks,
                                isLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = errorHandler.getUserFriendlyErrorMessage(e),
                        isLoading = false,
                        isRefreshing = false
                    )
                }
                _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
            }
        }
    }

    /**
     * Refresh bookmarks from the remote data source.
     */
    private fun refreshBookmarks() {
        if (currentCollectionId == -1L) return
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
                useCases.refreshBookmarks(currentCollectionId)
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false) }
                _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
            }
        }
    }

    /**
     * Update the search query and filter bookmarks.
     */
    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // Reload bookmarks with new search query
        if (currentCollectionId != -1L) {
            loadBookmarks(currentCollectionId)
        }
    }

    /**
     * Update the current filter.
     */
    private fun updateFilter(filter: BookmarkFilter) {
        _uiState.update { it.copy(filter = filter) }
        // Reload bookmarks with new filter
        if (currentCollectionId != -1L) {
            loadBookmarks(currentCollectionId)
        }
    }

    /**
     * Toggle the favorite status of a bookmark.
     */
    private fun toggleFavorite(bookmarkId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                useCases.toggleFavorite(bookmarkId, isFavorite).onSuccess {
                    _events.emit(BookmarkEvent.ShowMessage(
                        if (isFavorite) "Added to favorites" else "Removed from favorites"
                    ))
                }.onFailure { e ->
                    _events.emit(BookmarkEvent.ShowError(
                        errorHandler.getUserFriendlyErrorMessage(e)
                    ))
                }
            } catch (e: Exception) {
                _events.emit(BookmarkEvent.ShowError(
                    errorHandler.getUserFriendlyErrorMessage(e)
                ))
            }
        }
    }

    /**
     * Toggle the archive status of a bookmark.
     */
    private fun archiveBookmark(bookmarkId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            try {
                useCases.archiveBookmark(bookmarkId, isArchived).onSuccess {
                    _events.emit(BookmarkEvent.ShowMessage(
                        if (isArchived) "Bookmark archived" else "Bookmark unarchived"
                    ))
                }.onFailure { e ->
                    _events.emit(BookmarkEvent.ShowError(
                        errorHandler.getUserFriendlyErrorMessage(e)
                    ))
                }
            } catch (e: Exception) {
                _events.emit(BookmarkEvent.ShowError(
                    errorHandler.getUserFriendlyErrorMessage(e)
                ))
            }
        }
    }

    /**
     * Delete a bookmark.
     */
    private fun deleteBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                useCases.deleteBookmark(bookmarkId).onSuccess {
                    _events.emit(BookmarkEvent.ShowMessage("Bookmark deleted"))
                }.onFailure { e ->
                    _events.emit(BookmarkEvent.ShowError(
                        errorHandler.getUserFriendlyErrorMessage(e)
                    ))
                }
            } catch (e: Exception) {
                _events.emit(BookmarkEvent.ShowError(
                    errorHandler.getUserFriendlyErrorMessage(e)
                ))
            }
        }
    }

    /**
     * Open a bookmark and update its last opened timestamp.
     */
    private fun openBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                useCases.updateLastOpened(bookmarkId)
                _events.emit(BookmarkEvent.NavigateToBookmark(bookmarkId))
            } catch (e: Exception) {
                _events.emit(BookmarkEvent.ShowError(
                    errorHandler.getUserFriendlyErrorMessage(e)
                ))
            }
        }
    }

    /**
     * Share a bookmark.
     */
    private fun shareBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                val result = useCases.getBookmarkById(bookmarkId)
                result.onSuccess { bookmark ->
                    _events.emit(BookmarkEvent.ShareBookmark(bookmark))
                }.onFailure {
                    _events.emit(BookmarkEvent.ShowError("Bookmark not found"))
                }
            } catch (e: Exception) {
                _events.emit(BookmarkEvent.ShowError("Failed to share bookmark"))
            }
        }
    }

    /**
     * Navigate to edit a bookmark.
     */
    private fun editBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                val result = useCases.getBookmarkById(bookmarkId)
                result.onSuccess { bookmark ->
                    _events.emit(BookmarkEvent.NavigateToEditBookmark(bookmark))
                }.onFailure {
                    _events.emit(BookmarkEvent.ShowError("Bookmark not found"))
                }
            } catch (e: Exception) {
                _events.emit(BookmarkEvent.ShowError("Failed to edit bookmark"))
            }
        }
    }

    /**
     * Navigate to create a new bookmark.
     */
    private fun createBookmark() {
        viewModelScope.launch {
            _events.emit(BookmarkEvent.NavigateToCreateBookmark)
        }
    }

    /**
     * Clear any error message.
     */
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Selection management
    private fun startSelection() {
        _uiState.update { it.copy(isSelectionMode = true) }
    }
    
    private fun cancelSelection() {
        _uiState.update { state ->
            state.copy(
                isSelectionMode = false,
                selectedBookmarkIds = emptySet()
            )
        }
    }
    
    private fun toggleBookmarkSelection(bookmarkId: Long) {
        _uiState.update { state ->
            val newSelection = state.selectedBookmarkIds.toMutableSet()
            if (newSelection.contains(bookmarkId)) {
                newSelection.remove(bookmarkId)
            } else {
                newSelection.add(bookmarkId)
            }
            
            state.copy(
                selectedBookmarkIds = newSelection,
                isSelectionMode = newSelection.isNotEmpty()
            )
        }
    }
    
    private fun toggleSelectAll() {
        _uiState.update { state ->
            val allSelected = state.bookmarks.size == state.selectedBookmarkIds.size
            val newSelection = if (allSelected) {
                emptySet()
            } else {
                state.bookmarks.map { it.id }.toSet()
            }
            
            state.copy(
                selectedBookmarkIds = newSelection,
                isSelectionMode = !allSelected
            )
        }
    }
    
    private fun clearSelection() {
        _uiState.update { state ->
            state.copy(
                selectedBookmarkIds = emptySet(),
                isSelectionMode = false
            )
        }
    }
    
    // Bulk actions
    private fun performBulkAction(action: BulkAction) {
        val selectedIds = _uiState.value.selectedBookmarkIds.toList()
        if (selectedIds.isEmpty()) return
        
        viewModelScope.launch {
            try {
                when (action) {
                    is BulkAction.Delete -> {
                        useCases.deleteMultipleBookmarks(selectedIds).onFailure { e ->
                            _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
                            return@launch
                        }
                        _events.emit(BookmarkEvent.ShowMessage("${selectedIds.size} bookmarks deleted"))
                    }
                    is BulkAction.Archive -> {
                        useCases.archiveMultipleBookmarks(selectedIds, true).onFailure { e ->
                            _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
                            return@launch
                        }
                        _events.emit(BookmarkEvent.ShowMessage("${selectedIds.size} bookmarks archived"))
                    }
                    is BulkAction.Unarchive -> {
                        useCases.archiveMultipleBookmarks(selectedIds, false).onFailure { e ->
                            _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
                            return@launch
                        }
                        _events.emit(BookmarkEvent.ShowMessage("${selectedIds.size} bookmarks unarchived"))
                    }
                    is BulkAction.Favorite -> {
                        useCases.toggleFavoriteForMultiple(selectedIds, true).onFailure { e ->
                            _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
                            return@launch
                        }
                        _events.emit(BookmarkEvent.ShowMessage("${selectedIds.size} bookmarks added to favorites"))
                    }
                    is BulkAction.Unfavorite -> {
                        useCases.toggleFavoriteForMultiple(selectedIds, false).onFailure { e ->
                            _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
                            return@launch
                        }
                        _events.emit(BookmarkEvent.ShowMessage("${selectedIds.size} bookmarks removed from favorites"))
                    }
                    is BulkAction.AddToCollection -> {
                        // Implementation for adding to collection will be added later
                        _events.emit(BookmarkEvent.ShowMessage("Add to collection not yet implemented"))
                    }
                }
                
                // Clear selection after action
                clearSelection()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = errorHandler.getUserFriendlyErrorMessage(e)) }
                _events.emit(BookmarkEvent.ShowError(errorHandler.getUserFriendlyErrorMessage(e)))
            }
        }
    }
}
