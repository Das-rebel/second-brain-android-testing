package com.secondbrain.app.data.database.entities

import androidx.room.*
import com.secondbrain.app.data.model.Collection
import java.util.*

/**
 * Room entity for Collection data.
 */
@Entity(
    tableName = "collections",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["created_at"])
    ]
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "color")
    val color: String = "#6366F1",
    
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,
    
    @ColumnInfo(name = "bookmark_count")
    val bookmarkCount: Int = 0,
    
    @ColumnInfo(name = "is_shared")
    val isShared: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
) {
    /**
     * Converts this entity to a Collection domain model.
     */
    fun toCollection(): Collection {
        return Collection(
            id = id,
            name = name,
            description = description,
            color = color,
            isDefault = isDefault,
            bookmarkCount = bookmarkCount,
            isShared = isShared,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Creates a CollectionEntity from a Collection domain model.
         */
        fun fromCollection(collection: Collection): CollectionEntity {
            return CollectionEntity(
                id = collection.id,
                name = collection.name,
                description = collection.description,
                color = collection.color,
                isDefault = collection.isDefault,
                bookmarkCount = collection.bookmarkCount,
                isShared = collection.isShared,
                createdAt = collection.createdAt,
                updatedAt = collection.updatedAt
            )
        }
    }
}