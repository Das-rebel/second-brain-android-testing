package com.secondbrain.app.di

import com.secondbrain.app.BuildConfig
import com.secondbrain.app.data.network.BookmarkApiService
import com.secondbrain.app.data.network.CollectionApiService
import com.secondbrain.app.data.network.NetworkConnectivityManager
import com.secondbrain.app.util.ErrorHandler
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    // Add common headers here
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.secondbrain.app/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideCollectionApiService(retrofit: Retrofit): CollectionApiService {
        return retrofit.create(CollectionApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideBookmarkApiService(retrofit: Retrofit): BookmarkApiService {
        return retrofit.create(BookmarkApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(
        @ApplicationContext context: android.content.Context
    ): NetworkConnectivityManager {
        return NetworkConnectivityManager(context)
    }
    
    @Provides
    @Singleton
    fun provideErrorHandler(
        @ApplicationContext context: android.content.Context
    ): ErrorHandler {
        return ErrorHandler(context)
    }
}
