package com.secondbrain.app.domain.model

import java.util.*

/**
 * Domain model representing a bookmark in the application.
 *
 * @property id The unique identifier for the bookmark.
 * @property collectionId The ID of the collection this bookmark belongs to.
 * @property title The title of the bookmark.
 * @property url The URL of the bookmark.
 * @property description Optional description of the bookmark.
 * @property faviconUrl Optional URL to the favicon of the bookmarked site.
 * @property imageUrl Optional URL to a preview image of the bookmarked site.
 * @property isFavorite Whether the bookmark is marked as a favorite.
 * @property isArchived Whether the bookmark is archived.
 * @property tags List of tags associated with the bookmark.
 * @property createdAt When the bookmark was created.
 * @property updatedAt When the bookmark was last updated.
 * @property lastOpened When the bookmark was last opened by the user.
 * @property openCount How many times the bookmark has been opened.
 * @property isSynced Whether the bookmark has been synced with the server.
 * @property isDeleted Whether the bookmark has been marked as deleted (soft delete).
 * @property serverIsFavorite The favorite status on the server (for sync comparison).
 * @property serverIsArchived The archived status on the server (for sync comparison).
 * @property isSelected UI state for selection mode.
 */
data class Bookmark(
    val id: Long = 0,
    val collectionId: Long,
    val title: String,
    val url: String,
    val description: String? = null,
    val faviconUrl: String? = null,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val serverIsFavorite: Boolean = isFavorite,
    val serverIsArchived: Boolean = isArchived,
    val tags: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastOpened: Date? = null,
    val openCount: Int = 0,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val isSelected: Boolean = false
) {
    /**
     * Returns a copy of the bookmark with the lastOpened field updated to now
     * and openCount incremented by 1.
     */
    fun markAsOpened(): Bookmark {
        return copy(
            lastOpened = Date(),
            openCount = openCount + 1,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the bookmark with the favorite status toggled.
     */
    fun toggleFavorite(): Bookmark {
        return copy(
            isFavorite = !isFavorite,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the bookmark with the archived status toggled.
     */
    fun toggleArchived(): Bookmark {
        return copy(
            isArchived = !isArchived,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the bookmark marked as synced.
     */
    fun markAsSynced(): Bookmark {
        return copy(isSynced = true)
    }
    
    /**
     * Returns a copy of the bookmark marked as deleted (soft delete).
     */
    fun markAsDeleted(): Bookmark {
        return copy(
            isDeleted = true,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the bookmark with the given selection state.
     */
    fun withSelection(selected: Boolean): Bookmark {
        return copy(isSelected = selected)
    }
    
    /**
     * Returns the domain name of the URL.
     */
    val domain: String
        get() = try {
            val uri = java.net.URI(url)
            val domain = uri.host ?: return ""
            return if (domain.startsWith("www.")) domain.substring(4) else domain
        } catch (e: Exception) {
            ""
        }
    
    companion object {
        /**
         * Creates a new bookmark with default values.
         */
        fun create(
            collectionId: Long,
            title: String,
            url: String,
            description: String? = null,
            tags: List<String> = emptyList()
        ): Bookmark {
            return Bookmark(
                collectionId = collectionId,
                title = title.trim(),
                url = url.trim(),
                description = description?.trim(),
                tags = tags.map { it.trim() },
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}
