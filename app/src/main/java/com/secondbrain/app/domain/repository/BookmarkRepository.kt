package com.secondbrain.app.domain.repository

import com.secondbrain.app.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for bookmark-related data operations.
 */
interface BookmarkRepository {
    
    /**
     * Get all bookmarks for a specific collection as a Flow.
     * @param collectionId The ID of the collection.
     * @return Flow emitting a list of bookmarks.
     */
    fun getBookmarksByCollection(collectionId: Long): Flow<List<Bookmark>>
    
    /**
     * Get a bookmark by its ID.
     * @param bookmarkId The ID of the bookmark.
     * @return Result containing the bookmark or an error.
     */
    suspend fun getBookmarkById(bookmarkId: Long): Result<Bookmark>
    
    /**
     * Insert a new bookmark.
     * @param bookmark The bookmark to insert.
     * @return Result containing the ID of the inserted bookmark or an error.
     */
    suspend fun insertBookmark(bookmark: Bookmark): Result<Long>
    
    /**
     * Update an existing bookmark.
     * @param bookmark The bookmark with updated values.
     * @return Result containing Unit on success or an error.
     */
    suspend fun updateBookmark(bookmark: Bookmark): Result<Unit>
    
    /**
     * Delete a bookmark by its ID.
     * @param bookmarkId The ID of the bookmark to delete.
     * @return Result containing Unit on success or an error.
     */
    suspend fun deleteBookmark(bookmarkId: Long): Result<Unit>
    
    /**
     * Toggle the favorite status of a bookmark.
     * @param bookmarkId The ID of the bookmark.
     * @param isFavorite The new favorite status.
     * @return Result containing Unit on success or an error.
     */
    suspend fun toggleFavorite(bookmarkId: Long, isFavorite: Boolean): Result<Unit>
    
    /**
     * Toggle the archive status of a bookmark.
     * @param bookmarkId The ID of the bookmark.
     * @param isArchived The new archive status.
     * @return Result containing Unit on success or an error.
     */
    suspend fun archiveBookmark(bookmarkId: Long, isArchived: Boolean): Result<Unit>
    
    /**
     * Update the last opened timestamp of a bookmark.
     * @param bookmarkId The ID of the bookmark.
     * @return Result containing Unit on success or an error.
     */
    suspend fun updateLastOpened(bookmarkId: Long): Result<Unit>
    
    /**
     * Search bookmarks by a query string.
     * @param query The search query.
     * @return Flow emitting a list of matching bookmarks.
     */
    fun searchBookmarks(query: String): Flow<List<Bookmark>>
    
    /**
     * Refresh bookmarks for a specific collection from the remote data source.
     * @param collectionId The ID of the collection to refresh.
     */
    suspend fun refreshBookmarks(collectionId: Long)
    
    /**
     * Get all bookmarks from all collections as a Flow.
     * @return Flow emitting a list of all bookmarks.
     */
    fun getAllBookmarks(): Flow<List<Bookmark>>
    
    /**
     * Get favorite bookmarks as a Flow.
     * @return Flow emitting a list of favorite bookmarks.
     */
    fun getFavoriteBookmarks(): Flow<List<Bookmark>>
    
    /**
     * Get archived bookmarks as a Flow.
     * @return Flow emitting a list of archived bookmarks.
     */
    fun getArchivedBookmarks(): Flow<List<Bookmark>>
    
    /**
     * Delete multiple bookmarks by their IDs.
     * @param bookmarkIds The list of bookmark IDs to delete.
     * @return Result containing Unit on success or an error.
     */
    suspend fun deleteBookmarks(bookmarkIds: List<Long>): Result<Unit>
    
    /**
     * Toggle favorite status for multiple bookmarks.
     * @param bookmarkIds The list of bookmark IDs to update.
     * @param isFavorite The new favorite status.
     * @return Result containing Unit on success or an error.
     */
    suspend fun toggleFavoriteForMultiple(bookmarkIds: List<Long>, isFavorite: Boolean): Result<Unit>
    
    /**
     * Archive/unarchive multiple bookmarks.
     * @param bookmarkIds The list of bookmark IDs to update.
     * @param isArchived The new archive status.
     * @return Result containing Unit on success or an error.
     */
    suspend fun archiveMultipleBookmarks(bookmarkIds: List<Long>, isArchived: Boolean): Result<Unit>
    
    /**
     * Sync local bookmarks with the remote data source for a specific collection.
     * @param collectionId The ID of the collection to sync.
     */
    suspend fun syncBookmarks(collectionId: Long)
}
