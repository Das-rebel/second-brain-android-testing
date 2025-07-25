package com.secondbrain.app.di

import android.content.Context
import com.secondbrain.app.data.local.AppDatabase
import com.secondbrain.app.data.local.dao.BookmarkDao
import com.secondbrain.app.data.local.dao.CollectionDao
import com.secondbrain.app.data.network.BookmarkApiService
import com.secondbrain.app.data.network.CollectionApiService
import com.secondbrain.app.data.network.NetworkConnectivityManager
import com.secondbrain.app.data.network.RetrofitBuilder
import com.secondbrain.app.data.repository.BookmarkRepository
import com.secondbrain.app.data.repository.BookmarkRepositoryImpl
import com.secondbrain.app.data.repository.CollectionRepository
import com.secondbrain.app.data.repository.CollectionRepositoryImpl
import com.secondbrain.app.util.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideCollectionDao(database: AppDatabase): CollectionDao {
        return database.collectionDao()
    }

    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(@ApplicationContext context: Context): NetworkConnectivityManager {
        return NetworkConnectivityManager(context)
    }

    @Provides
    @Singleton
    fun provideCollectionApiService(): CollectionApiService {
        return RetrofitBuilder.collectionApiService
    }

    @Provides
    @Singleton
    fun provideBookmarkApiService(): BookmarkApiService {
        return RetrofitBuilder.bookmarkApiService
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionDao: CollectionDao,
        apiService: CollectionApiService,
        networkConnectivityManager: NetworkConnectivityManager,
        errorHandler: ErrorHandler
    ): CollectionRepository {
        return CollectionRepositoryImpl(
            collectionDao = collectionDao,
            apiService = apiService,
            networkConnectivityManager = networkConnectivityManager,
            errorHandler = errorHandler
        )
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: BookmarkDao,
        apiService: BookmarkApiService,
        networkConnectivityManager: NetworkConnectivityManager,
        errorHandler: ErrorHandler
    ): BookmarkRepository {
        return BookmarkRepositoryImpl(
            bookmarkDao = bookmarkDao,
            apiService = apiService,
            networkConnectivityManager = networkConnectivityManager,
            errorHandler = errorHandler
        )
    }
}
