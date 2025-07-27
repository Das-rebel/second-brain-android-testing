package com.secondbrain.app.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Data models for Supabase API responses.
 */

@Serializable
data class SupabaseBookmark(
    val id: Long? = null,
    val url: String,
    val title: String,
    val description: String? = null,
    val domain: String? = null,
    @SerialName("favicon_url")
    val faviconUrl: String? = null,
    @SerialName("is_favorite")
    val isFavorite: Boolean = false,
    @SerialName("is_archived")
    val isArchived: Boolean = false,
    @SerialName("open_count")
    val openCount: Int = 0,
    val tags: String? = null, // Comma-separated tags
    @SerialName("collection_id")
    val collectionId: Long? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class SupabaseCollection(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val color: String = "#3B82F6",
    @SerialName("is_shared")
    val isShared: Boolean = false,
    @SerialName("is_default")
    val isDefault: Boolean = false,
    @SerialName("bookmark_count")
    val bookmarkCount: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class SupabaseBookmarkNote(
    val id: Long? = null,
    @SerialName("bookmark_id")
    val bookmarkId: Long,
    val content: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class SupabaseTag(
    val id: Long? = null,
    val name: String,
    val color: String? = null,
    @SerialName("usage_count")
    val usageCount: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null
)