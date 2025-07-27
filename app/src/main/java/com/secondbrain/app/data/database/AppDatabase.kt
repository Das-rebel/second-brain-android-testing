package com.secondbrain.app.data.database

import android.content.Context
import androidx.room.*
import com.secondbrain.app.data.database.converters.Converters
import com.secondbrain.app.data.database.dao.BookmarkDao
import com.secondbrain.app.data.database.dao.CollectionDao
import com.secondbrain.app.data.database.entities.BookmarkEntity
import com.secondbrain.app.data.database.entities.CollectionEntity

/**
 * Room database for the Second Brain app.
 */
@Database(
    entities = [
        BookmarkEntity::class,
        CollectionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun collectionDao(): CollectionDao
    
    companion object {
        const val DATABASE_NAME = "second_brain_database"
    }
}

