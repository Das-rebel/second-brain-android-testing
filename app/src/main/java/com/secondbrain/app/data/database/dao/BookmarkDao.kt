package com.secondbrain.app.data.database.dao

import androidx.room.*
import com.secondbrain.app.data.database.entities.BookmarkEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Bookmark operations.
 */
@Dao
interface BookmarkDao {
    
    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE collection_id = :collectionId ORDER BY created_at DESC")
    fun getBookmarksByCollection(collectionId: Long): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE is_archived = 1 ORDER BY created_at DESC")
    fun getArchivedBookmarks(): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: Long): BookmarkEntity?
    
    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getBookmarkByUrl(url: String): BookmarkEntity?
    
    @Query("""
        SELECT * FROM bookmarks 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR tags LIKE '%' || :query || '%'
        ORDER BY created_at DESC
    """)
    fun searchBookmarks(query: String): Flow<List<BookmarkEntity>>
    
    @Query("SELECT DISTINCT tags FROM bookmarks WHERE tags != ''")
    suspend fun getAllTags(): List<String>
    
    @Query("SELECT * FROM bookmarks WHERE tags LIKE '%' || :tag || '%' ORDER BY created_at DESC")
    fun getBookmarksByTag(tag: String): Flow<List<BookmarkEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarks(bookmarks: List<BookmarkEntity>)
    
    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)
    
    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
    
    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
    
    @Query("DELETE FROM bookmarks WHERE collection_id = :collectionId")
    suspend fun deleteBookmarksByCollection(collectionId: Long)
    
    @Query("UPDATE bookmarks SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    
    @Query("UPDATE bookmarks SET is_archived = :isArchived WHERE id = :id")
    suspend fun updateArchivedStatus(id: Long, isArchived: Boolean)
    
    @Query("UPDATE bookmarks SET open_count = open_count + 1 WHERE id = :id")
    suspend fun incrementOpenCount(id: Long)
    
    @Query("UPDATE bookmarks SET collection_id = :collectionId WHERE id IN (:bookmarkIds)")
    suspend fun moveBookmarksToCollection(bookmarkIds: List<Long>, collectionId: Long?)
    
    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE collection_id = :collectionId")
    suspend fun getBookmarkCountByCollection(collectionId: Long): Int
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE is_favorite = 1")
    suspend fun getFavoriteBookmarkCount(): Int
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE is_archived = 1")
    suspend fun getArchivedBookmarkCount(): Int
}