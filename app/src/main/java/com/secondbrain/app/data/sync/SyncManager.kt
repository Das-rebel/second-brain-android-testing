package com.secondbrain.app.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.data.remote.repository.SupabaseRepository
import com.secondbrain.app.data.repository.BookmarkRepository
import com.secondbrain.app.data.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first

/**
 * Manages offline-first synchronization between local Room database and remote Supabase.
 */
class SyncManager(
    private val context: Context,
    private val bookmarkRepository: BookmarkRepository,
    private val collectionRepository: CollectionRepository,
    private val supabaseRepository: SupabaseRepository
) {
    
    data class SyncResult(
        val success: Boolean,
        val bookmarksSynced: Int = 0,
        val collectionsSynced: Int = 0,
        val error: String? = null
    )
    
    /**
     * Performs full sync between local and remote data.
     * Local data takes precedence in case of conflicts.
     */
    suspend fun performSync(): SyncResult {
        if (!isNetworkAvailable()) {
            return SyncResult(false, error = "No network connection")
        }
        
        return try {
            // Sync collections first
            val localCollections = collectionRepository.getAllCollections().first()
            val syncedCollections = syncCollections(localCollections)
            
            // Then sync bookmarks
            val localBookmarks = bookmarkRepository.getAllBookmarks().first()
            val syncedBookmarks = syncBookmarks(localBookmarks)
            
            SyncResult(
                success = true,
                bookmarksSynced = syncedBookmarks,
                collectionsSynced = syncedCollections
            )
        } catch (e: Exception) {
            SyncResult(false, error = e.message)
        }
    }
    
    /**
     * Performs incremental sync - only sync items modified since last sync.
     */
    fun performIncrementalSync(): Flow<SyncResult> = flow {
        if (!isNetworkAvailable()) {
            emit(SyncResult(false, error = "No network connection"))
            return@flow
        }
        
        try {
            emit(SyncResult(false, error = "Incremental sync not yet implemented"))
        } catch (e: Exception) {
            emit(SyncResult(false, error = e.message))
        }
    }
    
    private suspend fun syncCollections(localCollections: List<Collection>): Int {
        return try {
            val remoteCollections = supabaseRepository.getAllCollections()
            var syncedCount = 0
            
            // Upload local collections that don't exist remotely
            localCollections.forEach { localCollection ->
                val existsRemotely = remoteCollections.any { it.name == localCollection.name }
                if (!existsRemotely) {
                    supabaseRepository.insertCollection(localCollection)
                    syncedCount++
                }
            }
            
            // Download remote collections that don't exist locally
            remoteCollections.forEach { remoteCollection ->
                val existsLocally = localCollections.any { it.name == remoteCollection.name }
                if (!existsLocally) {
                    collectionRepository.insertCollection(remoteCollection)
                    syncedCount++
                }
            }
            
            syncedCount
        } catch (e: Exception) {
            0
        }
    }
    
    private suspend fun syncBookmarks(localBookmarks: List<Bookmark>): Int {
        return try {
            val remoteBookmarks = supabaseRepository.getAllBookmarks()
            var syncedCount = 0
            
            // Upload local bookmarks that don't exist remotely
            localBookmarks.forEach { localBookmark ->
                val existsRemotely = remoteBookmarks.any { it.url == localBookmark.url }
                if (!existsRemotely) {
                    supabaseRepository.insertBookmark(localBookmark)
                    syncedCount++
                }
            }
            
            // Download remote bookmarks that don't exist locally
            remoteBookmarks.forEach { remoteBookmark ->
                val existsLocally = localBookmarks.any { it.url == remoteBookmark.url }
                if (!existsLocally) {
                    bookmarkRepository.insertBookmark(remoteBookmark)
                    syncedCount++
                }
            }
            
            syncedCount
        } catch (e: Exception) {
            0
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}