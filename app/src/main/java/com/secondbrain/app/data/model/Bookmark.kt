package com.secondbrain.app.data.model

import java.util.*

/**
 * Data layer model for bookmark - used for data transformation.
 * This is a bridge between the domain model and the database entity.
 * 
 * Note: The actual domain model is in com.secondbrain.app.domain.model.Bookmark
 * This data model should mainly be used for data layer operations and mappings.
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
)
