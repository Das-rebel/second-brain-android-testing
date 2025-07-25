package com.secondbrain.app.data.repository

import com.secondbrain.app.data.local.dao.CollectionDao
import com.secondbrain.app.data.mapper.CollectionMapper
import com.secondbrain.app.data.model.BookmarkCollection
import com.secondbrain.app.data.model.CollectionShareSettings
import com.secondbrain.app.data.model.Result
import com.secondbrain.app.data.network.CollectionApiService
import com.secondbrain.app.data.network.NetworkConnectivityManager
import com.secondbrain.app.data.sync.SyncManager
import com.secondbrain.app.util.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionApiService: CollectionApiService,
    private val collectionMapper: CollectionMapper,
    private val networkConnectivityManager: NetworkConnectivityManager,
    private val syncManager: SyncManager,
    private val errorHandler: ErrorHandler
) : CollectionRepository {

    // Existing implementation
    override suspend fun getCollections(): Flow<List<BookmarkCollection>> {
        return collectionDao.observeAllCollections()
            .map { collections -> collections.map(collectionMapper::mapToDomain) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun updateSharingSettings(
        collectionId: Long,
        shareSettings: CollectionShareSettings
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Get the collection to verify it exists and is owned by the current user
            val collection = collectionDao.getCollectionById(collectionId)
                ?: return@withContext Result.Error(Exception("Collection not found"))

            // Update the collection with new sharing settings
            val updatedCollection = collection.copy(
                isShared = shareSettings.isShared,
                shareUrl = shareSettings.shareUrl,
                shareExpiry = shareSettings.expiryDate,
                updatedAt = Date()
            )

            // Save to local database
            collectionDao.updateCollection(updatedCollection)

            // Queue for sync
            syncManager.queueChange(
                entityType = "collection",
                entityId = collectionId.toString(),
                changeType = "update",
                changeData = collectionMapper.mapToNetwork(updatedCollection)
            )

            // If online, push changes to the server
            if (networkConnectivityManager.isConnected()) {
                collectionApiService.updateCollectionSharing(
                    collectionId,
                    collectionMapper.mapToSharingRequest(shareSettings)
                )
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(errorHandler.handleError(e))
        }
    }

    override suspend fun followSharedCollection(
        userId: String,
        shareUrlOrId: String
    ): Result<BookmarkCollection> = withContext(Dispatchers.IO) {
        return@withContext try {
            // If online, fetch the shared collection from the server
            if (networkConnectivityManager.isConnected()) {
                val response = collectionApiService.followSharedCollection(shareUrlOrId)
                val collection = collectionMapper.mapFromNetwork(response)
                
                // Save to local database
                collectionDao.insertCollection(collectionMapper.mapToEntity(collection))
                
                Result.Success(collection)
            } else {
                // If offline, check if we have a local copy
                val localCollection = collectionDao.getCollectionByShareUrl(shareUrlOrId)
                if (localCollection != null) {
                    Result.Success(collectionMapper.mapToDomain(localCollection))
                } else {
                    Result.Error(Exception("No network connection and no local copy available"))
                }
            }
        } catch (e: Exception) {
            Result.Error(errorHandler.handleError(e))
        }
    }

    override suspend fun unfollowSharedCollection(
        userId: String,
        collectionId: Long
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Mark as unfollowed in local database
            collectionDao.deleteCollection(collectionId)
            
            // Queue for sync
            syncManager.queueChange(
                entityType = "collection_follow",
                entityId = "${userId}_$collectionId",
                changeType = "delete",
                changeData = mapOf(
                    "userId" to userId,
                    "collectionId" to collectionId
                )
            )
            
            // If online, notify the server
            if (networkConnectivityManager.isConnected()) {
                collectionApiService.unfollowSharedCollection(collectionId)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(errorHandler.handleError(e))
        }
    }

    override fun getSharedCollections(userId: String): Flow<List<BookmarkCollection>> {
        return collectionDao.observeSharedCollections(userId)
            .map { collections -> collections.map(collectionMapper::mapToDomain) }
            .flowOn(Dispatchers.IO)
    }
}
