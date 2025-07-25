package com.secondbrain.app.ui.collection.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.app.data.model.BookmarkCollection
import com.secondbrain.app.data.repository.CollectionRepository
import com.secondbrain.app.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionListViewModel @Inject constructor(
    private val repository: CollectionRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _state = MutableStateFlow(CollectionListState())
    val state: StateFlow<CollectionListState> = _state.asStateFlow()
    
    private val _events = MutableSharedFlow<CollectionListEvent>()
    val events = _events.asSharedFlow()
    
    init {
        loadCollections()
        observeCollections()
    }
    
    fun handleAction(action: CollectionListAction) {
        when (action) {
            is CollectionListAction.Refresh -> refreshCollections()
            is CollectionListAction.Search -> onSearchQueryChanged(action.query)
            is CollectionListAction.CollectionClick -> onCollectionClick(action.collection)
            is CollectionListAction.CreateCollection -> onCreateCollection()
            is CollectionListAction.DeleteCollection -> onDeleteCollection(action.collection)
            is CollectionListAction.ShareCollection -> onShareCollection(action.collection)
            is CollectionListAction.FollowCollection -> onFollowCollection(action.shareUrl)
            is CollectionListAction.UnfollowCollection -> onUnfollowCollection(action.collectionId)
        }
    }
    
    private fun loadCollections() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.getCollections()
                    .catch { e ->
                        _state.update { it.copy(
                            error = errorHandler.handleError(e),
                            isLoading = false
                        )}
                    }
                    .collect { collections ->
                        _state.update { it.copy(
                            collections = collections,
                            isLoading = false,
                            error = null
                        )}
                    }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = errorHandler.handleError(e),
                    isLoading = false
                )}
            }
        }
    }
    
    private fun observeCollections() {
        viewModelScope.launch {
            repository.getCollections()
                .catch { e ->
                    _state.update { it.copy(error = errorHandler.handleError(e)) }
                }
                .collect { collections ->
                    _state.update { it.copy(collections = collections) }
                }
        }
    }
    
    private fun refreshCollections() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                // Refresh logic here
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
    
    private fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
    
    private fun onCollectionClick(collection: BookmarkCollection) {
        viewModelScope.launch {
            _events.emit(CollectionListEvent.NavigateToCollection(collection.id))
        }
    }
    
    private fun onCreateCollection() {
        viewModelScope.launch {
            _events.emit(CollectionListEvent.NavigateToCreateCollection)
        }
    }
    
    private fun onDeleteCollection(collection: BookmarkCollection) {
        viewModelScope.launch {
            try {
                repository.deleteCollection(collection.id)
                _events.emit(CollectionListEvent.ShowMessage("Collection deleted"))
            } catch (e: Exception) {
                _events.emit(CollectionListEvent.ShowError(errorHandler.handleError(e)))
            }
        }
    }
    
    private fun onShareCollection(collection: BookmarkCollection) {
        viewModelScope.launch {
            _events.emit(CollectionListEvent.NavigateToShareCollection(collection.id))
        }
    }
    
    private fun onFollowCollection(shareUrl: String) {
        viewModelScope.launch {
            try {
                val collection = repository.followSharedCollection("current_user_id", shareUrl)
                collection.onSuccess {
                    _events.emit(CollectionListEvent.ShowMessage("Now following collection"))
                }.onFailure { e ->
                    _events.emit(CollectionListEvent.ShowError(errorHandler.handleError(e)))
                }
            } catch (e: Exception) {
                _events.emit(CollectionListEvent.ShowError(errorHandler.handleError(e)))
            }
        }
    }
    
    private fun onUnfollowCollection(collectionId: Long) {
        viewModelScope.launch {
            try {
                repository.unfollowSharedCollection("current_user_id", collectionId)
                _events.emit(CollectionListEvent.ShowMessage("Stopped following collection"))
            } catch (e: Exception) {
                _events.emit(CollectionListEvent.ShowError(errorHandler.handleError(e)))
            }
        }
    }
}

sealed class CollectionListAction {
    object Refresh : CollectionListAction()
    data class Search(val query: String) : CollectionListAction()
    data class CollectionClick(val collection: BookmarkCollection) : CollectionListAction()
    object CreateCollection : CollectionListAction()
    data class DeleteCollection(val collection: BookmarkCollection) : CollectionListAction()
    data class ShareCollection(val collection: BookmarkCollection) : CollectionListAction()
    data class FollowCollection(val shareUrl: String) : CollectionListAction()
    data class UnfollowCollection(val collectionId: Long) : CollectionListAction()
}

sealed class CollectionListEvent {
    data class NavigateToCollection(val collectionId: Long) : CollectionListEvent()
    object NavigateToCreateCollection : CollectionListEvent()
    data class NavigateToShareCollection(val collectionId: Long) : CollectionListEvent()
    data class ShowMessage(val message: String) : CollectionListEvent()
    data class ShowError(val message: String) : CollectionListEvent()
}
