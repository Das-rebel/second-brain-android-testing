package com.secondbrain.app.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

/**
 * Network DTO for a collection.
 */
@JsonClass(generateAdapter = true)
data class CollectionDto(
    @Json(name = "id") val id: Long,
    @Json(name = "user_id") val userId: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "color") val color: String,
    @Json(name = "icon") val icon: String?,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "updated_at") val updatedAt: Date,
    @Json(name = "item_count") val itemCount: Int,
    @Json(name = "is_default") val isDefault: Boolean,
    @Json(name = "is_shared") val isShared: Boolean,
    @Json(name = "share_url") val shareUrl: String?,
    @Json(name = "share_expiry") val shareExpiry: Date?,
    @Json(name = "access_level") val accessLevel: String,
    @Json(name = "sort_order") val sortOrder: Int
)

/**
 * Request DTO for creating or updating a collection.
 */
@JsonClass(generateAdapter = true)
data class CollectionRequest(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "color") val color: String,
    @Json(name = "icon") val icon: String?,
    @Json(name = "is_default") val isDefault: Boolean = false,
    @Json(name = "is_shared") val isShared: Boolean = false,
    @Json(name = "access_level") val accessLevel: String = "view"
)

/**
 * Request DTO for updating sharing settings.
 */
@JsonClass(generateAdapter = true)
data class UpdateSharingRequest(
    @Json(name = "is_shared") val isShared: Boolean,
    @Json(name = "access_level") val accessLevel: String,
    @Json(name = "expiry_days") val expiryDays: Int? = null
)

/**
 * Response DTO for shared collection operations.
 */
@JsonClass(generateAdapter = true)
data class ShareCollectionResponse(
    @Json(name = "share_url") val shareUrl: String,
    @Json(name = "expires_at") val expiresAt: Date?
)
