package com.secondbrain.app.data.repository

import com.secondbrain.app.data.database.dao.BookmarkDao
import com.secondbrain.app.data.database.dao.CollectionDao
import com.secondbrain.app.data.database.entities.CollectionEntity
import com.secondbrain.app.data.model.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
/**
 * Repository for collection operations.
 */
class CollectionRepository(
    private val collectionDao: CollectionDao,
    private val bookmarkDao: BookmarkDao
) {
    
    fun getAllCollections(): Flow<List<Collection>> {
        return collectionDao.getAllCollections().map { entities ->
            entities.map { it.toCollection() }
        }
    }
    
    suspend fun getCollectionById(id: Long): Collection? {
        return collectionDao.getCollectionById(id)?.toCollection()
    }
    
    suspend fun getCollectionByName(name: String): Collection? {
        return collectionDao.getCollectionByName(name)?.toCollection()
    }
    
    suspend fun getDefaultCollection(): Collection? {
        return collectionDao.getDefaultCollection()?.toCollection()
    }
    
    fun searchCollections(query: String): Flow<List<Collection>> {
        return collectionDao.searchCollections(query).map { entities ->
            entities.map { it.toCollection() }
        }
    }
    
    suspend fun insertCollection(collection: Collection): Long {
        val entity = CollectionEntity.fromCollection(collection)
        return collectionDao.insertCollection(entity)
    }
    
    suspend fun updateCollection(collection: Collection) {
        val entity = CollectionEntity.fromCollection(collection)
        collectionDao.updateCollection(entity)
    }
    
    suspend fun deleteCollection(collection: Collection) {
        // First delete all bookmarks in this collection
        bookmarkDao.deleteBookmarksByCollection(collection.id)
        
        // Then delete the collection
        val entity = CollectionEntity.fromCollection(collection)
        collectionDao.deleteCollection(entity)
    }
    
    suspend fun deleteCollectionById(id: Long) {
        // First delete all bookmarks in this collection
        bookmarkDao.deleteBookmarksByCollection(id)
        
        // Then delete the collection
        collectionDao.deleteCollectionById(id)
    }
    
    suspend fun refreshBookmarkCount(id: Long) {
        collectionDao.refreshBookmarkCount(id)
    }
    
    suspend fun refreshAllBookmarkCounts() {
        collectionDao.refreshAllBookmarkCounts()
    }
    
    suspend fun getCollectionCount(): Int {
        return collectionDao.getCollectionCount()
    }
    
    suspend fun getBookmarkCountByCollection(collectionId: Long): Int {
        return bookmarkDao.getBookmarkCountByCollection(collectionId)
    }
}