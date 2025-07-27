package com.secondbrain.app.data.model

import java.util.*

/**
 * Domain model representing a bookmark collection in the Second Brain app.
 */
data class Collection(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val color: String = "#6366F1", // Default indigo color
    val isDefault: Boolean = false,
    val bookmarkCount: Int = 0,
    val isShared: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    companion object {
        /**
         * Creates a new collection with default values.
         */
        fun create(
            name: String,
            description: String? = null,
            color: String = "#6366F1"
        ): Collection {
            return Collection(
                name = name.trim(),
                description = description?.trim(),
                color = color,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
        
        /**
         * Sample collections for demonstration.
         */
        fun getSampleCollections(): List<Collection> {
            return listOf(
                Collection(1, "Work Resources", "Professional bookmarks and references", "#0F172A", false, 15),
                Collection(2, "Tech Learning", "Programming tutorials and documentation", "#06B6D4", false, 28),
                Collection(3, "Design Inspiration", "UI/UX design examples and tools", "#8B5CF6", false, 42),
                Collection(4, "Research Papers", "Academic papers and studies", "#EC4899", false, 8),
                Collection(5, "Recipe Collection", "Favorite cooking recipes", "#10B981", false, 22)
            )
        }
    }
}