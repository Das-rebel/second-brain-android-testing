package com.secondbrain.app.data.repository

import com.secondbrain.app.data.database.dao.BookmarkDao
import com.secondbrain.app.data.database.dao.CollectionDao
import com.secondbrain.app.data.database.entities.BookmarkEntity
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.util.WebMetadataExtractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
/**
 * Repository for bookmark operations.
 */
class BookmarkRepository(
    private val bookmarkDao: BookmarkDao,
    private val collectionDao: CollectionDao,
    private val webMetadataExtractor: WebMetadataExtractor
) {
    
    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { entities ->
            entities.map { it.toBookmark() }
        }
    }
    
    fun getBookmarksByCollection(collectionId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksByCollection(collectionId).map { entities ->
            entities.map { it.toBookmark() }
        }
    }
    
    fun getFavoriteBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getFavoriteBookmarks().map { entities ->
            entities.map { it.toBookmark() }
        }
    }
    
    fun getArchivedBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getArchivedBookmarks().map { entities ->
            entities.map { it.toBookmark() }
        }
    }
    
    suspend fun getBookmarkById(id: Long): Bookmark? {
        return bookmarkDao.getBookmarkById(id)?.toBookmark()
    }
    
    suspend fun getBookmarkByUrl(url: String): Bookmark? {
        return bookmarkDao.getBookmarkByUrl(url)?.toBookmark()
    }
    
    fun searchBookmarks(query: String): Flow<List<Bookmark>> {
        return bookmarkDao.searchBookmarks(query).map { entities ->
            entities.map { it.toBookmark() }
        }
    }
    
    fun getBookmarksByTag(tag: String): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksByTag(tag).map { entities ->
            entities.map { it.toBookmark() }
        }
    }
    
    suspend fun insertBookmark(bookmark: Bookmark): Long {
        // If this is a new bookmark (id = 0), try to fetch metadata
        val bookmarkToSave = if (bookmark.id == 0L && bookmark.url.isNotBlank()) {
            try {
                val metadata = webMetadataExtractor.extractMetadata(bookmark.url)
                bookmark.copy(
                    title = bookmark.title.ifBlank { metadata.title },
                    description = bookmark.description ?: metadata.description.ifEmpty { null },
                    domain = bookmark.domain ?: metadata.domain,
                    faviconUrl = bookmark.faviconUrl ?: metadata.faviconUrl
                )
            } catch (e: Exception) {
                // If metadata extraction fails, use the original bookmark
                bookmark
            }
        } else {
            bookmark
        }
        val entity = BookmarkEntity.fromBookmark(bookmarkToSave)
        val id = bookmarkDao.insertBookmark(entity)
        
        // Update collection bookmark count if bookmark belongs to a collection
        bookmark.collectionId?.let { collectionId ->
            collectionDao.refreshBookmarkCount(collectionId)
        }
        
        return id
    }
    
    suspend fun updateBookmark(bookmark: Bookmark) {
        val entity = BookmarkEntity.fromBookmark(bookmark)
        bookmarkDao.updateBookmark(entity)
        
        // Update collection bookmark count if bookmark belongs to a collection
        bookmark.collectionId?.let { collectionId ->
            collectionDao.refreshBookmarkCount(collectionId)
        }
    }
    
    suspend fun deleteBookmark(bookmark: Bookmark) {
        val entity = BookmarkEntity.fromBookmark(bookmark)
        bookmarkDao.deleteBookmark(entity)
        
        // Update collection bookmark count if bookmark belonged to a collection
        bookmark.collectionId?.let { collectionId ->
            collectionDao.refreshBookmarkCount(collectionId)
        }
    }
    
    suspend fun deleteBookmarkById(id: Long) {
        val bookmark = getBookmarkById(id)
        bookmarkDao.deleteBookmarkById(id)
        
        // Update collection bookmark count if bookmark belonged to a collection
        bookmark?.collectionId?.let { collectionId ->
            collectionDao.refreshBookmarkCount(collectionId)
        }
    }
    
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) {
        bookmarkDao.updateFavoriteStatus(id, isFavorite)
    }
    
    suspend fun updateArchivedStatus(id: Long, isArchived: Boolean) {
        bookmarkDao.updateArchivedStatus(id, isArchived)
    }
    
    suspend fun incrementOpenCount(id: Long) {
        bookmarkDao.incrementOpenCount(id)
    }
    
    suspend fun moveBookmarksToCollection(bookmarkIds: List<Long>, collectionId: Long?) {
        // Get old collection IDs for updating counts
        val oldCollectionIds = bookmarkIds.mapNotNull { id ->
            getBookmarkById(id)?.collectionId
        }.distinct()
        
        bookmarkDao.moveBookmarksToCollection(bookmarkIds, collectionId)
        
        // Update bookmark counts for affected collections
        oldCollectionIds.forEach { oldCollectionId ->
            collectionDao.refreshBookmarkCount(oldCollectionId)
        }
        collectionId?.let { newCollectionId ->
            collectionDao.refreshBookmarkCount(newCollectionId)
        }
    }
    
    suspend fun getBookmarkCount(): Int {
        return bookmarkDao.getBookmarkCount()
    }
    
    suspend fun getFavoriteBookmarkCount(): Int {
        return bookmarkDao.getFavoriteBookmarkCount()
    }
    
    suspend fun getArchivedBookmarkCount(): Int {
        return bookmarkDao.getArchivedBookmarkCount()
    }
    
    suspend fun getAllTags(): List<String> {
        return bookmarkDao.getAllTags().flatMap { tagString ->
            tagString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }.distinct().sorted()
    }
}