package com.secondbrain.app.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.secondbrain.app.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds and provides Retrofit instances for API services.
 */
@Singleton
class RetrofitBuilder @Inject constructor() {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
    
    private val contentType = "application/json".toMediaType()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                // Add auth token if available
                // .header("Authorization", "Bearer ${tokenProvider.getToken()}")
            
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(logging)
            }
        }
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
    
    val collectionApiService: CollectionApiService by lazy {
        retrofit.create(CollectionApiService::class.java)
    }
    
    val bookmarkApiService: BookmarkApiService by lazy {
        retrofit.create(BookmarkApiService::class.java)
    }
    
    companion object {
        // For direct access without DI if needed
        val instance: RetrofitBuilder by lazy { RetrofitBuilder() }
    }
}
