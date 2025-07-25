package com.secondbrain.app.ui.collection.list

import androidx.compose.runtime.Immutable
import com.secondbrain.app.data.model.BookmarkCollection

@Immutable
data class CollectionListState(
    val isLoading: Boolean = false,
    val collections: List<BookmarkCollection> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
) {
    val isEmpty: Boolean
        get() = collections.isEmpty() && !isLoading && searchQuery.isEmpty()
    
    val filteredCollections: List<BookmarkCollection>
        get() = if (searchQuery.isBlank()) {
            collections
        } else {
            collections.filter { collection ->
                collection.name.contains(searchQuery, ignoreCase = true) ||
                collection.description?.contains(searchQuery, ignoreCase = true) == true
            }
        }
}
