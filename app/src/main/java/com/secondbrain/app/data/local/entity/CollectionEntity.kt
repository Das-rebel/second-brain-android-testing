package com.secondbrain.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity representing a collection in the local database.
 */
@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val name: String,
    val description: String? = null,
    val color: String,
    val icon: String? = null,
    val createdAt: Date,
    val updatedAt: Date,
    val itemCount: Int = 0,
    val isDefault: Boolean = false,
    val isShared: Boolean = false,
    val shareUrl: String? = null,
    val shareExpiry: Date? = null,
    val accessLevel: String = "view",
    val sortOrder: Int = 0,
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null
)
