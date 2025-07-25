package com.secondbrain.app.ui.collection.detail

import androidx.compose.runtime.Immutable
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.BookmarkCollection

@Immutable
data class CollectionDetailState(
    val isLoading: Boolean = false,
    val collection: BookmarkCollection? = null,
    val bookmarks: List<Bookmark> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val isBookmarkOptionsExpanded: Boolean = false,
    val selectedBookmark: Bookmark? = null
) {
    val isEmpty: Boolean
        get() = bookmarks.isEmpty() && !isLoading && error == null
}
