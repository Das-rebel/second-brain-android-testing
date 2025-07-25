package com.secondbrain.app.ui.collection.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.app.data.model.BookmarkCollection
import com.secondbrain.app.data.repository.CollectionRepository
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.usecase.BookmarkUseCases
import com.secondbrain.app.navigation.Screen
import com.secondbrain.app.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val bookmarkUseCases: BookmarkUseCases,
    private val errorHandler: ErrorHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val collectionId = checkNotNull(savedStateHandle.get<Long>(Screen.COLLECTION_ID_ARG))
    
    private val _state = MutableStateFlow(CollectionDetailState(isLoading = true))
    val state: StateFlow<CollectionDetailState> = _state.asStateFlow()
    
    private val _events = MutableSharedFlow<CollectionDetailEvent>()
    val events = _events.asSharedFlow()
    
    init {
        loadCollection()
        loadBookmarks()
    }
    
    fun handleAction(action: CollectionDetailAction) {
        when (action) {
            is CollectionDetailAction.Refresh -> refresh()
            is CollectionDetailAction.BookmarkClick -> onBookmarkClick(action.bookmark)
            is CollectionDetailAction.BookmarkLongClick -> onBookmarkLongClick(action.bookmark)
            is CollectionDetailAction.ToggleBookmarkOptions -> onToggleBookmarkOptions(action.bookmark)
            is CollectionDetailAction.DeleteBookmark -> onDeleteBookmark(action.bookmark)
            is CollectionDetailAction.EditBookmark -> onEditBookmark(action.bookmark)
            is CollectionDetailAction.ShareBookmark -> onShareBookmark(action.bookmark)
            is CollectionDetailAction.CopyBookmarkLink -> onCopyBookmarkLink(action.bookmark)
        }
    }
    
    private fun loadCollection() {
        viewModelScope.launch {
            collectionRepository.getCollectionById(collectionId)
                .catch { e ->
                    _state.update { it.copy(
                        error = errorHandler.handleError(e),
                        isLoading = false
                    )}
                }
                .collect { collection ->
                    _state.update { it.copy(
                        collection = collection,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }
    
    private fun loadBookmarks() {
        viewModelScope.launch {
            bookmarkUseCases.getBookmarksByCollection(collectionId)
                .catch { e ->
                    _state.update { it.copy(
                        error = errorHandler.handleError(e),
                        isLoading = false
                    )}
                }
                .collect { bookmarks ->
                    _state.update { it.copy(
                        bookmarks = bookmarks,
                        isLoading = false,
                        error = null
                    )}
                }
        }
    }
    
    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                // Refresh both collection and bookmarks
                collectionRepository.refreshCollection(collectionId)
                bookmarkUseCases.refreshBookmarks(collectionId)
                _state.update { it.copy(isRefreshing = false) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isRefreshing = false,
                        error = errorHandler.handleError(e)
                    )
                }
            }
        }
    }
    
    private fun onBookmarkClick(bookmark: Bookmark) {
        viewModelScope.launch {
            _events.emit(CollectionDetailEvent.NavigateToBookmark(bookmark.url))
        }
    }
    
    private fun onBookmarkLongClick(bookmark: Bookmark) {
        _state.update { it.copy(
            isBookmarkOptionsExpanded = true,
            selectedBookmark = bookmark
        )}
    }
    
    private fun onToggleBookmarkOptions(bookmark: Bookmark) {
        _state.update { currentState ->
            if (currentState.selectedBookmark?.id == bookmark.id && currentState.isBookmarkOptionsExpanded) {
                currentState.copy(
                    isBookmarkOptionsExpanded = false,
                    selectedBookmark = null
                )
            } else {
                currentState.copy(
                    isBookmarkOptionsExpanded = true,
                    selectedBookmark = bookmark
                )
            }
        }
    }
    
    private fun onDeleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            try {
                bookmarkUseCases.deleteBookmark(bookmark.id).onSuccess {
                    _events.emit(CollectionDetailEvent.ShowMessage("Bookmark deleted"))
                }.onFailure { e ->
                    _events.emit(CollectionDetailEvent.ShowError(errorHandler.handleError(e)))
                }
            } catch (e: Exception) {
                _events.emit(CollectionDetailEvent.ShowError(errorHandler.handleError(e)))
            } finally {
                _state.update { it.copy(
                    isBookmarkOptionsExpanded = false,
                    selectedBookmark = null
                )}
            }
        }
    }
    
    private fun onEditBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            _events.emit(CollectionDetailEvent.NavigateToEditBookmark(bookmark.id))
            _state.update { it.copy(
                isBookmarkOptionsExpanded = false,
                selectedBookmark = null
            )}
        }
    }
    
    private fun onShareBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            _events.emit(CollectionDetailEvent.ShareBookmark(bookmark))
            _state.update { it.copy(
                isBookmarkOptionsExpanded = false,
                selectedBookmark = null
            )}
        }
    }
    
    private fun onCopyBookmarkLink(bookmark: Bookmark) {
        viewModelScope.launch {
            _events.emit(CollectionDetailEvent.CopyBookmarkLink(bookmark.url))
            _events.emit(CollectionDetailEvent.ShowMessage("Link copied to clipboard"))
            _state.update { it.copy(
                isBookmarkOptionsExpanded = false,
                selectedBookmark = null
            )}
        }
    }
}

sealed class CollectionDetailAction {
    object Refresh : CollectionDetailAction()
    data class BookmarkClick(val bookmark: Bookmark) : CollectionDetailAction()
    data class BookmarkLongClick(val bookmark: Bookmark) : CollectionDetailAction()
    data class ToggleBookmarkOptions(val bookmark: Bookmark) : CollectionDetailAction()
    data class DeleteBookmark(val bookmark: Bookmark) : CollectionDetailAction()
    data class EditBookmark(val bookmark: Bookmark) : CollectionDetailAction()
    data class ShareBookmark(val bookmark: Bookmark) : CollectionDetailAction()
    data class CopyBookmarkLink(val bookmark: Bookmark) : CollectionDetailAction()
}

sealed class CollectionDetailEvent {
    data class NavigateToBookmark(val url: String) : CollectionDetailEvent()
    data class NavigateToEditBookmark(val bookmarkId: Long) : CollectionDetailEvent()
    data class ShowMessage(val message: String) : CollectionDetailEvent()
    data class ShowError(val message: String) : CollectionDetailEvent()
    data class ShareBookmark(val bookmark: Bookmark) : CollectionDetailEvent()
    data class CopyBookmarkLink(val url: String) : CollectionDetailEvent()
}
