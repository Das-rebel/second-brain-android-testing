package com.secondbrain.app.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

/**
 * Data Transfer Object for bookmark API responses.
 */
@JsonClass(generateAdapter = true)
data class BookmarkDto(
    @Json(name = "id") val id: Long,
    @Json(name = "collection_id") val collectionId: Long,
    @Json(name = "title") val title: String,
    @Json(name = "url") val url: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "favicon_url") val faviconUrl: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "is_favorite") val isFavorite: Boolean = false,
    @Json(name = "is_archived") val isArchived: Boolean = false,
    @Json(name = "server_is_favorite") val serverIsFavorite: Boolean = isFavorite,
    @Json(name = "server_is_archived") val serverIsArchived: Boolean = isArchived,
    @Json(name = "tags") val tags: List<String> = emptyList(),
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "updated_at") val updatedAt: Date,
    @Json(name = "last_opened") val lastOpened: Date? = null,
    @Json(name = "open_count") val openCount: Int = 0
)

/**
 * Request DTO for creating or updating a bookmark.
 */
@JsonClass(generateAdapter = true)
data class BookmarkRequest(
    @Json(name = "collection_id") val collectionId: Long,
    @Json(name = "title") val title: String,
    @Json(name = "url") val url: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "tags") val tags: List<String> = emptyList()
)
