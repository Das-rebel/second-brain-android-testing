package com.secondbrain.app.data.network

import com.secondbrain.app.data.network.dto.BookmarkDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service for bookmark-related API endpoints.
 */
interface BookmarkApiService {

    @GET("collections/{collectionId}/bookmarks")
    suspend fun getBookmarksByCollection(
        @Path("collectionId") collectionId: Long
    ): List<BookmarkDto>

    @GET("bookmarks/{bookmarkId}")
    suspend fun getBookmarkById(
        @Path("bookmarkId") bookmarkId: Long
    ): BookmarkDto

    @POST("bookmarks")
    suspend fun createBookmark(
        @Body bookmark: BookmarkDto
    ): BookmarkDto

    @PUT("bookmarks/{bookmarkId}")
    suspend fun updateBookmark(
        @Path("bookmarkId") bookmarkId: Long,
        @Body bookmark: BookmarkDto
    ): BookmarkDto

    @PATCH("bookmarks/{bookmarkId}/favorite")
    suspend fun updateBookmarkFavorite(
        @Path("bookmarkId") bookmarkId: Long,
        @Body isFavorite: Map<String, Boolean>
    ): Response<Unit>

    @PATCH("bookmarks/{bookmarkId}/archive")
    suspend fun updateBookmarkArchive(
        @Path("bookmarkId") bookmarkId: Long,
        @Body isArchived: Map<String, Boolean>
    ): Response<Unit>

    @DELETE("bookmarks/{bookmarkId}")
    suspend fun deleteBookmark(
        @Path("bookmarkId") bookmarkId: Long
    ): Response<Unit>

    @GET("search")
    suspend fun searchBookmarks(
        @Query("q") query: String,
        @Query("collectionId") collectionId: Long? = null
    ): List<BookmarkDto>
}
