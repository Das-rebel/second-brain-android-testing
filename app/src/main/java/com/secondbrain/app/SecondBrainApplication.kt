package com.secondbrain.app

import android.app.Application
import androidx.room.Room
import com.secondbrain.app.data.database.AppDatabase
import com.secondbrain.app.data.repository.BookmarkRepository
import com.secondbrain.app.data.repository.CollectionRepository
import com.secondbrain.app.util.WebMetadataExtractor

/**
 * Application class for the Second Brain app.
 */
class SecondBrainApplication : Application() {
    
    // Database
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    // Repositories
    val bookmarkRepository by lazy {
        BookmarkRepository(
            database.bookmarkDao(),
            database.collectionDao(),
            WebMetadataExtractor()
        )
    }
    
    val collectionRepository by lazy {
        CollectionRepository(
            database.collectionDao(),
            database.bookmarkDao()
        )
    }
}