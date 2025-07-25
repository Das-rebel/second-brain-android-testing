package com.secondbrain.app.ui.bookmark

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.usecase.BookmarkUseCases
import com.secondbrain.app.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the BookmarkDetail screen.
 */
data class BookmarkDetailUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val isBookmarkDeleted: Boolean = false,
    val error: String? = null,
    val bookmark: Bookmark? = null,
    val editableTitle: String = "",
    val editableUrl: String = "",
    val editableDescription: String? = null,
    val editableTags: List<String> = emptyList()
)

/**
 * ViewModel for managing bookmark detail screen state and business logic.
 */
@HiltViewModel
class BookmarkDetailViewModel @Inject constructor(
    private val bookmarkUseCases: BookmarkUseCases,
    private val errorHandler: ErrorHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarkDetailUiState())
    val uiState: StateFlow<BookmarkDetailUiState> = _uiState.asStateFlow()

    private var currentBookmark: Bookmark? = null

    init {
        // Load bookmark from saved state if available
        savedStateHandle.get<Long>("bookmarkId")?.let { bookmarkId ->
            loadBookmark(bookmarkId)
        }
    }

    /**
     * Load a bookmark by ID.
     */
    fun loadBookmark(bookmarkId: Long) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val result = bookmarkUseCases.getBookmarkById(bookmarkId)
                result.onSuccess { bookmark ->
                    currentBookmark = bookmark
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookmark = bookmark,
                            editableTitle = bookmark.title,
                            editableUrl = bookmark.url,
                            editableDescription = bookmark.description,
                            editableTags = bookmark.tags
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = errorHandler.getUserFriendlyErrorMessage(exception)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = errorHandler.getUserFriendlyErrorMessage(e)
                    )
                }
            }
        }
    }

    /**
     * Toggle edit mode.
     */
    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    /**
     * Update the editable title.
     */
    fun updateTitle(title: String) {
        _uiState.update { it.copy(editableTitle = title) }
    }

    /**
     * Update the editable URL.
     */
    fun updateUrl(url: String) {
        _uiState.update { it.copy(editableUrl = url) }
    }

    /**
     * Update the editable description.
     */
    fun updateDescription(description: String) {
        _uiState.update { it.copy(editableDescription = description.takeIf { it.isNotBlank() }) }
    }

    /**
     * Update the editable tags.
     */
    fun updateTags(tags: List<String>) {
        _uiState.update { it.copy(editableTags = tags) }
    }

    /**
     * Save the current bookmark.
     */
    fun saveBookmark() {
        val currentState = _uiState.value
        val currentBookmark = currentState.bookmark ?: return
        
        val updatedBookmark = currentBookmark.copy(
            title = currentState.editableTitle.trim(),
            url = currentState.editableUrl.trim(),
            description = currentState.editableDescription?.trim(),
            tags = currentState.editableTags,
            updatedAt = Date()
        )
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            try {
                val result = bookmarkUseCases.updateBookmark(updatedBookmark)
                result.onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isEditMode = false,
                            bookmark = updatedBookmark
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = errorHandler.getUserFriendlyErrorMessage(exception)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = errorHandler.getUserFriendlyErrorMessage(e)
                    )
                }
            }
        }
    }

    /**
     * Toggle the favorite status of the current bookmark.
     */
    fun toggleFavorite() {
        val currentBookmark = _uiState.value.bookmark ?: return
        
        viewModelScope.launch {
            try {
                val isFavorite = !currentBookmark.isFavorite
                val result = bookmarkUseCases.toggleFavorite(currentBookmark.id, isFavorite)
                
                result.onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            bookmark = currentBookmark.copy(
                                isFavorite = isFavorite,
                                updatedAt = Date()
                            )
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(error = errorHandler.getUserFriendlyErrorMessage(exception))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = errorHandler.getUserFriendlyErrorMessage(e))
                }
            }
        }
    }

    /**
     * Toggle the archive status of the current bookmark.
     */
    fun toggleArchive() {
        val currentBookmark = _uiState.value.bookmark ?: return
        
        viewModelScope.launch {
            try {
                val isArchived = !currentBookmark.isArchived
                val result = bookmarkUseCases.archiveBookmark(currentBookmark.id, isArchived)
                
                result.onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            bookmark = currentBookmark.copy(
                                isArchived = isArchived,
                                updatedAt = Date()
                            )
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(error = errorHandler.getUserFriendlyErrorMessage(exception))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = errorHandler.getUserFriendlyErrorMessage(e))
                }
            }
        }
    }

    /**
     * Show the delete confirmation dialog.
     */
    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    /**
     * Dismiss the delete confirmation dialog.
     */
    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete the current bookmark.
     */
    fun deleteBookmark() {
        val bookmarkId = _uiState.value.bookmark?.id ?: return
        
        viewModelScope.launch {
            try {
                val result = bookmarkUseCases.deleteBookmark(bookmarkId)
                result.onSuccess {
                    _uiState.update {
                        it.copy(
                            showDeleteConfirmation = false,
                            isBookmarkDeleted = true
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            showDeleteConfirmation = false,
                            error = errorHandler.getUserFriendlyErrorMessage(exception)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        showDeleteConfirmation = false,
                        error = errorHandler.getUserFriendlyErrorMessage(e)
                    )
                }
            }
        }
    }

    /**
     * Clear the current error.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
