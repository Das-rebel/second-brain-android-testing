package com.secondbrain.app.data.sync

import com.secondbrain.app.data.model.ChangeType
import com.secondbrain.app.data.network.CollectionApiService
import com.secondbrain.app.util.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManagerImpl @Inject constructor(
    private val collectionApiService: CollectionApiService,
    private val errorHandler: ErrorHandler
) : SyncManager {
    
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val pendingChanges = mutableListOf<SyncChange>()
    
    override fun queueChange(
        userId: String,
        entityType: String,
        entityId: String,
        changeType: ChangeType,
        changeData: Any
    ) {
        val change = SyncChange(
            userId = userId,
            entityType = entityType,
            entityId = entityId,
            changeType = changeType,
            changeData = changeData,
            timestamp = System.currentTimeMillis()
        )
        
        pendingChanges.add(change)
        processChanges()
    }
    
    private fun processChanges() {
        if (pendingChanges.isEmpty()) return
        
        ioScope.launch {
            val changesToProcess = pendingChanges.toList()
            pendingChanges.clear()
            
            try {
                // Process each change
                changesToProcess.forEach { change ->
                    when (change.entityType) {
                        "collection" -> processCollectionChange(change)
                        "collection_sharing" -> processSharingChange(change)
                        // Add other entity types as needed
                    }
                }
            } catch (e: Exception) {
                // Re-add failed changes to retry later
                pendingChanges.addAll(0, changesToProcess)
                errorHandler.logError("Sync failed: ${e.message}", e)
            }
        }
    }
    
    private suspend fun processCollectionChange(change: SyncChange) {
        when (change.changeType) {
            ChangeType.CREATE -> {
                // Handle collection creation
                val collection = change.changeData as? Map<*, *>
                    ?: throw IllegalArgumentException("Invalid collection data")
                collectionApiService.createCollection(collection)
            }
            ChangeType.UPDATE -> {
                // Handle collection update
                val collection = change.changeData as? Map<*, *>
                    ?: throw IllegalArgumentException("Invalid collection data")
                collectionApiService.updateCollection(change.entityId.toLong(), collection)
            }
            ChangeType.DELETE -> {
                // Handle collection deletion
                collectionApiService.deleteCollection(change.entityId.toLong())
            }
        }
    }
    
    private suspend fun processSharingChange(change: SyncChange) {
        // Handle sharing changes
        when (change.changeType) {
            ChangeType.CREATE, ChangeType.UPDATE -> {
                val settings = change.changeData as? Map<*, *>
                    ?: throw IllegalArgumentException("Invalid sharing settings")
                collectionApiService.updateCollectionSharing(
                    collectionId = change.entityId.toLong(),
                    settings = settings
                )
            }
            ChangeType.DELETE -> {
                collectionApiService.unfollowSharedCollection(change.entityId.toLong())
            }
        }
    }
    
    override fun observePendingChanges(): Flow<List<SyncChange>> {
        // Implement if you want to observe pending changes in the UI
        TODO("Not yet implemented")
    }
    
    override suspend fun retryFailedChanges() {
        processChanges()
    }
    
    override suspend fun clearPendingChanges() {
        pendingChanges.clear()
    }
}
