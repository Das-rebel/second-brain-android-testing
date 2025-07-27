package com.secondbrain.app.data.model

import java.util.*

/**
 * Domain model representing a bookmark in the Second Brain app.
 */
data class Bookmark(
    val id: Long = 0,
    val title: String,
    val url: String,
    val description: String? = null,
    val domain: String? = null,
    val faviconUrl: String? = null,
    val tags: List<String> = emptyList(),
    val collectionId: Long? = null,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val openCount: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    val computedDomain: String
        get() = domain ?: try {
            val uri = java.net.URI(url)
            uri.host ?: ""
        } catch (e: Exception) {
            ""
        }

    companion object {
        /**
         * Creates a new bookmark with default values.
         */
        fun create(
            title: String,
            url: String,
            description: String? = null,
            tags: List<String> = emptyList(),
            collectionId: Long? = null
        ): Bookmark {
            return Bookmark(
                title = title.trim(),
                url = url.trim(),
                description = description?.trim(),
                tags = tags.map { it.trim() }.filter { it.isNotEmpty() },
                collectionId = collectionId,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
        
        /**
         * Sample bookmarks for demonstration.
         */
        fun getSampleBookmarks(): List<Bookmark> {
            return listOf(
                Bookmark(
                    id = 1,
                    title = "Modern Android Development Guide",
                    url = "https://developer.android.com/guide",
                    description = "Comprehensive guide to building modern Android apps",
                    domain = "developer.android.com",
                    tags = listOf("android", "development", "guide"),
                    isFavorite = true,
                    openCount = 15
                ),
                Bookmark(
                    id = 2,
                    title = "Jetpack Compose Documentation",
                    url = "https://developer.android.com/jetpack/compose",
                    description = "Official documentation for Jetpack Compose",
                    domain = "developer.android.com",
                    tags = listOf("compose", "ui", "android"),
                    isFavorite = true,
                    openCount = 28
                ),
                Bookmark(
                    id = 3,
                    title = "Material Design 3",
                    url = "https://m3.material.io/",
                    description = "Latest Material Design guidelines and components",
                    domain = "m3.material.io",
                    tags = listOf("design", "material", "ui"),
                    openCount = 12
                ),
                Bookmark(
                    id = 4,
                    title = "Kotlin Coroutines Guide",
                    url = "https://kotlinlang.org/docs/coroutines-guide.html",
                    description = "Complete guide to Kotlin coroutines",
                    domain = "kotlinlang.org",
                    tags = listOf("kotlin", "coroutines", "async"),
                    isFavorite = true,
                    openCount = 22
                ),
                Bookmark(
                    id = 5,
                    title = "GitHub REST API",
                    url = "https://docs.github.com/en/rest",
                    description = "GitHub's REST API documentation",
                    domain = "docs.github.com",
                    tags = listOf("api", "github", "rest"),
                    isArchived = true,
                    openCount = 5
                )
            )
        }
    }
}