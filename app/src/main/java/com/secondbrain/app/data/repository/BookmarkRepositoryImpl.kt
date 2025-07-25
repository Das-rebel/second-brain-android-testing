package com.secondbrain.app.data.repository

import com.secondbrain.app.data.local.dao.BookmarkDao
import com.secondbrain.app.data.mapper.toBookmark
import com.secondbrain.app.data.mapper.toBookmarkEntity
import com.secondbrain.app.data.mapper.toBookmarkDto
import com.secondbrain.app.data.network.BookmarkApiService
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.util.ErrorHandler
import com.secondbrain.app.util.NetworkConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val apiService: BookmarkApiService,
    private val networkConnectivityManager: NetworkConnectivityManager,
    private val errorHandler: ErrorHandler
) : BookmarkRepository {

    override fun getBookmarksByCollection(collectionId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.observeBookmarksByCollection(collectionId)
            .map { entities -> entities.map { it.toBookmark() } }
            .onEach { bookmarks ->
                if (bookmarks.isEmpty() && networkConnectivityManager.isConnected()) {
                    refreshBookmarks(collectionId)
                }
            }
    }

    override suspend fun getBookmarkById(bookmarkId: Long): Result<Bookmark> {
        return try {
            val entity = bookmarkDao.getBookmarkById(bookmarkId)
            if (entity != null) {
                Result.success(entity.toBookmark())
            } else {
                Result.failure(NoSuchElementException("Bookmark not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertBookmark(bookmark: Bookmark): Result<Long> {
        return try {
            val entity = bookmark.toBookmarkEntity()
            val id = bookmarkDao.insertBookmark(entity)
            if (networkConnectivityManager.isConnected()) {
                try {
                    val bookmarkDto = bookmark.toBookmarkDto()
                    val response = apiService.createBookmark(bookmarkDto)
                    // Update local ID with server ID
                    bookmarkDao.updateLocalId(id, response.id)
                    // Update the entity with server state
                    val updatedEntity = entity.copy(
                        id = response.id,
                        serverIsFavorite = response.isFavorite,
                        serverIsArchived = response.isArchived,
                        isSynced = true,
                        updatedAt = Date()
                    )
                    bookmarkDao.updateBookmark(updatedEntity)
                } catch (e: Exception) {
                    // Continue with local operation even if sync fails
                    errorHandler.logError("Failed to sync bookmark with server", e)
                }
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBookmark(bookmark: Bookmark): Result<Unit> {
        return try {
            val entity = bookmark.toBookmarkEntity()
            bookmarkDao.updateBookmark(entity)
            if (networkConnectivityManager.isConnected()) {
                try {
                    val bookmarkDto = bookmark.toBookmarkDto()
                    val response = apiService.updateBookmark(bookmark.id, bookmarkDto)
                    // Update the entity with server state
                    val updatedEntity = entity.copy(
                        serverIsFavorite = response.isFavorite,
                        serverIsArchived = response.isArchived,
                        isSynced = true,
                        updatedAt = Date()
                    )
                    bookmarkDao.updateBookmark(updatedEntity)
                } catch (e: Exception) {
                    // Continue with local operation even if sync fails
                    errorHandler.logError("Failed to sync bookmark update with server", e)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBookmark(bookmarkId: Long): Result<Unit> {
        return try {
            val entity = bookmarkDao.getBookmarkById(bookmarkId)
            if (entity != null) {
                if (networkConnectivityManager.isConnected()) {
                    try {
                        apiService.deleteBookmark(bookmarkId)
                    } catch (e: Exception) {
                        // Continue with local operation even if sync fails
                        errorHandler.logError("Failed to sync bookmark deletion with server", e)
                    }
                }
                // Mark as deleted locally
                val now = Date()
                val deletedEntity = entity.copy(
                    isDeleted = true,
                    updatedAt = now
                )
                bookmarkDao.updateBookmark(deletedEntity)
                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("Bookmark not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(bookmarkId: Long, isFavorite: Boolean): Result<Unit> {
        return try {
            val now = Date()
            // Update local state
            bookmarkDao.updateFavoriteStatus(bookmarkId, isFavorite, now)
            
            if (networkConnectivityManager.isConnected()) {
                try {
                    // Update server state
                    val response = apiService.updateBookmarkFavorite(
                        bookmarkId = bookmarkId,
                        isFavorite = mapOf("is_favorite" to isFavorite)
                    )
                    // If the API call was successful, update local entity with synced state
                    if (response.isSuccessful) {
                        val entity = bookmarkDao.getBookmarkById(bookmarkId)
                        if (entity != null) {
                            val updatedEntity = entity.copy(
                                serverIsFavorite = isFavorite, // Assume server state matches what we sent
                                isSynced = true,
                                updatedAt = now
                            )
                            bookmarkDao.updateBookmark(updatedEntity)
                        }
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync favorite status with server", e)
                    // We'll sync the change later
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveBookmark(bookmarkId: Long, isArchived: Boolean): Result<Unit> {
        return try {
            val now = Date()
            // Update local state
            bookmarkDao.updateArchiveStatus(bookmarkId, isArchived, now)
            
            if (networkConnectivityManager.isConnected()) {
                try {
                    // Update server state
                    val response = apiService.updateBookmarkArchive(
                        bookmarkId = bookmarkId,
                        isArchived = mapOf("is_archived" to isArchived)
                    )
                    // If the API call was successful, update local entity with synced state
                    if (response.isSuccessful) {
                        val entity = bookmarkDao.getBookmarkById(bookmarkId)
                        if (entity != null) {
                            val updatedEntity = entity.copy(
                                serverIsArchived = isArchived, // Assume server state matches what we sent
                                isSynced = true,
                                updatedAt = now
                            )
                            bookmarkDao.updateBookmark(updatedEntity)
                        }
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync archive status with server", e)
                    // We'll sync the change later
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastOpened(bookmarkId: Long): Result<Unit> {
        return try {
            val now = Date()
            // Update local state
            bookmarkDao.updateLastOpened(bookmarkId, now)
            
            if (networkConnectivityManager.isConnected()) {
                try {
                    // Get the current entity to update last opened time on server
                    val entity = bookmarkDao.getBookmarkById(bookmarkId)
                    if (entity != null) {
                        // Create a DTO with updated lastOpened time
                        val bookmarkDto = entity.copy(
                            lastOpened = now,
                            updatedAt = now
                        ).toBookmarkDto()
                        
                        // Update on server
                        val response = apiService.updateBookmark(bookmarkId, bookmarkDto)
                        
                        // Update local entity with server state
                        val updatedEntity = entity.copy(
                            lastOpened = response.lastOpened,
                            updatedAt = response.updatedAt,
                            isSynced = true
                        )
                        bookmarkDao.updateBookmark(updatedEntity)
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync last opened time with server", e)
                    // We'll sync the change later
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchBookmarks(query: String): Flow<List<Bookmark>> {
        return bookmarkDao.searchBookmarks(query)
            .map { entities -> entities.map { it.toBookmark() } }
    }

    override suspend fun refreshBookmarks(collectionId: Long) {
        if (!networkConnectivityManager.isConnected()) return
        
        try {
            // Get bookmarks from server
            val bookmarks = apiService.getBookmarksByCollection(collectionId)
            
            // Get existing bookmarks from local DB
            val existingBookmarks = bookmarkDao.observeBookmarksByCollection(collectionId).firstOrNull() ?: emptyList()
            
            // Create maps for quick lookup
            val remoteBookmarksMap = bookmarks.associateBy { it.id }
            val localBookmarksMap = existingBookmarks.associateBy { it.id }
            
            // Find bookmarks to insert/update
            val bookmarksToInsertOrUpdate = bookmarks.map { remote ->
                val local = localBookmarksMap[remote.id]
                if (local != null) {
                    // Update existing bookmark with server data
                    local.copy(
                        title = remote.title,
                        description = remote.description,
                        url = remote.url,
                        imageUrl = remote.imageUrl,
                        isFavorite = remote.isFavorite,
                        isArchived = remote.isArchived,
                        lastOpened = remote.lastOpened,
                        updatedAt = remote.updatedAt,
                        isSynced = true,
                        serverIsFavorite = remote.isFavorite,
                        serverIsArchived = remote.isArchived
                    )
                } else {
                    // Convert remote DTO to entity with server state
                    remote.toBookmarkEntity(collectionId = collectionId, isSynced = true).copy(
                        serverIsFavorite = remote.isFavorite,
                        serverIsArchived = remote.isArchived
                    )
                }
            }
            
            // Find bookmarks to delete (present locally but not on server)
            val bookmarksToDelete = existingBookmarks.filter { local ->
                !remoteBookmarksMap.containsKey(local.id) && local.isSynced
            }
            
            // Apply changes in a transaction
            withContext(Dispatchers.IO) {
                bookmarkDao.runInTransaction {
                    // Insert or update bookmarks
                    if (bookmarksToInsertOrUpdate.isNotEmpty()) {
                        bookmarkDao.insertBookmarks(bookmarksToInsertOrUpdate)
                    }
                    
                    // Delete bookmarks that don't exist on server
                    if (bookmarksToDelete.isNotEmpty()) {
                        val idsToDelete = bookmarksToDelete.map { it.id }
                        bookmarkDao.deleteBookmarks(idsToDelete)
                    }
                }
            }
        } catch (e: Exception) {
            errorHandler.logError("Failed to refresh bookmarks", e)
            throw e
        }
    }

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.observeAllBookmarks()
            .map { entities -> entities.map { it.toBookmark() } }
    }

    override fun getFavoriteBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.observeFavoriteBookmarks()
            .map { entities -> entities.map { it.toBookmark() } }
    }

    override fun getArchivedBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.observeArchivedBookmarks()
            .map { entities -> entities.map { it.toBookmark() } }
    }

    override suspend fun deleteBookmarks(bookmarkIds: List<Long>): Result<Unit> {
        return try {
            if (networkConnectivityManager.isConnected()) {
                try {
                    // Delete on server first
                    bookmarkIds.forEach { bookmarkId ->
                        apiService.deleteBookmark(bookmarkId)
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to delete bookmarks on server", e)
                    // Continue with local deletion even if server fails
                }
            }
            
            // Mark as deleted locally
            val now = Date()
            bookmarkDao.markBookmarksAsDeleted(bookmarkIds, now)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavoriteForMultiple(bookmarkIds: List<Long>, isFavorite: Boolean): Result<Unit> {
        return try {
            val now = Date()
            // Update local state first
            bookmarkDao.updateFavoriteStatusForMultiple(bookmarkIds, isFavorite, now)
            
            if (networkConnectivityManager.isConnected()) {
                try {
                    // Update server state for each bookmark
                    bookmarkIds.forEach { bookmarkId ->
                        val response = apiService.updateBookmarkFavorite(
                            bookmarkId = bookmarkId,
                            isFavorite = mapOf("is_favorite" to isFavorite)
                        )
                        // Update sync status if successful
                        if (response.isSuccessful) {
                            bookmarkDao.markBookmarkAsSynced(bookmarkId, now)
                        }
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync favorite status with server", e)
                    // Changes will be synced later
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveMultipleBookmarks(bookmarkIds: List<Long>, isArchived: Boolean): Result<Unit> {
        return try {
            val now = Date()
            // Update local state first
            bookmarkDao.updateArchiveStatusForMultiple(bookmarkIds, isArchived, now)
            
            if (networkConnectivityManager.isConnected()) {
                try {
                    // Update server state for each bookmark
                    bookmarkIds.forEach { bookmarkId ->
                        val response = apiService.updateBookmarkArchive(
                            bookmarkId = bookmarkId,
                            isArchived = mapOf("is_archived" to isArchived)
                        )
                        // Update sync status if successful
                        if (response.isSuccessful) {
                            bookmarkDao.markBookmarkAsSynced(bookmarkId, now)
                        }
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync archive status with server", e)
                    // Changes will be synced later
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncBookmarks(collectionId: Long) {
        if (!networkConnectivityManager.isConnected()) return
        
        try {
            // 1. First, push local changes to server
            // Get unsynced bookmarks (new or updated)
            val unsyncedBookmarks = bookmarkDao.getUnsyncedBookmarks()
                .filter { it.collectionId == collectionId }
            
            // Get bookmarks marked as deleted
            val deletedBookmarks = bookmarkDao.getDeletedBookmarks()
                .filter { it.collectionId == collectionId }
            
            // Get bookmarks with modified favorite/archive status
            val modifiedBookmarks = bookmarkDao.getModifiedBookmarks()
                .filter { it.collectionId == collectionId }
            
            val now = Date()
            
            // Process unsynced bookmarks (insert or update)
            unsyncedBookmarks.forEach { entity ->
                try {
                    val bookmarkDto = entity.toBookmarkDto()
                    if (entity.isLocalId) {
                        // New entity - create on server
                        val response = apiService.createBookmark(bookmarkDto)
                        // Update local entity with server data
                        val updatedEntity = entity.copy(
                            id = response.id,
                            isLocalId = false,
                            serverIsFavorite = response.isFavorite,
                            serverIsArchived = response.isArchived,
                            isSynced = true,
                            updatedAt = now
                        )
                        bookmarkDao.updateBookmark(updatedEntity)
                    } else {
                        // Existing entity - update on server
                        val response = apiService.updateBookmark(entity.id, bookmarkDto)
                        // Update local entity with server data
                        val updatedEntity = entity.copy(
                            serverIsFavorite = response.isFavorite,
                            serverIsArchived = response.isArchived,
                            isSynced = true,
                            updatedAt = now
                        )
                        bookmarkDao.updateBookmark(updatedEntity)
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync bookmark ${entity.id}", e)
                }
            }
            
            // Process deleted bookmarks
            deletedBookmarks.forEach { entity ->
                try {
                    if (!entity.isLocalId) {
                        // Only delete on server if it was previously synced
                        apiService.deleteBookmark(entity.id)
                    }
                    // Remove from local DB
                    bookmarkDao.deleteBookmark(entity.id)
                } catch (e: Exception) {
                    errorHandler.logError("Failed to delete bookmark ${entity.id}", e)
                }
            }
            
            // Process modified bookmarks (favorite/archive status)
            modifiedBookmarks.forEach { entity ->
                try {
                    // Only update if the entity exists on the server
                    if (!entity.isLocalId) {
                        // Update favorite status if changed
                        if (entity.isFavorite != entity.serverIsFavorite) {
                            val response = apiService.updateBookmarkFavorite(
                                bookmarkId = entity.id,
                                isFavorite = mapOf("is_favorite" to entity.isFavorite)
                            )
                            // Update local entity with server state if successful
                            if (response.isSuccessful) {
                                val updatedEntity = entity.copy(
                                    serverIsFavorite = entity.isFavorite, // Assume server matches what we sent
                                    isSynced = true,
                                    updatedAt = now
                                )
                                bookmarkDao.updateBookmark(updatedEntity)
                            }
                        }
                        
                        // Update archive status if changed
                        if (entity.isArchived != entity.serverIsArchived) {
                            val response = apiService.updateBookmarkArchive(
                                bookmarkId = entity.id,
                                isArchived = mapOf("is_archived" to entity.isArchived)
                            )
                            // Update local entity with server state if successful
                            if (response.isSuccessful) {
                                val updatedEntity = entity.copy(
                                    serverIsArchived = entity.isArchived, // Assume server matches what we sent
                                    isSynced = true,
                                    updatedAt = now
                                )
                                bookmarkDao.updateBookmark(updatedEntity)
                            }
                        }
                    }
                } catch (e: Exception) {
                    errorHandler.logError("Failed to sync bookmark status ${entity.id}", e)
                }
            }
            
            // 2. Then, refresh bookmarks from server to get any changes made on other devices
            refreshBookmarks(collectionId)
            
        } catch (e: Exception) {
            errorHandler.logError("Failed to sync bookmarks", e)
            throw e
        }
    }
}
