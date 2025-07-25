package com.secondbrain.app.data.local.dao

import androidx.room.*
import com.secondbrain.app.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Data Access Object for the collections table.
 */
@Dao
interface CollectionDao {
    
    @Query("SELECT * FROM collections WHERE isDeleted = 0 ORDER BY sortOrder ASC, name ASC")
    fun observeAllCollections(): Flow<List<CollectionEntity>>
    
    @Query("SELECT * FROM collections WHERE id = :collectionId AND isDeleted = 0 LIMIT 1")
    suspend fun getCollectionById(collectionId: Long): CollectionEntity?
    
    @Query("SELECT * FROM collections WHERE id = :collectionId LIMIT 1")
    suspend fun getCollectionByIdIncludeDeleted(collectionId: Long): CollectionEntity?
    
    @Query("SELECT * FROM collections WHERE isShared = 1 AND isDeleted = 0 ORDER BY name ASC")
    fun observeSharedCollections(): Flow<List<CollectionEntity>>
    
    @Query("SELECT * FROM collections WHERE shareUrl = :shareUrl AND isDeleted = 0 LIMIT 1")
    suspend fun getCollectionByShareUrl(shareUrl: String): CollectionEntity?
    
    @Query("SELECT * FROM collections WHERE userId = :userId AND isDeleted = 0 ORDER BY sortOrder ASC, name ASC")
    fun observeCollectionsByUser(userId: String): Flow<List<CollectionEntity>>
    
    @Query("SELECT * FROM collections WHERE isDefault = 1 AND userId = :userId AND isDeleted = 0 LIMIT 1")
    suspend fun getDefaultCollection(userId: String): CollectionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CollectionEntity>)
    
    @Update
    suspend fun updateCollection(collection: CollectionEntity)
    
    @Query("UPDATE collections SET isDefault = 0 WHERE userId = :userId AND isDefault = 1")
    suspend fun clearDefaultCollections(userId: String)
    
    @Query("UPDATE collections SET sortOrder = :newOrder WHERE id = :collectionId")
    suspend fun updateCollectionOrder(collectionId: Long, newOrder: Int)
    
    @Query("UPDATE collections SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :collectionId")
    suspend fun markCollectionAsDeleted(collectionId: Long, deletedAt: Date = Date())
    
    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollection(collectionId: Long)
    
    @Query("DELETE FROM collections")
    suspend fun deleteAllCollections()
}
