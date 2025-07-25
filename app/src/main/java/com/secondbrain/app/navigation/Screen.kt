package com.secondbrain.app.navigation

/**
 * Sealed class representing all screens in the app.
 */
sealed class Screen(val route: String) {
    object CollectionList : Screen("collection_list")
    object CollectionDetail : Screen("collection_detail/{collectionId}") {
        fun createRoute(collectionId: Long) = "collection_detail/$collectionId"
    }
    object CreateCollection : Screen("create_collection")
    object EditCollection : Screen("edit_collection/{collectionId}") {
        fun createRoute(collectionId: Long) = "edit_collection/$collectionId"
    }
    object ShareCollection : Screen("share_collection/{collectionId}") {
        fun createRoute(collectionId: Long) = "share_collection/$collectionId"
    }
    object Settings : Screen("settings")
    object About : Screen("about")
    
    // Bookmark screens
    object BookmarkList : Screen("bookmark_list/{collectionId}") {
        fun createRoute(collectionId: Long) = "bookmark_list/$collectionId"
    }
    object BookmarkDetail : Screen("bookmark_detail/{bookmarkId}") {
        fun createRoute(bookmarkId: Long) = "bookmark_detail/$bookmarkId"
    }
    object CreateBookmark : Screen("create_bookmark")
    object EditBookmark : Screen("edit_bookmark/{bookmarkId}") {
        fun createRoute(bookmarkId: Long) = "edit_bookmark/$bookmarkId"
    }
    
    companion object {
        const val COLLECTION_ID_ARG = "collectionId"
        const val BOOKMARK_ID_ARG = "bookmarkId"
    }
}
