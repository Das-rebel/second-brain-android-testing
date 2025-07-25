package com.secondbrain.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

/**
 * Room entity representing a bookmark in the local database.
 */
@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("bookmark_collection_id_idx", ["collectionId"]),
        Index("bookmark_url_idx", ["url"], unique = true),
        Index("bookmark_created_at_idx", ["createdAt"]),
        Index("bookmark_updated_at_idx", ["updatedAt"])
    ]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val collectionId: Long,
    val title: String,
    val url: String,
    val description: String? = null,
    val faviconUrl: String? = null,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val serverIsFavorite: Boolean = false,
    val serverIsArchived: Boolean = false,
    val tags: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastOpened: Date? = null,
    val openCount: Int = 0,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val isLocalId: Boolean = true
)
