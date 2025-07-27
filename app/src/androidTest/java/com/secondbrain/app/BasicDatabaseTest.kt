package com.secondbrain.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.secondbrain.app.data.database.AppDatabase
import com.secondbrain.app.data.database.entities.BookmarkEntity
import com.secondbrain.app.data.database.entities.CollectionEntity
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Basic instrumentation test for database functionality.
 * Tests core database operations with actual Room database.
 */
@RunWith(AndroidJUnit4::class)
class BasicDatabaseTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testDatabaseCreation() {
        // Verify database is created and open
        assertNotNull("Database should be created", database)
        assertTrue("Database should be open", database.isOpen)
        
        // Verify DAOs are accessible
        assertNotNull("BookmarkDao should be accessible", database.bookmarkDao())
        assertNotNull("CollectionDao should be accessible", database.collectionDao())
    }

    @Test
    fun testCollectionOperations() = runBlocking {
        val collectionDao = database.collectionDao()
        
        // Test insertion
        val collection = CollectionEntity(
            name = "Test Collection",
            description = "Test Description",
            color = "#FF0000"
        )
        
        val id = collectionDao.insertCollection(collection)
        assertTrue("Collection ID should be greater than 0", id > 0)
        
        // Test retrieval
        val retrieved = collectionDao.getCollectionById(id)
        assertNotNull("Collection should be retrieved", retrieved)
        assertEquals("Names should match", "Test Collection", retrieved?.name)
        assertEquals("Colors should match", "#FF0000", retrieved?.color)
        
        // Test update
        val updated = retrieved!!.copy(name = "Updated Collection")
        collectionDao.updateCollection(updated)
        
        val afterUpdate = collectionDao.getCollectionById(id)
        assertEquals("Name should be updated", "Updated Collection", afterUpdate?.name)
        
        // Test count
        val count = collectionDao.getCollectionCount()
        assertEquals("Should have one collection", 1, count)
    }

    @Test
    fun testBookmarkOperations() = runBlocking {
        val bookmarkDao = database.bookmarkDao()
        val collectionDao = database.collectionDao()
        
        // Create a collection first
        val collection = CollectionEntity(name = "Test Collection")
        val collectionId = collectionDao.insertCollection(collection)
        
        // Test bookmark insertion
        val bookmark = BookmarkEntity(
            title = "Test Bookmark",
            url = "https://test.com",
            description = "Test Description",
            collectionId = collectionId,
            isFavorite = true
        )
        
        val id = bookmarkDao.insertBookmark(bookmark)
        assertTrue("Bookmark ID should be greater than 0", id > 0)
        
        // Test retrieval
        val retrieved = bookmarkDao.getBookmarkById(id)
        assertNotNull("Bookmark should be retrieved", retrieved)
        assertEquals("Titles should match", "Test Bookmark", retrieved?.title)
        assertEquals("URLs should match", "https://test.com", retrieved?.url)
        assertTrue("Should be favorite", retrieved?.isFavorite == true)
        assertEquals("Collection ID should match", collectionId, retrieved?.collectionId)
        
        // Test favorite status update
        bookmarkDao.updateFavoriteStatus(id, false)
        val afterFavoriteUpdate = bookmarkDao.getBookmarkById(id)
        assertFalse("Should not be favorite", afterFavoriteUpdate?.isFavorite == true)
        
        // Test count
        val count = bookmarkDao.getBookmarkCount()
        assertEquals("Should have one bookmark", 1, count)
        
        // Test collection relationship
        val bookmarksInCollection = bookmarkDao.getBookmarksByCollection(collectionId).first()
        assertEquals("Should have one bookmark in collection", 1, bookmarksInCollection.size)
    }

    @Test
    fun testSampleDataInsertion() = runBlocking {
        val bookmarkDao = database.bookmarkDao()
        val collectionDao = database.collectionDao()
        
        // Insert sample collections
        val sampleCollections = Collection.getSampleCollections()
        for (collection in sampleCollections) {
            val entity = CollectionEntity.fromCollection(collection)
            collectionDao.insertCollection(entity)
        }
        
        val collectionCount = collectionDao.getCollectionCount()
        assertEquals("Should have all sample collections", sampleCollections.size, collectionCount)
        
        // Insert sample bookmarks
        val sampleBookmarks = Bookmark.getSampleBookmarks()
        for (bookmark in sampleBookmarks) {
            val entity = BookmarkEntity.fromBookmark(bookmark)
            bookmarkDao.insertBookmark(entity)
        }
        
        val bookmarkCount = bookmarkDao.getBookmarkCount()
        assertEquals("Should have all sample bookmarks", sampleBookmarks.size, bookmarkCount)
        
        // Test favorite bookmarks
        val favoriteCount = bookmarkDao.getFavoriteBookmarkCount()
        assertTrue("Should have favorite bookmarks", favoriteCount > 0)
        
        // Test archived bookmarks
        val archivedCount = bookmarkDao.getArchivedBookmarkCount()
        assertTrue("Should have archived bookmarks", archivedCount > 0)
    }

    @Test
    fun testSearchFunctionality() = runBlocking {
        val bookmarkDao = database.bookmarkDao()
        
        // Insert test bookmarks
        val bookmark1 = BookmarkEntity(
            title = "Android Development Guide",
            url = "https://developer.android.com",
            description = "Learn Android development",
            tags = listOf("android", "development")
        )
        
        val bookmark2 = BookmarkEntity(
            title = "Kotlin Documentation",
            url = "https://kotlinlang.org",
            description = "Kotlin programming language",
            tags = listOf("kotlin", "programming")
        )
        
        bookmarkDao.insertBookmark(bookmark1)
        bookmarkDao.insertBookmark(bookmark2)
        
        // Test title search
        val androidResults = bookmarkDao.searchBookmarks("Android").first()
        assertEquals("Should find Android bookmark", 1, androidResults.size)
        assertEquals("Should find correct bookmark", "Android Development Guide", androidResults[0].title)
        
        // Test description search
        val kotlinResults = bookmarkDao.searchBookmarks("Kotlin").first()
        assertTrue("Should find Kotlin bookmark", kotlinResults.isNotEmpty())
        
        // Test tag search
        val tagResults = bookmarkDao.getBookmarksByTag("android").first()
        assertEquals("Should find bookmark by tag", 1, tagResults.size)
    }

    @Test
    fun testForeignKeyConstraints() = runBlocking {
        val bookmarkDao = database.bookmarkDao()
        val collectionDao = database.collectionDao()
        
        // Create collection and bookmark
        val collection = CollectionEntity(name = "Test Collection")
        val collectionId = collectionDao.insertCollection(collection)
        
        val bookmark = BookmarkEntity(
            title = "Test Bookmark",
            url = "https://test.com",
            collectionId = collectionId
        )
        val bookmarkId = bookmarkDao.insertBookmark(bookmark)
        
        // Verify bookmark is in collection
        val bookmarksInCollection = bookmarkDao.getBookmarksByCollection(collectionId).first()
        assertEquals("Should have bookmark in collection", 1, bookmarksInCollection.size)
        
        // Delete collection (should set bookmark's collection_id to null)
        collectionDao.deleteCollectionById(collectionId)
        
        val bookmarkAfterDelete = bookmarkDao.getBookmarkById(bookmarkId)
        assertNotNull("Bookmark should still exist", bookmarkAfterDelete)
        assertNull("Collection ID should be null", bookmarkAfterDelete?.collectionId)
    }
}