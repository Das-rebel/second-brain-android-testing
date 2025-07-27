package com.secondbrain.app.data.database.dao

import androidx.room.*
import com.secondbrain.app.data.database.entities.CollectionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Collection operations.
 */
@Dao
interface CollectionDao {
    
    @Query("SELECT * FROM collections ORDER BY created_at DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>
    
    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: Long): CollectionEntity?
    
    @Query("SELECT * FROM collections WHERE name = :name LIMIT 1")
    suspend fun getCollectionByName(name: String): CollectionEntity?
    
    @Query("SELECT * FROM collections WHERE is_default = 1 LIMIT 1")
    suspend fun getDefaultCollection(): CollectionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CollectionEntity>)
    
    @Update
    suspend fun updateCollection(collection: CollectionEntity)
    
    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)
    
    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollectionById(id: Long)
    
    @Query("UPDATE collections SET bookmark_count = :count WHERE id = :id")
    suspend fun updateBookmarkCount(id: Long, count: Int)
    
    @Query("""
        UPDATE collections 
        SET bookmark_count = (
            SELECT COUNT(*) FROM bookmarks WHERE collection_id = collections.id
        )
        WHERE id = :id
    """)
    suspend fun refreshBookmarkCount(id: Long)
    
    @Query("""
        UPDATE collections 
        SET bookmark_count = (
            SELECT COUNT(*) FROM bookmarks WHERE collection_id = collections.id
        )
    """)
    suspend fun refreshAllBookmarkCounts()
    
    @Query("SELECT COUNT(*) FROM collections")
    suspend fun getCollectionCount(): Int
    
    @Query("SELECT * FROM collections WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchCollections(query: String): Flow<List<CollectionEntity>>
}