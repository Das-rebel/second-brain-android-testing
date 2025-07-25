package com.secondbrain.app.data.local.dao

import androidx.room.*
import com.secondbrain.app.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Data Access Object for the bookmarks table.
 */
@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE collectionId = :collectionId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun observeBookmarksByCollection(collectionId: Long): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :bookmarkId AND isDeleted = 0 LIMIT 1")
    suspend fun getBookmarkById(bookmarkId: Long): BookmarkEntity?
    
    @Query("SELECT * FROM bookmarks WHERE url = :url AND isDeleted = 0 LIMIT 1")
    suspend fun getBookmarkByUrl(url: String): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)

    @Query("UPDATE bookmarks SET isDeleted = 1, updatedAt = :timestamp WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: Long, timestamp: Date = Date())

    @Query("UPDATE bookmarks SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id = :bookmarkId")
    suspend fun updateFavoriteStatus(bookmarkId: Long, isFavorite: Boolean, timestamp: Date = Date())

    @Query("UPDATE bookmarks SET isArchived = :isArchived, updatedAt = :timestamp WHERE id = :bookmarkId")
    suspend fun updateArchiveStatus(bookmarkId: Long, isArchived: Boolean, timestamp: Date = Date())

    @Query("UPDATE bookmarks SET lastOpened = :timestamp, openCount = openCount + 1, updatedAt = :timestamp WHERE id = :bookmarkId")
    suspend fun updateLastOpened(bookmarkId: Long, timestamp: Date = Date())

    @Query("SELECT * FROM bookmarks WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND isDeleted = 0 ORDER BY createdAt DESC")
    fun searchBookmarks(query: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedBookmarks(): List<BookmarkEntity>

    @Query("SELECT * FROM bookmarks WHERE isDeleted = 1 AND isSynced = 0")
    suspend fun getDeletedBookmarks(): List<BookmarkEntity>
    
    @Query("SELECT * FROM bookmarks WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun observeAllBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE isFavorite = 1 AND isDeleted = 0 ORDER BY createdAt DESC")
    fun observeFavoriteBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE isArchived = 1 AND isDeleted = 0 ORDER BY createdAt DESC")
    fun observeArchivedBookmarks(): Flow<List<BookmarkEntity>>

    @Query("UPDATE bookmarks SET isSynced = 1, updatedAt = :timestamp WHERE id = :bookmarkId")
    suspend fun markAsSynced(bookmarkId: Long, timestamp: Date = Date())
    
    @Query("UPDATE bookmarks SET id = :newId, updatedAt = :timestamp WHERE id = :oldId")
    suspend fun updateLocalId(oldId: Long, newId: Long, timestamp: Date = Date())
    
    @Query("UPDATE bookmarks SET isSynced = 1, updatedAt = :timestamp WHERE collectionId = :collectionId")
    suspend fun markAllAsSynced(collectionId: Long, timestamp: Date = Date())
    
    @Query("SELECT * FROM bookmarks WHERE (isFavorite != serverIsFavorite OR isArchived != serverIsArchived) AND isSynced = 1 AND isDeleted = 0")
    suspend fun getModifiedBookmarks(): List<BookmarkEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<BookmarkEntity>)
    
    @Query("UPDATE bookmarks SET isDeleted = 1, updatedAt = :timestamp WHERE id IN (:bookmarkIds)")
    suspend fun deleteBookmarks(bookmarkIds: List<Long>, timestamp: Date = Date())
    
    @Query("UPDATE bookmarks SET isDeleted = 1, updatedAt = :timestamp WHERE id IN (:bookmarkIds)")
    suspend fun markBookmarksAsDeleted(bookmarkIds: List<Long>, timestamp: Date = Date())
    
    @Query("UPDATE bookmarks SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id IN (:bookmarkIds)")
    suspend fun updateFavoriteStatusForMultiple(bookmarkIds: List<Long>, isFavorite: Boolean, timestamp: Date = Date())
    
    @Query("UPDATE bookmarks SET isArchived = :isArchived, updatedAt = :timestamp WHERE id IN (:bookmarkIds)")
    suspend fun updateArchiveStatusForMultiple(bookmarkIds: List<Long>, isArchived: Boolean, timestamp: Date = Date())
    
    @Query("UPDATE bookmarks SET isSynced = 1, updatedAt = :timestamp WHERE id = :bookmarkId")
    suspend fun markBookmarkAsSynced(bookmarkId: Long, timestamp: Date = Date())
    
    @Transaction
    suspend fun runInTransaction(action: suspend () -> Unit) {
        action()
    }
}
