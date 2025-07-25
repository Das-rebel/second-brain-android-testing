package com.secondbrain.app.domain.model

/**
 * Domain model representing bookmark filtering criteria.
 */
data class BookmarkFilter(
    val collectionId: Long? = null,
    val showFavorites: Boolean = false,
    val showArchived: Boolean = false,
    val searchQuery: String? = null,
    val tags: List<String> = emptyList(),
    val sortBy: BookmarkSortBy = BookmarkSortBy.CREATED_DATE_DESC
) {
    /**
     * Returns true if any filters are applied.
     */
    val hasActiveFilters: Boolean
        get() = collectionId != null || showFavorites || showArchived || 
                !searchQuery.isNullOrBlank() || tags.isNotEmpty()
    
    /**
     * Returns a copy of the filter with the collection ID updated.
     */
    fun withCollectionId(collectionId: Long?): BookmarkFilter {
        return copy(collectionId = collectionId)
    }
    
    /**
     * Returns a copy of the filter with favorites toggled.
     */
    fun toggleFavorites(): BookmarkFilter {
        return copy(showFavorites = !showFavorites, showArchived = false)
    }
    
    /**
     * Returns a copy of the filter with archived toggled.
     */
    fun toggleArchived(): BookmarkFilter {
        return copy(showArchived = !showArchived, showFavorites = false)
    }
    
    /**
     * Returns a copy of the filter with the search query updated.
     */
    fun withSearchQuery(query: String?): BookmarkFilter {
        return copy(searchQuery = query?.trim()?.takeIf { it.isNotEmpty() })
    }
    
    /**
     * Returns a copy of the filter with tags added.
     */
    fun withTags(tags: List<String>): BookmarkFilter {
        return copy(tags = tags.filter { it.isNotBlank() }.map { it.trim() })
    }
    
    /**
     * Returns a copy of the filter with sort criteria updated.
     */
    fun withSortBy(sortBy: BookmarkSortBy): BookmarkFilter {
        return copy(sortBy = sortBy)
    }
    
    /**
     * Clears all filters except collection ID.
     */
    fun clearFilters(): BookmarkFilter {
        return copy(
            showFavorites = false,
            showArchived = false,
            searchQuery = null,
            tags = emptyList()
        )
    }
    
    companion object {
        /**
         * Creates a filter for a specific collection.
         */
        fun forCollection(collectionId: Long): BookmarkFilter {
            return BookmarkFilter(collectionId = collectionId)
        }
        
        /**
         * Creates a filter for favorite bookmarks.
         */
        fun forFavorites(): BookmarkFilter {
            return BookmarkFilter(showFavorites = true)
        }
        
        /**
         * Creates a filter for archived bookmarks.
         */
        fun forArchived(): BookmarkFilter {
            return BookmarkFilter(showArchived = true)
        }
        
        /**
         * Creates a filter for search query.
         */
        fun forSearch(query: String): BookmarkFilter {
            return BookmarkFilter(searchQuery = query.trim().takeIf { it.isNotEmpty() })
        }
    }
}

/**
 * Enum representing different sorting options for bookmarks.
 */
enum class BookmarkSortBy {
    /** Sort by creation date, newest first */
    CREATED_DATE_DESC,
    
    /** Sort by creation date, oldest first */
    CREATED_DATE_ASC,
    
    /** Sort by last updated date, newest first */
    UPDATED_DATE_DESC,
    
    /** Sort by last updated date, oldest first */
    UPDATED_DATE_ASC,
    
    /** Sort by last opened date, newest first */
    LAST_OPENED_DESC,
    
    /** Sort by last opened date, oldest first */
    LAST_OPENED_ASC,
    
    /** Sort by title alphabetically */
    TITLE_ASC,
    
    /** Sort by title reverse alphabetically */
    TITLE_DESC,
    
    /** Sort by open count, most opened first */
    OPEN_COUNT_DESC,
    
    /** Sort by open count, least opened first */
    OPEN_COUNT_ASC,
    
    /** Sort by domain name */
    DOMAIN_ASC
}