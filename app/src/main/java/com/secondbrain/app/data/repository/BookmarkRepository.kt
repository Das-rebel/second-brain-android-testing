package com.secondbrain.app.data.repository

import com.secondbrain.app.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing bookmarks.
 */
interface BookmarkRepository {
    
    //region Local operations
    
    /**
     * Gets all bookmarks for a specific collection.
     */
    fun getBookmarksByCollection(collectionId: Long): Flow<List<Bookmark>>
    
    /**
     * Gets a bookmark by its ID.
     */
    suspend fun getBookmarkById(bookmarkId: Long): Result<Bookmark>
    
    /**
     * Inserts a new bookmark.
     */
    suspend fun insertBookmark(bookmark: Bookmark): Result<Long>
    
    /**
     * Updates an existing bookmark.
     */
    suspend fun updateBookmark(bookmark: Bookmark): Result<Unit>
    
    /**
     * Deletes a bookmark.
     */
    suspend fun deleteBookmark(bookmarkId: Long): Result<Unit>
    
    /**
     * Marks a bookmark as favorite.
     */
    suspend fun toggleFavorite(bookmarkId: Long, isFavorite: Boolean): Result<Unit>
    
    /**
     * Archives a bookmark.
     */
    suspend fun archiveBookmark(bookmarkId: Long, isArchived: Boolean): Result<Unit>
    
    /**
     * Updates the last opened timestamp for a bookmark.
     */
    suspend fun updateLastOpened(bookmarkId: Long): Result<Unit>
    
    /**
     * Searches bookmarks by query.
     */
    fun searchBookmarks(query: String): Flow<List<Bookmark>>
    
    //endregion
    
    /**
     * Gets all bookmarks from all collections.
     */
    fun getAllBookmarks(): Flow<List<Bookmark>>
    
    /**
     * Gets favorite bookmarks.
     */
    fun getFavoriteBookmarks(): Flow<List<Bookmark>>
    
    /**
     * Gets archived bookmarks.
     */
    fun getArchivedBookmarks(): Flow<List<Bookmark>>
    
    /**
     * Deletes multiple bookmarks.
     */
    suspend fun deleteBookmarks(bookmarkIds: List<Long>): Result<Unit>
    
    /**
     * Toggles favorite status for multiple bookmarks.
     */
    suspend fun toggleFavoriteForMultiple(bookmarkIds: List<Long>, isFavorite: Boolean): Result<Unit>
    
    /**
     * Archives/unarchives multiple bookmarks.
     */
    suspend fun archiveMultipleBookmarks(bookmarkIds: List<Long>, isArchived: Boolean): Result<Unit>
    
    //region Remote operations
    
    /**
     * Refreshes bookmarks for a collection from the remote server.
     */
    suspend fun refreshBookmarks(collectionId: Long)
    
    /**
     * Syncs local bookmarks with the remote server.
     */
    suspend fun syncBookmarks(collectionId: Long)
    
    //endregion
}
