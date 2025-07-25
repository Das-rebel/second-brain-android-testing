package com.secondbrain.app.data.repository

import com.secondbrain.app.data.model.BookmarkCollection
import com.secondbrain.app.data.model.CollectionShareSettings
import com.secondbrain.app.data.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for collection-related operations including sharing functionality.
 */
interface CollectionRepository {
    // Existing methods
    suspend fun getCollections(): Flow<List<BookmarkCollection>>
    
    // Collection sharing methods
    
    /**
     * Updates the sharing settings for a collection.
     * 
     * @param collectionId The ID of the collection to update sharing settings for
     * @param shareSettings The new sharing settings
     * @return Result indicating success or failure
     */
    suspend fun updateSharingSettings(
        collectionId: Long,
        shareSettings: CollectionShareSettings
    ): Result<Unit>
    
    /**
     * Follows a shared collection using a share URL or ID.
     * 
     * @param userId The ID of the user who wants to follow the collection
     * @param shareUrlOrId The share URL or ID of the collection to follow
     * @return Result containing the followed collection or an error
     */
    suspend fun followSharedCollection(
        userId: String,
        shareUrlOrId: String
    ): Result<BookmarkCollection>
    
    /**
     * Unfollows a previously followed shared collection.
     * 
     * @param userId The ID of the user who wants to unfollow the collection
     * @param collectionId The ID of the shared collection to unfollow
     * @return Result indicating success or failure
     */
    suspend fun unfollowSharedCollection(
        userId: String,
        collectionId: Long
    ): Result<Unit>
    
    /**
     * Retrieves all collections that have been shared with the current user.
     * 
     * @param userId The ID of the current user
     * @return Flow emitting a list of shared collections
     */
    fun getSharedCollections(userId: String): Flow<List<BookmarkCollection>>
}
