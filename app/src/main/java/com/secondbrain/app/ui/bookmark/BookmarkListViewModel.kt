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

@HiltViewModel
class BookmarkListViewModel @Inject constructor(
    private val bookmarkUseCases: BookmarkUseCases,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _state = MutableStateFlow(BookmarkListState())
    val state: StateFlow<BookmarkListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<BookmarkListEvent>()
    val events: SharedFlow<BookmarkListEvent> = _events.asSharedFlow()

    private var currentCollectionId: Long? = null

    init {
        loadBookmarks()
    }

    fun handleAction(action: BookmarkListAction) {
        when (action) {
            is BookmarkListAction.LoadBookmarks -> loadBookmarks(collectionId = action.collectionId)
            is BookmarkListAction.Refresh -> refreshBookmarks()
            is BookmarkListAction.Search -> updateSearchQuery(action.query)
            is BookmarkListAction.Filter -> updateFilter(action.filter)
            is BookmarkListAction.ToggleFavorite -> toggleFavorite(action.bookmarkId, action.isFavorite)
            is BookmarkListAction.Archive -> archiveBookmark(action.bookmarkId, action.isArchived)
            is BookmarkListAction.Delete -> deleteBookmark(action.bookmarkId)
            is BookmarkListAction.OpenBookmark -> openBookmark(action.bookmarkId)
            is BookmarkListAction.CreateBookmark -> createBookmark()
        }
    }

    private fun loadBookmarks(collectionId: Long? = null) {
        currentCollectionId = collectionId ?: currentCollectionId
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Create filter based on current state
                val currentState = _state.value
                val filter = currentState.filter.copy(
                    collectionId = currentCollectionId,
                    searchQuery = currentState.searchQuery.takeIf { it.isNotBlank() }
                )
                
                bookmarkUseCases.getFilteredBookmarks(filter).collect { bookmarksList ->
                    _state.update { it.copy(bookmarks = bookmarksList, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = errorHandler.getUserFriendlyErrorMessage(e)
                    ) 
                }
            }
        }
    }

    private fun refreshBookmarks() {
        _state.update { it.copy(isRefreshing = true) }
        loadBookmarks()
        _state.update { it.copy(isRefreshing = false) }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        // Reload bookmarks with new search query
        loadBookmarks()
    }

    private fun updateFilter(filter: BookmarkFilter) {
        _state.update { it.copy(filter = filter) }
        // Reload bookmarks with new filter
        loadBookmarks()
    }

    private fun toggleFavorite(bookmarkId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            bookmarkUseCases.toggleFavorite(bookmarkId, isFavorite).onFailure { e ->
                _state.update { it.copy(error = errorHandler.getUserFriendlyErrorMessage(e)) }
            }
        }
    }

    private fun archiveBookmark(bookmarkId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            bookmarkUseCases.archiveBookmark(bookmarkId, isArchived).onFailure { e ->
                _state.update { it.copy(error = errorHandler.getUserFriendlyErrorMessage(e)) }
            }
        }
    }

    private fun deleteBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            bookmarkUseCases.deleteBookmark(bookmarkId).onFailure { e ->
                _state.update { it.copy(error = errorHandler.getUserFriendlyErrorMessage(e)) }
            }
        }
    }

    private fun openBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            _events.emit(BookmarkListEvent.NavigateToBookmark(bookmarkId))
            bookmarkUseCases.updateLastOpened(bookmarkId)
        }
    }

    private fun createBookmark() {
        viewModelScope.launch {
            _events.emit(BookmarkListEvent.NavigateToCreateBookmark)
        }
    }
}

sealed class BookmarkListAction {
    data class LoadBookmarks(val collectionId: Long? = null) : BookmarkListAction()
    object Refresh : BookmarkListAction()
    data class Search(val query: String) : BookmarkListAction()
    data class Filter(val filter: BookmarkFilter) : BookmarkListAction()
    data class ToggleFavorite(val bookmarkId: Long, val isFavorite: Boolean) : BookmarkListAction()
    data class Archive(val bookmarkId: Long, val isArchived: Boolean) : BookmarkListAction()
    data class Delete(val bookmarkId: Long) : BookmarkListAction()
    data class OpenBookmark(val bookmarkId: Long) : BookmarkListAction()
    object CreateBookmark : BookmarkListAction()
}

sealed class BookmarkListEvent {
    data class NavigateToBookmark(val bookmarkId: Long) : BookmarkListEvent()
    object NavigateToCreateBookmark : BookmarkListEvent()
    data class ShowMessage(val message: String) : BookmarkListEvent()
    data class ShowError(val error: String) : BookmarkListEvent()
}
