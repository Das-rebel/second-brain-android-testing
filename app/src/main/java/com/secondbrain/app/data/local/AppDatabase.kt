package com.secondbrain.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.secondbrain.app.data.local.converter.Converters
import com.secondbrain.app.data.local.dao.BookmarkDao
import com.secondbrain.app.data.local.dao.CollectionDao
import com.secondbrain.app.data.local.entity.BookmarkEntity
import com.secondbrain.app.data.local.entity.CollectionEntity

/**
 * The Room database for this app.
 */
@Database(
    entities = [
        CollectionEntity::class,
        BookmarkEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun collectionDao(): CollectionDao
    abstract fun bookmarkDao(): BookmarkDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private const val DATABASE_NAME = "second_brain_db"
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // Fallback for other migrations
                .addCallback(object : RoomDatabase.Callback() {
                    // You can add database callbacks here if needed
                })
                .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Migration from version 1 to 2:
         * - Added serverIsFavorite and serverIsArchived columns to bookmarks table
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE bookmarks ADD COLUMN serverIsFavorite INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE bookmarks ADD COLUMN serverIsArchived INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
