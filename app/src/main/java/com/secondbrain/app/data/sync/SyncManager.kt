package com.secondbrain.app.data.sync

import com.secondbrain.app.data.model.ChangeType
import kotlinx.coroutines.flow.Flow

/**
 * Manages synchronization of local changes with the server.
 */
interface SyncManager {
    
    /**
     * Queues a change to be synchronized with the server.
     * 
     * @param userId The ID of the user who made the change
     * @param entityType The type of entity being changed (e.g., "collection", "bookmark")
     * @param entityId The ID of the entity being changed
     * @param changeType The type of change (CREATE, UPDATE, DELETE)
     * @param changeData The data associated with the change
     */
    fun queueChange(
        userId: String,
        entityType: String,
        entityId: String,
        changeType: ChangeType,
        changeData: Any
    )
    
    /**
     * Observes the list of pending changes that need to be synchronized.
     */
    fun observePendingChanges(): Flow<List<SyncChange>>
    
    /**
     * Retries any failed synchronization attempts.
     */
    suspend fun retryFailedChanges()
    
    /**
     * Clears all pending changes from the queue.
     */
    suspend fun clearPendingChanges()
}
