package com.secondbrain.app.ui.bookmark

import androidx.compose.runtime.Immutable
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.BookmarkFilter

@Immutable
data class BookmarkListState(
    val isLoading: Boolean = false,
    val bookmarks: List<Bookmark> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val filter: BookmarkFilter = BookmarkFilter()
) {
    val isEmpty: Boolean
        get() = bookmarks.isEmpty() && !isLoading && searchQuery.isEmpty()
}
