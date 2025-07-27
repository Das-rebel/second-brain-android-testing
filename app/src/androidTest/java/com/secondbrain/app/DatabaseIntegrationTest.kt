package com.secondbrain.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.secondbrain.app.data.database.AppDatabase
import com.secondbrain.app.data.database.dao.BookmarkDao
import com.secondbrain.app.data.database.dao.CollectionDao
import com.secondbrain.app.data.database.entities.BookmarkEntity
import com.secondbrain.app.data.database.entities.CollectionEntity
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.data.repository.BookmarkRepository
import com.secondbrain.app.data.repository.CollectionRepository
import com.secondbrain.app.util.WebMetadataExtractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * Comprehensive database integration test for Second Brain app.
 * Tests database initialization, entities, DAOs, repositories, and sample data.
 */
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var collectionDao: CollectionDao
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var collectionRepository: CollectionRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        
        bookmarkDao = database.bookmarkDao()
        collectionDao = database.collectionDao()
        
        // Initialize repositories
        bookmarkRepository = BookmarkRepository(
            bookmarkDao,
            collectionDao,
            WebMetadataExtractor()
        )
        collectionRepository = CollectionRepository(
            collectionDao,
            bookmarkDao
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testDatabaseInitialization() {
        // Test that database was created successfully
        assertNotNull(database)
        assertTrue(database.isOpen)
        
        // Test that DAOs are accessible
        assertNotNull(bookmarkDao)
        assertNotNull(collectionDao)
    }

    @Test
    fun testCollectionEntityOperations() = runBlocking {
        // Test collection insertion
        val collection = CollectionEntity(
            name = "Test Collection",
            description = "A test collection",
            color = "#FF0000",
            isDefault = false
        )
        
        val id = collectionDao.insertCollection(collection)
        assertTrue("Collection ID should be greater than 0", id > 0)
        
        // Test collection retrieval
        val retrievedCollection = collectionDao.getCollectionById(id)
        assertNotNull("Retrieved collection should not be null", retrievedCollection)
        assertEquals("Collection names should match", "Test Collection", retrievedCollection?.name)
        assertEquals("Collection colors should match", "#FF0000", retrievedCollection?.color)
        
        // Test collection search
        val searchResults = collectionDao.searchCollections("Test").first()
        assertEquals("Should find one collection", 1, searchResults.size)
        assertEquals("Found collection should match", "Test Collection", searchResults[0].name)
        
        // Test collection update
        val updatedCollection = retrievedCollection!!.copy(
            name = "Updated Test Collection",
            description = "Updated description"
        )
        collectionDao.updateCollection(updatedCollection)
        
        val afterUpdate = collectionDao.getCollectionById(id)
        assertEquals("Name should be updated", "Updated Test Collection", afterUpdate?.name)
        assertEquals("Description should be updated", "Updated description", afterUpdate?.description)
        
        // Test collection deletion
        collectionDao.deleteCollectionById(id)
        val afterDelete = collectionDao.getCollectionById(id)
        assertNull("Collection should be deleted", afterDelete)
    }

    @Test
    fun testBookmarkEntityOperations() = runBlocking {
        // First create a collection for foreign key relationship
        val collection = CollectionEntity(
            name = "Test Collection",
            description = "For bookmark testing"
        )
        val collectionId = collectionDao.insertCollection(collection)
        
        // Test bookmark insertion
        val bookmark = BookmarkEntity(
            title = "Test Bookmark",
            url = "https://example.com",
            description = "A test bookmark",
            domain = "example.com",
            tags = listOf("test", "bookmark"),
            collectionId = collectionId,
            isFavorite = true
        )
        
        val bookmarkId = bookmarkDao.insertBookmark(bookmark)
        assertTrue("Bookmark ID should be greater than 0", bookmarkId > 0)
        
        // Test bookmark retrieval
        val retrievedBookmark = bookmarkDao.getBookmarkById(bookmarkId)
        assertNotNull("Retrieved bookmark should not be null", retrievedBookmark)
        assertEquals("Bookmark titles should match", "Test Bookmark", retrievedBookmark?.title)
        assertEquals("Bookmark URLs should match", "https://example.com", retrievedBookmark?.url)
        assertTrue("Bookmark should be favorite", retrievedBookmark?.isFavorite == true)
        assertEquals("Collection ID should match", collectionId, retrievedBookmark?.collectionId)
        
        // Test bookmark search
        val searchResults = bookmarkDao.searchBookmarks("Test").first()
        assertEquals("Should find one bookmark", 1, searchResults.size)
        
        // Test tag search
        val tagResults = bookmarkDao.getBookmarksByTag("test").first()
        assertEquals("Should find bookmark by tag", 1, tagResults.size)
        
        // Test favorite status update
        bookmarkDao.updateFavoriteStatus(bookmarkId, false)
        val afterFavoriteUpdate = bookmarkDao.getBookmarkById(bookmarkId)
        assertFalse("Bookmark should not be favorite", afterFavoriteUpdate?.isFavorite == true)
        
        // Test archived status update
        bookmarkDao.updateArchivedStatus(bookmarkId, true)
        val afterArchiveUpdate = bookmarkDao.getBookmarkById(bookmarkId)
        assertTrue("Bookmark should be archived", afterArchiveUpdate?.isArchived == true)
        
        // Test open count increment
        val initialOpenCount = afterArchiveUpdate?.openCount ?: 0
        bookmarkDao.incrementOpenCount(bookmarkId)
        val afterIncrement = bookmarkDao.getBookmarkById(bookmarkId)
        assertEquals("Open count should be incremented", initialOpenCount + 1, afterIncrement?.openCount)
        
        // Test bookmark deletion
        bookmarkDao.deleteBookmarkById(bookmarkId)
        val afterDelete = bookmarkDao.getBookmarkById(bookmarkId)
        assertNull("Bookmark should be deleted", afterDelete)
    }

    @Test
    fun testForeignKeyConstraints() = runBlocking {
        // Create a collection
        val collection = CollectionEntity(name = "FK Test Collection")
        val collectionId = collectionDao.insertCollection(collection)
        
        // Create a bookmark in this collection
        val bookmark = BookmarkEntity(
            title = "FK Test Bookmark",
            url = "https://fktest.com",
            collectionId = collectionId
        )
        val bookmarkId = bookmarkDao.insertBookmark(bookmark)
        
        // Verify bookmark is in collection
        val bookmarksInCollection = bookmarkDao.getBookmarksByCollection(collectionId).first()
        assertEquals("Should have one bookmark in collection", 1, bookmarksInCollection.size)
        
        // Delete collection (should set bookmark's collection_id to null due to SET_NULL)
        collectionDao.deleteCollectionById(collectionId)
        
        val bookmarkAfterCollectionDelete = bookmarkDao.getBookmarkById(bookmarkId)
        assertNotNull("Bookmark should still exist", bookmarkAfterCollectionDelete)
        assertNull("Bookmark's collection ID should be null", bookmarkAfterCollectionDelete?.collectionId)
    }

    @Test
    fun testRepositoryOperations() = runBlocking {
        // Test collection repository
        val collection = Collection.create(
            name = "Repository Test Collection",
            description = "Testing repository pattern"
        )
        
        val collectionId = collectionRepository.insertCollection(collection)
        assertTrue("Collection ID should be greater than 0", collectionId > 0)
        
        val retrievedCollection = collectionRepository.getCollectionById(collectionId)
        assertNotNull("Retrieved collection should not be null", retrievedCollection)
        assertEquals("Collection names should match", "Repository Test Collection", retrievedCollection?.name)
        
        // Test bookmark repository
        val bookmark = Bookmark.create(
            title = "Repository Test Bookmark",
            url = "https://repotest.com",
            description = "Testing repository pattern",
            tags = listOf("repository", "test"),
            collectionId = collectionId
        )
        
        val bookmarkId = bookmarkRepository.insertBookmark(bookmark)
        assertTrue("Bookmark ID should be greater than 0", bookmarkId > 0)
        
        val retrievedBookmark = bookmarkRepository.getBookmarkById(bookmarkId)
        assertNotNull("Retrieved bookmark should not be null", retrievedBookmark)
        assertEquals("Bookmark titles should match", "Repository Test Bookmark", retrievedBookmark?.title)
        
        // Test repository search functionality
        val searchResults = bookmarkRepository.searchBookmarks("Repository").first()
        assertTrue("Should find bookmarks in search", searchResults.isNotEmpty())
        
        // Test tag operations
        val allTags = bookmarkRepository.getAllTags()
        assertTrue("Should have tags", allTags.contains("repository"))
        assertTrue("Should have tags", allTags.contains("test"))
        
        // Test counts
        val bookmarkCount = bookmarkRepository.getBookmarkCount()
        assertTrue("Should have bookmarks", bookmarkCount > 0)
        
        val collectionCount = collectionRepository.getCollectionCount()
        assertTrue("Should have collections", collectionCount > 0)
    }

    @Test
    fun testSampleDataInsertion() = runBlocking {
        // Test inserting sample collections
        val sampleCollections = Collection.getSampleCollections()
        assertTrue("Should have sample collections", sampleCollections.isNotEmpty())
        
        for (collection in sampleCollections) {
            val id = collectionRepository.insertCollection(collection)
            assertTrue("Sample collection should be inserted", id > 0)
        }
        
        val allCollections = collectionRepository.getAllCollections().first()
        assertEquals("Should have all sample collections", sampleCollections.size, allCollections.size)
        
        // Test inserting sample bookmarks
        val sampleBookmarks = Bookmark.getSampleBookmarks()
        assertTrue("Should have sample bookmarks", sampleBookmarks.isNotEmpty())
        
        for (bookmark in sampleBookmarks) {
            val id = bookmarkRepository.insertBookmark(bookmark)
            assertTrue("Sample bookmark should be inserted", id > 0)
        }
        
        val allBookmarks = bookmarkRepository.getAllBookmarks().first()
        assertEquals("Should have all sample bookmarks", sampleBookmarks.size, allBookmarks.size)
        
        // Test specific sample data properties
        val favoriteBookmarks = bookmarkRepository.getFavoriteBookmarks().first()
        assertTrue("Should have favorite bookmarks", favoriteBookmarks.isNotEmpty())
        
        val archivedBookmarks = bookmarkRepository.getArchivedBookmarks().first()
        assertTrue("Should have archived bookmarks", archivedBookmarks.isNotEmpty())
    }

    @Test
    fun testDataModelIntegrity() = runBlocking {
        // Test Bookmark model
        val bookmark = Bookmark.create(
            title = "  Model Test  ",
            url = "  https://modeltest.com  ",
            tags = listOf("  model  ", "  test  ", "")
        )
        
        assertEquals("Title should be trimmed", "Model Test", bookmark.title)
        assertEquals("URL should be trimmed", "https://modeltest.com", bookmark.url)
        assertEquals("Tags should be cleaned", listOf("model", "test"), bookmark.tags)
        assertTrue("Created date should be set", bookmark.createdAt.time <= Date().time)
        
        // Test computed domain
        assertEquals("Domain should be computed correctly", "modeltest.com", bookmark.computedDomain)
        
        // Test Collection model
        val collection = Collection.create(
            name = "  Model Collection Test  ",
            description = "  Test description  "
        )
        
        assertEquals("Name should be trimmed", "Model Collection Test", collection.name)
        assertEquals("Description should be trimmed", "Test description", collection.description)
        assertEquals("Default color should be set", "#6366F1", collection.color)
        assertTrue("Created date should be set", collection.createdAt.time <= Date().time)
    }

    @Test
    fun testEntityConversions() = runBlocking {
        // Test BookmarkEntity to Bookmark conversion
        val entity = BookmarkEntity(
            id = 1,
            title = "Entity Test",
            url = "https://entitytest.com",
            description = "Testing entity conversion",
            tags = listOf("entity", "test"),
            isFavorite = true
        )
        
        val bookmark = entity.toBookmark()
        assertEquals("ID should match", entity.id, bookmark.id)
        assertEquals("Title should match", entity.title, bookmark.title)
        assertEquals("URL should match", entity.url, bookmark.url)
        assertEquals("Description should match", entity.description, bookmark.description)
        assertEquals("Tags should match", entity.tags, bookmark.tags)
        assertEquals("Favorite status should match", entity.isFavorite, bookmark.isFavorite)
        
        // Test Bookmark to BookmarkEntity conversion
        val convertedEntity = BookmarkEntity.fromBookmark(bookmark)
        assertEquals("Converted entity should match original", entity.title, convertedEntity.title)
        assertEquals("Converted entity should match original", entity.url, convertedEntity.url)
        assertEquals("Converted entity should match original", entity.isFavorite, convertedEntity.isFavorite)
        
        // Test CollectionEntity to Collection conversion
        val collectionEntity = CollectionEntity(
            id = 1,
            name = "Entity Collection Test",
            description = "Testing collection entity conversion",
            color = "#FF0000",
            isDefault = true
        )
        
        val collection = collectionEntity.toCollection()
        assertEquals("ID should match", collectionEntity.id, collection.id)
        assertEquals("Name should match", collectionEntity.name, collection.name)
        assertEquals("Description should match", collectionEntity.description, collection.description)
        assertEquals("Color should match", collectionEntity.color, collection.color)
        assertEquals("Default status should match", collectionEntity.isDefault, collection.isDefault)
        
        // Test Collection to CollectionEntity conversion
        val convertedCollectionEntity = CollectionEntity.fromCollection(collection)
        assertEquals("Converted entity should match original", collectionEntity.name, convertedCollectionEntity.name)
        assertEquals("Converted entity should match original", collectionEntity.color, convertedCollectionEntity.color)
        assertEquals("Converted entity should match original", collectionEntity.isDefault, convertedCollectionEntity.isDefault)
    }

    @Test
    fun testDatabasePersistence() = runBlocking {
        // Insert test data
        val collection = CollectionEntity(name = "Persistence Test")
        val collectionId = collectionDao.insertCollection(collection)
        
        val bookmark = BookmarkEntity(
            title = "Persistence Bookmark",
            url = "https://persistencetest.com",
            collectionId = collectionId
        )
        val bookmarkId = bookmarkDao.insertBookmark(bookmark)
        
        // Verify data is persisted
        val persistedCollection = collectionDao.getCollectionById(collectionId)
        val persistedBookmark = bookmarkDao.getBookmarkById(bookmarkId)
        
        assertNotNull("Collection should be persisted", persistedCollection)
        assertNotNull("Bookmark should be persisted", persistedBookmark)
        assertEquals("Collection name should persist", "Persistence Test", persistedCollection?.name)
        assertEquals("Bookmark title should persist", "Persistence Bookmark", persistedBookmark?.title)
        
        // Test that relationships are maintained
        val bookmarksInCollection = bookmarkDao.getBookmarksByCollection(collectionId).first()
        assertEquals("Should have bookmark in collection", 1, bookmarksInCollection.size)
        assertEquals("Bookmark should be the correct one", bookmarkId, bookmarksInCollection[0].id)
    }

    @Test
    fun testOfflineFirstArchitecture() = runBlocking {
        // Test that all operations work without network (offline-first)
        // This simulates the offline-first architecture where all data operations
        // should work locally regardless of network connectivity
        
        // Collection operations
        val collection = Collection.create("Offline Collection", "Works offline")
        val collectionId = collectionRepository.insertCollection(collection)
        assertTrue("Offline collection creation should work", collectionId > 0)
        
        val retrievedCollection = collectionRepository.getCollectionById(collectionId)
        assertNotNull("Offline collection retrieval should work", retrievedCollection)
        
        // Bookmark operations
        val bookmark = Bookmark.create(
            title = "Offline Bookmark",
            url = "https://offline.test",
            description = "Works without network",
            collectionId = collectionId
        )
        val bookmarkId = bookmarkRepository.insertBookmark(bookmark)
        assertTrue("Offline bookmark creation should work", bookmarkId > 0)
        
        val retrievedBookmark = bookmarkRepository.getBookmarkById(bookmarkId)
        assertNotNull("Offline bookmark retrieval should work", retrievedBookmark)
        
        // Search operations
        val searchResults = bookmarkRepository.searchBookmarks("Offline").first()
        assertTrue("Offline search should work", searchResults.isNotEmpty())
        
        // Update operations
        bookmarkRepository.updateFavoriteStatus(bookmarkId, true)
        val updatedBookmark = bookmarkRepository.getBookmarkById(bookmarkId)
        assertTrue("Offline update should work", updatedBookmark?.isFavorite == true)
        
        // Delete operations
        bookmarkRepository.deleteBookmarkById(bookmarkId)
        val deletedBookmark = bookmarkRepository.getBookmarkById(bookmarkId)
        assertNull("Offline delete should work", deletedBookmark)
    }
}