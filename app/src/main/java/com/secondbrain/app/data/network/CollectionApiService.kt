package com.secondbrain.app.data.network

import com.secondbrain.app.data.network.dto.CollectionDto
import com.secondbrain.app.data.network.dto.CollectionRequest
import com.secondbrain.app.data.network.dto.ShareCollectionResponse
import com.secondbrain.app.data.network.dto.UpdateSharingRequest
import retrofit2.Response
import retrofit2.http.*
import java.util.*

/**
 * Retrofit service for collection-related API endpoints.
 */
interface CollectionApiService {
    
    @GET("collections")
    suspend fun getCollections(): List<CollectionDto>
    
    @GET("collections/{id}")
    suspend fun getCollectionById(@Path("id") id: Long): CollectionDto
    
    @POST("collections")
    suspend fun createCollection(@Body request: CollectionRequest): CollectionDto
    
    @PUT("collections/{id}")
    suspend fun updateCollection(
        @Path("id") id: Long,
        @Body request: CollectionRequest
    ): CollectionDto
    
    @DELETE("collections/{id}")
    suspend fun deleteCollection(@Path("id") id: Long)
    
    @POST("collections/{id}/share")
    suspend fun shareCollection(
        @Path("id") id: Long,
        @Body request: UpdateSharingRequest
    ): ShareCollectionResponse
    
    @PUT("collections/{id}/sharing")
    suspend fun updateCollectionSharing(
        @Path("id") id: Long,
        @Body request: UpdateSharingRequest
    ): Response<Unit>
    
    @GET("shared/collections")
    suspend fun getSharedCollections(): List<CollectionDto>
    
    @POST("shared/collections/{shareId}/follow")
    suspend fun followSharedCollection(
        @Path("shareId") shareId: String
    ): CollectionDto
    
    @DELETE("shared/collections/{collectionId}/follow")
    suspend fun unfollowSharedCollection(
        @Path("collectionId") collectionId: Long
    )
    
    @GET("collections/shared-with-me")
    suspend fun getCollectionsSharedWithMe(): List<CollectionDto>
}
