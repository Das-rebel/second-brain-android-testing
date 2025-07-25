package com.secondbrain.app.domain.usecase

import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.BookmarkFilter
import com.secondbrain.app.domain.model.BookmarkSortBy
import com.secondbrain.app.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import java.util.*
import javax.inject.Inject

/**
 * Contains all the use cases related to bookmarks.
 * Each use case encapsulates business logic and validation.
 */
class BookmarkUseCases @Inject constructor(
    private val repository: BookmarkRepository,
    private val validationUseCase: BookmarkValidationUseCase
) {
    
    //region Helper methods
    
    /**
     * Applies sorting to a list of bookmarks.
     */
    private fun List<Bookmark>.applySorting(sortBy: BookmarkSortBy): List<Bookmark> {
        return when (sortBy) {
            BookmarkSortBy.CREATED_DATE_DESC -> sortedByDescending { it.createdAt }
            BookmarkSortBy.CREATED_DATE_ASC -> sortedBy { it.createdAt }
            BookmarkSortBy.UPDATED_DATE_DESC -> sortedByDescending { it.updatedAt }
            BookmarkSortBy.UPDATED_DATE_ASC -> sortedBy { it.updatedAt }
            BookmarkSortBy.LAST_OPENED_DESC -> sortedByDescending { it.lastOpened ?: Date(0) }
            BookmarkSortBy.LAST_OPENED_ASC -> sortedBy { it.lastOpened ?: Date(0) }
            BookmarkSortBy.TITLE_ASC -> sortedBy { it.title.lowercase() }
            BookmarkSortBy.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
            BookmarkSortBy.OPEN_COUNT_DESC -> sortedByDescending { it.openCount }
            BookmarkSortBy.OPEN_COUNT_ASC -> sortedBy { it.openCount }
            BookmarkSortBy.DOMAIN_ASC -> sortedBy { it.domain.lowercase() }
        }
    }
    
    /**
     * Applies text search to a list of bookmarks.
     */
    private fun List<Bookmark>.applySearch(query: String): List<Bookmark> {
        val lowercaseQuery = query.lowercase()
        return filter { bookmark ->
            bookmark.title.lowercase().contains(lowercaseQuery) ||
            bookmark.description?.lowercase()?.contains(lowercaseQuery) == true ||
            bookmark.url.lowercase().contains(lowercaseQuery) ||
            bookmark.domain.lowercase().contains(lowercaseQuery) ||
            bookmark.tags.any { tag -> tag.lowercase().contains(lowercaseQuery) }
        }
    }
    
    /**
     * Applies tag filtering to a list of bookmarks.
     */
    private fun List<Bookmark>.applyTagFilter(tags: List<String>): List<Bookmark> {
        if (tags.isEmpty()) return this
        val lowercaseTags = tags.map { it.lowercase() }
        return filter { bookmark ->
            bookmark.tags.any { bookmarkTag ->
                lowercaseTags.any { filterTag ->
                    bookmarkTag.lowercase().contains(filterTag)
                }
            }
        }
    }
    
    //endregion
    /**
     * Use case for getting all bookmarks from all collections.
     */
    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return repository.getAllBookmarks()
            .catch { emit(emptyList()) }
    }
    
    /**
     * Use case for getting bookmarks by collection ID with validation.
     */
    fun getBookmarksByCollection(collectionId: Long): Flow<List<Bookmark>> {
        return if (validationUseCase.validateCollectionId(collectionId).isValid) {
            repository.getBookmarksByCollection(collectionId)
                .catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }
    
    /**
     * Use case for getting favorite bookmarks.
     */
    fun getFavoriteBookmarks(): Flow<List<Bookmark>> {
        return repository.getFavoriteBookmarks()
            .catch { emit(emptyList()) }
    }
    
    /**
     * Use case for getting archived bookmarks.
     */
    fun getArchivedBookmarks(): Flow<List<Bookmark>> {
        return repository.getArchivedBookmarks()
            .catch { emit(emptyList()) }
    }
    
    /**
     * Use case for getting a bookmark by ID with validation.
     */
    suspend fun getBookmarkById(bookmarkId: Long): Result<Bookmark> {
        val validation = validationUseCase.validateBookmarkId(bookmarkId)
        return if (validation.isValid) {
            repository.getBookmarkById(bookmarkId)
        } else {
            Result.failure(IllegalArgumentException(validation.firstErrorMessage ?: "Invalid bookmark ID"))
        }
    }
    
    /**
     * Alias for getBookmarkById for backward compatibility.
     */
    suspend fun getBookmark(bookmarkId: Long): Result<Bookmark> {
        return getBookmarkById(bookmarkId)
    }
    
    /**
     * Use case for adding a new bookmark with validation.
     */
    suspend fun addBookmark(bookmark: Bookmark): Result<Long> {
        val validation = validationUseCase.validateForUpdate(bookmark)
        return if (validation.isValid) {
            val sanitizedData = validationUseCase.sanitizeBookmarkData(
                bookmark.title,
                bookmark.url,
                bookmark.description,
                bookmark.tags
            )
            val sanitizedBookmark = bookmark.copy(
                title = sanitizedData.title,
                url = sanitizedData.url,
                description = sanitizedData.description,
                tags = sanitizedData.tags,
                createdAt = Date(),
                updatedAt = Date()
            )
            repository.insertBookmark(sanitizedBookmark)
        } else {
            Result.failure(IllegalArgumentException(validation.allErrorMessages))
        }
    }
    
    /**
     * Use case for creating a bookmark with URL validation and metadata extraction.
     */
    suspend fun createBookmark(
        collectionId: Long,
        title: String,
        url: String,
        description: String? = null,
        tags: List<String> = emptyList()
    ): Result<Long> {
        val validation = validationUseCase.validateForCreation(collectionId, title, url, description, tags)
        return if (validation.isValid) {
            val sanitizedData = validationUseCase.sanitizeBookmarkData(title, url, description, tags)
            val bookmark = Bookmark.create(
                collectionId = collectionId,
                title = sanitizedData.title,
                url = sanitizedData.url,
                description = sanitizedData.description,
                tags = sanitizedData.tags
            )
            repository.insertBookmark(bookmark)
        } else {
            Result.failure(IllegalArgumentException(validation.allErrorMessages))
        }
    }
    
    /**
     * Use case for updating an existing bookmark with validation.
     */
    suspend fun updateBookmark(bookmark: Bookmark): Result<Unit> {
        val validation = validationUseCase.validateForUpdate(bookmark)
        return if (validation.isValid) {
            val sanitizedData = validationUseCase.sanitizeBookmarkData(
                bookmark.title,
                bookmark.url,
                bookmark.description,
                bookmark.tags
            )
            val sanitizedBookmark = bookmark.copy(
                title = sanitizedData.title,
                url = sanitizedData.url,
                description = sanitizedData.description,
                tags = sanitizedData.tags,
                updatedAt = Date()
            )
            repository.updateBookmark(sanitizedBookmark)
        } else {
            Result.failure(IllegalArgumentException(validation.allErrorMessages))
        }
    }
    
    /**
     * Use case for deleting a bookmark with validation.
     */
    suspend fun deleteBookmark(bookmarkId: Long): Result<Unit> {
        val validation = validationUseCase.validateBookmarkId(bookmarkId)
        return if (validation.isValid) {
            repository.deleteBookmark(bookmarkId)
        } else {
            Result.failure(IllegalArgumentException(validation.firstErrorMessage ?: "Invalid bookmark ID"))
        }
    }
    
    /**
     * Use case for deleting multiple bookmarks with validation.
     */
    suspend fun deleteMultipleBookmarks(bookmarkIds: List<Long>): Result<Unit> {
        val validation = validationUseCase.validateBookmarkIds(bookmarkIds)
        return if (validation.isValid) {
            repository.deleteBookmarks(bookmarkIds)
        } else {
            Result.failure(IllegalArgumentException(validation.allErrorMessages))
        }
    }
    
    /**
     * Use case for toggling the favorite status of a bookmark with validation.
     */
    suspend fun toggleFavorite(bookmarkId: Long, isFavorite: Boolean): Result<Unit> {
        val validation = validationUseCase.validateBookmarkId(bookmarkId)
        return if (validation.isValid) {
            repository.toggleFavorite(bookmarkId, isFavorite)
        } else {
            Result.failure(IllegalArgumentException(validation.firstErrorMessage ?: "Invalid bookmark ID"))
        }
    }
    
    /**
     * Use case for toggling favorite status for multiple bookmarks.
     */
    suspend fun toggleFavoriteForMultiple(bookmarkIds: List<Long>, isFavorite: Boolean): Result<Unit> {
        val validation = validationUseCase.validateBookmarkIds(bookmarkIds)
        return if (validation.isValid) {
            repository.toggleFavoriteForMultiple(bookmarkIds, isFavorite)
        } else {
            Result.failure(IllegalArgumentException(validation.allErrorMessages))
        }
    }
    
    /**
     * Use case for archiving/unarchiving a bookmark with validation.
     */
    suspend fun archiveBookmark(bookmarkId: Long, isArchived: Boolean): Result<Unit> {
        return if (isValidBookmarkId(bookmarkId)) {
            repository.archiveBookmark(bookmarkId, isArchived)
        } else {
            Result.failure(IllegalArgumentException("Invalid bookmark ID: $bookmarkId"))
        }
    }
    
    /**
     * Use case for archiving/unarchiving multiple bookmarks.
     */
    suspend fun archiveMultipleBookmarks(bookmarkIds: List<Long>, isArchived: Boolean): Result<Unit> {
        return when {
            bookmarkIds.isEmpty() -> {
                Result.failure(IllegalArgumentException("No bookmark IDs provided"))
            }
            bookmarkIds.any { !isValidBookmarkId(it) } -> {
                val invalidIds = bookmarkIds.filter { !isValidBookmarkId(it) }
                Result.failure(IllegalArgumentException("Invalid bookmark IDs: $invalidIds"))
            }
            else -> {
                repository.archiveMultipleBookmarks(bookmarkIds, isArchived)
            }
        }
    }
    
    /**
     * Use case for updating the last opened timestamp of a bookmark with validation.
     */
    suspend fun updateLastOpened(bookmarkId: Long): Result<Unit> {
        return if (isValidBookmarkId(bookmarkId)) {
            repository.updateLastOpened(bookmarkId)
        } else {
            Result.failure(IllegalArgumentException("Invalid bookmark ID: $bookmarkId"))
        }
    }
    
    /**
     * Use case for searching bookmarks with query validation and sanitization.
     */
    fun searchBookmarks(query: String): Flow<List<Bookmark>> {
        val sanitizedQuery = sanitizeSearchQuery(query)
        return if (sanitizedQuery != null) {
            repository.searchBookmarks(sanitizedQuery)
                .catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }
    
    /**
     * Use case for filtering bookmarks based on comprehensive criteria.
     */
    fun getFilteredBookmarks(filter: BookmarkFilter): Flow<List<Bookmark>> {
        // Validate search query if provided
        val searchValidation = validationUseCase.validateSearchQuery(filter.searchQuery)
        if (searchValidation.isInvalid) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        
        // Get base bookmarks flow based on primary filter
        val baseFlow = when {
            filter.showFavorites -> getFavoriteBookmarks()
            filter.showArchived -> getArchivedBookmarks()
            filter.collectionId != null && validationUseCase.validateCollectionId(filter.collectionId).isValid -> 
                getBookmarksByCollection(filter.collectionId)
            else -> getAllBookmarks()
        }
        
        return baseFlow.map { bookmarks ->
            var filteredBookmarks = bookmarks
            
            // Apply search filter
            filter.searchQuery?.let { query ->
                if (query.isNotBlank()) {
                    filteredBookmarks = filteredBookmarks.applySearch(query)
                }
            }
            
            // Apply tag filter
            if (filter.tags.isNotEmpty()) {
                filteredBookmarks = filteredBookmarks.applyTagFilter(filter.tags)
            }
            
            // Apply sorting
            filteredBookmarks.applySorting(filter.sortBy)
        }.catch { emit(emptyList()) }
    }
    
    /**
     * Use case for filtering bookmarks based on individual criteria (legacy method).
     */
    fun getFilteredBookmarks(
        collectionId: Long? = null,
        showFavorites: Boolean = false,
        showArchived: Boolean = false,
        searchQuery: String? = null
    ): Flow<List<Bookmark>> {
        val filter = BookmarkFilter(
            collectionId = collectionId,
            showFavorites = showFavorites,
            showArchived = showArchived,
            searchQuery = searchQuery
        )
        return getFilteredBookmarks(filter)
    }
    
    /**
     * Use case for refreshing bookmarks from the remote data source with validation.
     */
    suspend fun refreshBookmarks(collectionId: Long): Result<Unit> {
        val validation = validationUseCase.validateCollectionId(collectionId)
        return if (validation.isValid) {
            try {
                repository.refreshBookmarks(collectionId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalArgumentException(validation.firstErrorMessage ?: "Invalid collection ID"))
        }
    }
    
    /**
     * Use case for syncing local bookmarks with the remote data source.
     */
    suspend fun syncBookmarks(collectionId: Long): Result<Unit> {
        return if (isValidCollectionId(collectionId)) {
            try {
                repository.syncBookmarks(collectionId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalArgumentException("Invalid collection ID: $collectionId"))
        }
    }
    
    //region Analytics and Statistics
    
    /**
     * Use case for getting bookmark statistics.
     */
    fun getBookmarkStatistics(collectionId: Long? = null): Flow<BookmarkStatistics> {
        val bookmarksFlow = if (collectionId != null && isValidCollectionId(collectionId)) {
            getBookmarksByCollection(collectionId)
        } else {
            getAllBookmarks()
        }
        
        return bookmarksFlow.map { bookmarks ->
            BookmarkStatistics(
                totalCount = bookmarks.size,
                favoriteCount = bookmarks.count { it.isFavorite },
                archivedCount = bookmarks.count { it.isArchived },
                unsyncedCount = bookmarks.count { !it.isSynced },
                tagsCount = bookmarks.flatMap { it.tags }.distinct().size,
                mostRecentlyAdded = bookmarks.maxByOrNull { it.createdAt },
                mostRecentlyOpened = bookmarks.maxByOrNull { it.lastOpened ?: Date(0) },
                totalOpenCount = bookmarks.sumOf { it.openCount }
            )
        }
    }
    
    //endregion
    
    //region Helper validation methods
    
    /**
     * Checks if a bookmark ID is valid.
     */
    private fun isValidBookmarkId(bookmarkId: Long): Boolean {
        return bookmarkId > 0
    }
    
    /**
     * Checks if a collection ID is valid.
     */
    private fun isValidCollectionId(collectionId: Long): Boolean {
        return collectionId > 0
    }
    
    /**
     * Sanitizes a search query for safe use.
     */
    private fun sanitizeSearchQuery(query: String): String? {
        val trimmed = query.trim()
        return if (trimmed.length >= 2) {
            trimmed.replace(Regex("[<>\"']"), "")
        } else {
            null
        }
    }
    
    //endregion
}

/**
 * Data class representing bookmark statistics.
 */
data class BookmarkStatistics(
    val totalCount: Int,
    val favoriteCount: Int,
    val archivedCount: Int,
    val unsyncedCount: Int,
    val tagsCount: Int,
    val mostRecentlyAdded: Bookmark?,
    val mostRecentlyOpened: Bookmark?,
    val totalOpenCount: Int
)
