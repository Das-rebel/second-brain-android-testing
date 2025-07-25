package com.secondbrain.app.domain.model

import java.util.*

/**
 * Domain model representing a collection in the application.
 *
 * @property id The unique identifier for the collection.
 * @property userId The ID of the user who owns this collection.
 * @property name The name of the collection.
 * @property description Optional description of the collection.
 * @property color The color theme for the collection (hex code).
 * @property icon Optional icon identifier for the collection.
 * @property createdAt When the collection was created.
 * @property updatedAt When the collection was last updated.
 * @property itemCount The number of bookmarks in this collection.
 * @property isDefault Whether this is the default collection for the user.
 * @property isShared Whether the collection is shared with others.
 * @property shareUrl Optional URL for sharing the collection publicly.
 * @property shareExpiry Optional expiry date for the share URL.
 * @property accessLevel The access level for shared collections (view, edit, admin).
 * @property sortOrder The sort order for displaying collections.
 * @property isDeleted Whether the collection has been marked as deleted (soft delete).
 * @property deletedAt When the collection was deleted.
 */
data class Collection(
    val id: Long = 0,
    val userId: String,
    val name: String,
    val description: String? = null,
    val color: String,
    val icon: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val itemCount: Int = 0,
    val isDefault: Boolean = false,
    val isShared: Boolean = false,
    val shareUrl: String? = null,
    val shareExpiry: Date? = null,
    val accessLevel: CollectionAccessLevel = CollectionAccessLevel.VIEW,
    val sortOrder: Int = 0,
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null
) {
    /**
     * Returns a copy of the collection with updated item count.
     */
    fun withItemCount(count: Int): Collection {
        return copy(
            itemCount = count,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the collection marked as shared.
     */
    fun markAsShared(shareUrl: String, expiry: Date? = null): Collection {
        return copy(
            isShared = true,
            shareUrl = shareUrl,
            shareExpiry = expiry,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the collection with sharing disabled.
     */
    fun disableSharing(): Collection {
        return copy(
            isShared = false,
            shareUrl = null,
            shareExpiry = null,
            updatedAt = Date()
        )
    }
    
    /**
     * Returns a copy of the collection marked as deleted (soft delete).
     */
    fun markAsDeleted(): Collection {
        return copy(
            isDeleted = true,
            deletedAt = Date(),
            updatedAt = Date()
        )
    }
    
    /**
     * Returns true if the collection is active (not deleted).
     */
    val isActive: Boolean
        get() = !isDeleted
    
    /**
     * Returns true if the collection has items.
     */
    val hasItems: Boolean
        get() = itemCount > 0
    
    /**
     * Returns true if the share URL is expired.
     */
    val isShareExpired: Boolean
        get() = shareExpiry?.let { it.before(Date()) } ?: false
    
    /**
     * Returns true if the collection can be shared.
     */
    val canShare: Boolean
        get() = isActive && !isDefault
    
    companion object {
        /**
         * Default colors for collections.
         */
        val DEFAULT_COLORS = listOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57",
            "#FF9FF3", "#54A0FF", "#5F27CD", "#00D2D3", "#FF9F43",
            "#FC427B", "#BDC3C7", "#6C5CE7", "#FD79A8", "#FDCB6E"
        )
        
        /**
         * Default icons for collections.
         */
        val DEFAULT_ICONS = listOf(
            "bookmark", "folder", "heart", "star", "work", "home", 
            "school", "travel", "food", "music", "movies", "books",
            "tech", "sports", "gaming", "art", "finance", "health"
        )
        
        /**
         * Creates a new collection with default values.
         */
        fun create(
            userId: String,
            name: String,
            description: String? = null,
            color: String = DEFAULT_COLORS.random(),
            icon: String? = null
        ): Collection {
            return Collection(
                userId = userId,
                name = name.trim(),
                description = description?.trim(),
                color = color,
                icon = icon,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}

/**
 * Enum representing different access levels for shared collections.
 */
enum class CollectionAccessLevel(val value: String) {
    /** Read-only access to view bookmarks */
    VIEW("view"),
    
    /** Can add and edit bookmarks */
    EDIT("edit"),
    
    /** Full admin access including sharing and deletion */
    ADMIN("admin");
    
    companion object {
        fun fromString(value: String): CollectionAccessLevel {
            return values().find { it.value == value } ?: VIEW
        }
    }
}