package com.secondbrain.app.di

import com.secondbrain.app.data.mapper.CollectionMapper
import com.secondbrain.app.data.mapper.CollectionMapperImpl
import com.secondbrain.app.data.repository.BookmarkRepository
import com.secondbrain.app.data.repository.BookmarkRepositoryImpl
import com.secondbrain.app.data.repository.CollectionRepository
import com.secondbrain.app.data.repository.CollectionRepositoryImpl
import com.secondbrain.app.data.sync.SyncManager
import com.secondbrain.app.data.sync.SyncManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        repository: BookmarkRepositoryImpl
    ): BookmarkRepository
    
    @Binds
    @Singleton
    abstract fun bindCollectionRepository(
        repository: CollectionRepositoryImpl
    ): CollectionRepository
    
    @Binds
    @Singleton
    abstract fun bindSyncManager(
        syncManager: SyncManagerImpl
    ): SyncManager
    
    @Binds
    @Singleton
    abstract fun bindCollectionMapper(
        mapper: CollectionMapperImpl
    ): CollectionMapper
}
