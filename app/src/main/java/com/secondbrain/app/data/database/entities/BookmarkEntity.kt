package com.secondbrain.app.data.database.entities

import androidx.room.*
import com.secondbrain.app.data.model.Bookmark
import java.util.*

/**
 * Room entity for Bookmark data.
 */
@Entity(
    tableName = "bookmarks",
    indices = [
        Index(value = ["title"]),
        Index(value = ["url"], unique = true),
        Index(value = ["collection_id"]),
        Index(value = ["created_at"]),
        Index(value = ["is_favorite"]),
        Index(value = ["is_archived"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "url")
    val url: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "domain")
    val domain: String? = null,
    
    @ColumnInfo(name = "favicon_url")
    val faviconUrl: String? = null,
    
    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(),
    
    @ColumnInfo(name = "collection_id")
    val collectionId: Long? = null,
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    
    @ColumnInfo(name = "open_count")
    val openCount: Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
) {
    /**
     * Converts this entity to a Bookmark domain model.
     */
    fun toBookmark(): Bookmark {
        return Bookmark(
            id = id,
            title = title,
            url = url,
            description = description,
            domain = domain,
            faviconUrl = faviconUrl,
            tags = tags,
            collectionId = collectionId,
            isFavorite = isFavorite,
            isArchived = isArchived,
            openCount = openCount,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Creates a BookmarkEntity from a Bookmark domain model.
         */
        fun fromBookmark(bookmark: Bookmark): BookmarkEntity {
            return BookmarkEntity(
                id = bookmark.id,
                title = bookmark.title,
                url = bookmark.url,
                description = bookmark.description,
                domain = bookmark.domain,
                faviconUrl = bookmark.faviconUrl,
                tags = bookmark.tags,
                collectionId = bookmark.collectionId,
                isFavorite = bookmark.isFavorite,
                isArchived = bookmark.isArchived,
                openCount = bookmark.openCount,
                createdAt = bookmark.createdAt,
                updatedAt = bookmark.updatedAt
            )
        }
    }
}