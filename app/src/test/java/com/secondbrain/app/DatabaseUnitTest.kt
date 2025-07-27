package com.secondbrain.app

import com.secondbrain.app.data.database.converters.Converters
import com.secondbrain.app.data.database.entities.BookmarkEntity
import com.secondbrain.app.data.database.entities.CollectionEntity
import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.util.WebMetadata
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Unit tests for database-related functionality that doesn't require Android context.
 * Tests entities, converters, data models, and business logic.
 */
class DatabaseUnitTest {

    @Test
    fun testConverters() {
        val converters = Converters()
        
        // Test Date conversion
        val date = Date()
        val timestamp = converters.dateToTimestamp(date)
        val convertedDate = converters.fromTimestamp(timestamp)
        
        assertEquals("Date conversion should be reversible", date.time, convertedDate?.time)
        
        // Test null date conversion
        val nullDate = converters.fromTimestamp(null)
        val nullTimestamp = converters.dateToTimestamp(null)
        
        assertNull("Null date should convert to null", nullDate)
        assertNull("Null timestamp should convert to null", nullTimestamp)
        
        // Test String list conversion
        val tags = listOf("tag1", "tag2", "tag3")
        val tagString = converters.fromStringList(tags)
        val convertedTags = converters.toStringList(tagString)
        
        assertEquals("String list conversion should be reversible", tags, convertedTags)
        
        // Test empty list conversion
        val emptyTags = emptyList<String>()
        val emptyTagString = converters.fromStringList(emptyTags)
        val convertedEmptyTags = converters.toStringList(emptyTagString)
        
        assertEquals("Empty list should convert correctly", emptyTags, convertedEmptyTags)
        
        // Test string list with trimming and filtering
        val messyTagString = "  tag1  ,  tag2  ,  ,  tag3  "
        val cleanedTags = converters.toStringList(messyTagString)
        val expectedTags = listOf("tag1", "tag2", "tag3")
        
        assertEquals("Messy string should be cleaned", expectedTags, cleanedTags)
    }

    @Test
    fun testBookmarkEntity() {
        val bookmark = Bookmark.create(
            title = "Test Bookmark",
            url = "https://example.com",
            description = "Test description",
            tags = listOf("test", "bookmark"),
            collectionId = 1L
        )
        
        // Test entity creation from domain model
        val entity = BookmarkEntity.fromBookmark(bookmark)
        
        assertEquals("Title should match", bookmark.title, entity.title)
        assertEquals("URL should match", bookmark.url, entity.url)
        assertEquals("Description should match", bookmark.description, entity.description)
        assertEquals("Tags should match", bookmark.tags, entity.tags)
        assertEquals("Collection ID should match", bookmark.collectionId, entity.collectionId)
        
        // Test domain model creation from entity
        val convertedBookmark = entity.toBookmark()
        
        assertEquals("Converted title should match", entity.title, convertedBookmark.title)
        assertEquals("Converted URL should match", entity.url, convertedBookmark.url)
        assertEquals("Converted description should match", entity.description, convertedBookmark.description)
        assertEquals("Converted tags should match", entity.tags, convertedBookmark.tags)
        assertEquals("Converted collection ID should match", entity.collectionId, convertedBookmark.collectionId)
    }

    @Test
    fun testCollectionEntity() {
        val collection = Collection.create(
            name = "Test Collection",
            description = "Test description",
            color = "#FF0000"
        )
        
        // Test entity creation from domain model
        val entity = CollectionEntity.fromCollection(collection)
        
        assertEquals("Name should match", collection.name, entity.name)
        assertEquals("Description should match", collection.description, entity.description)
        assertEquals("Color should match", collection.color, entity.color)
        assertEquals("Default status should match", collection.isDefault, entity.isDefault)
        
        // Test domain model creation from entity
        val convertedCollection = entity.toCollection()
        
        assertEquals("Converted name should match", entity.name, convertedCollection.name)
        assertEquals("Converted description should match", entity.description, convertedCollection.description)
        assertEquals("Converted color should match", entity.color, convertedCollection.color)
        assertEquals("Converted default status should match", entity.isDefault, convertedCollection.isDefault)
    }

    @Test
    fun testBookmarkModel() {
        // Test create function with trimming
        val bookmark = Bookmark.create(
            title = "  Test Title  ",
            url = "  https://example.com  ",
            description = "  Test description  ",
            tags = listOf("  tag1  ", "  tag2  ", "", "  tag3  "),
            collectionId = 1L
        )
        
        assertEquals("Title should be trimmed", "Test Title", bookmark.title)
        assertEquals("URL should be trimmed", "https://example.com", bookmark.url)
        assertEquals("Description should be trimmed", "Test description", bookmark.description)
        assertEquals("Tags should be cleaned", listOf("tag1", "tag2", "tag3"), bookmark.tags)
        
        // Test computed domain
        val bookmarkWithDomain = bookmark.copy(domain = "custom.domain.com")
        assertEquals("Should use explicit domain", "custom.domain.com", bookmarkWithDomain.computedDomain)
        
        val bookmarkWithoutDomain = bookmark.copy(domain = null)
        assertEquals("Should compute domain from URL", "example.com", bookmarkWithoutDomain.computedDomain)
        
        // Test invalid URL domain computation
        val invalidUrlBookmark = bookmark.copy(url = "invalid-url", domain = null)
        assertEquals("Should return empty string for invalid URL", "", invalidUrlBookmark.computedDomain)
    }

    @Test
    fun testCollectionModel() {
        // Test create function with trimming
        val collection = Collection.create(
            name = "  Test Collection  ",
            description = "  Test description  ",
            color = "#FF0000"
        )
        
        assertEquals("Name should be trimmed", "Test Collection", collection.name)
        assertEquals("Description should be trimmed", "Test description", collection.description)
        assertEquals("Color should be set", "#FF0000", collection.color)
        assertEquals("Should not be default", false, collection.isDefault)
        assertEquals("Should have zero bookmark count", 0, collection.bookmarkCount)
        assertEquals("Should not be shared", false, collection.isShared)
        
        // Test default color
        val defaultColorCollection = Collection.create("Test")
        assertEquals("Should use default color", "#6366F1", defaultColorCollection.color)
    }

    @Test
    fun testSampleData() {
        // Test sample bookmarks
        val sampleBookmarks = Bookmark.getSampleBookmarks()
        
        assertTrue("Should have sample bookmarks", sampleBookmarks.isNotEmpty())
        assertTrue("Should have at least 5 sample bookmarks", sampleBookmarks.size >= 5)
        
        // Verify sample bookmark properties
        val firstBookmark = sampleBookmarks.first()
        assertNotNull("First bookmark should have title", firstBookmark.title)
        assertNotNull("First bookmark should have URL", firstBookmark.url)
        assertTrue("First bookmark should have valid URL", firstBookmark.url.startsWith("http"))
        assertTrue("First bookmark should have tags", firstBookmark.tags.isNotEmpty())
        
        // Check for favorite bookmarks in sample data
        val favoriteBookmarks = sampleBookmarks.filter { it.isFavorite }
        assertTrue("Should have favorite bookmarks in sample data", favoriteBookmarks.isNotEmpty())
        
        // Check for archived bookmarks in sample data
        val archivedBookmarks = sampleBookmarks.filter { it.isArchived }
        assertTrue("Should have archived bookmarks in sample data", archivedBookmarks.isNotEmpty())
        
        // Test sample collections
        val sampleCollections = Collection.getSampleCollections()
        
        assertTrue("Should have sample collections", sampleCollections.isNotEmpty())
        assertTrue("Should have at least 5 sample collections", sampleCollections.size >= 5)
        
        // Verify sample collection properties
        val firstCollection = sampleCollections.first()
        assertNotNull("First collection should have name", firstCollection.name)
        assertNotNull("First collection should have description", firstCollection.description)
        assertNotNull("First collection should have color", firstCollection.color)
        assertTrue("First collection should have bookmark count", firstCollection.bookmarkCount >= 0)
        
        // Verify unique collection names
        val collectionNames = sampleCollections.map { it.name }
        val uniqueNames = collectionNames.toSet()
        assertEquals("Collection names should be unique", collectionNames.size, uniqueNames.size)
    }

    @Test
    fun testWebMetadata() {
        val metadata = WebMetadata(
            title = "Test Title",
            description = "Test Description",
            faviconUrl = "https://example.com/favicon.ico",
            domain = "example.com"
        )
        
        assertEquals("Title should be set", "Test Title", metadata.title)
        assertEquals("Description should be set", "Test Description", metadata.description)
        assertEquals("Favicon URL should be set", "https://example.com/favicon.ico", metadata.faviconUrl)
        assertEquals("Domain should be set", "example.com", metadata.domain)
        
        // Test with null values
        val nullMetadata = WebMetadata(
            title = "Title Only",
            description = "",
            faviconUrl = null,
            domain = null
        )
        
        assertEquals("Title should be set", "Title Only", nullMetadata.title)
        assertEquals("Description should be empty", "", nullMetadata.description)
        assertNull("Favicon URL should be null", nullMetadata.faviconUrl)
        assertNull("Domain should be null", nullMetadata.domain)
    }

    @Test
    fun testBookmarkValidation() {
        // Test valid bookmark creation
        val validBookmark = Bookmark.create(
            title = "Valid Bookmark",
            url = "https://valid.com",
            description = "Valid description"
        )
        
        assertTrue("Valid bookmark should have non-empty title", validBookmark.title.isNotBlank())
        assertTrue("Valid bookmark should have non-empty URL", validBookmark.url.isNotBlank())
        assertTrue("Valid bookmark should have valid URL format", validBookmark.url.startsWith("http"))
        
        // Test bookmark with minimal data
        val minimalBookmark = Bookmark.create(
            title = "Minimal",
            url = "https://minimal.com"
        )
        
        assertEquals("Minimal bookmark should have title", "Minimal", minimalBookmark.title)
        assertEquals("Minimal bookmark should have URL", "https://minimal.com", minimalBookmark.url)
        assertNull("Minimal bookmark should have null description", minimalBookmark.description)
        assertTrue("Minimal bookmark should have empty tags", minimalBookmark.tags.isEmpty())
        assertNull("Minimal bookmark should have null collection ID", minimalBookmark.collectionId)
    }

    @Test
    fun testCollectionValidation() {
        // Test valid collection creation
        val validCollection = Collection.create(
            name = "Valid Collection",
            description = "Valid description",
            color = "#FF0000"
        )
        
        assertTrue("Valid collection should have non-empty name", validCollection.name.isNotBlank())
        assertNotNull("Valid collection should have description", validCollection.description)
        assertTrue("Valid collection should have valid color format", validCollection.color.startsWith("#"))
        assertEquals("Valid collection should have 7-character color", 7, validCollection.color.length)
        
        // Test collection with minimal data
        val minimalCollection = Collection.create("Minimal")
        
        assertEquals("Minimal collection should have name", "Minimal", minimalCollection.name)
        assertNull("Minimal collection should have null description", minimalCollection.description)
        assertEquals("Minimal collection should have default color", "#6366F1", minimalCollection.color)
    }

    @Test
    fun testEntityFieldMapping() {
        val date = Date()
        
        // Test BookmarkEntity field mapping
        val bookmarkEntity = BookmarkEntity(
            id = 123L,
            title = "Entity Test",
            url = "https://entity.test",
            description = "Entity description",
            domain = "entity.test",
            faviconUrl = "https://entity.test/favicon.ico",
            tags = listOf("entity", "test"),
            collectionId = 456L,
            isFavorite = true,
            isArchived = false,
            openCount = 10,
            createdAt = date,
            updatedAt = date
        )
        
        // Verify all fields are preserved in conversion
        val bookmark = bookmarkEntity.toBookmark()
        assertEquals("ID should match", bookmarkEntity.id, bookmark.id)
        assertEquals("Title should match", bookmarkEntity.title, bookmark.title)
        assertEquals("URL should match", bookmarkEntity.url, bookmark.url)
        assertEquals("Description should match", bookmarkEntity.description, bookmark.description)
        assertEquals("Domain should match", bookmarkEntity.domain, bookmark.domain)
        assertEquals("Favicon URL should match", bookmarkEntity.faviconUrl, bookmark.faviconUrl)
        assertEquals("Tags should match", bookmarkEntity.tags, bookmark.tags)
        assertEquals("Collection ID should match", bookmarkEntity.collectionId, bookmark.collectionId)
        assertEquals("Favorite status should match", bookmarkEntity.isFavorite, bookmark.isFavorite)
        assertEquals("Archived status should match", bookmarkEntity.isArchived, bookmark.isArchived)
        assertEquals("Open count should match", bookmarkEntity.openCount, bookmark.openCount)
        assertEquals("Created date should match", bookmarkEntity.createdAt, bookmark.createdAt)
        assertEquals("Updated date should match", bookmarkEntity.updatedAt, bookmark.updatedAt)
        
        // Test CollectionEntity field mapping
        val collectionEntity = CollectionEntity(
            id = 789L,
            name = "Entity Collection",
            description = "Entity collection description",
            color = "#123456",
            isDefault = true,
            bookmarkCount = 42,
            isShared = true,
            createdAt = date,
            updatedAt = date
        )
        
        // Verify all fields are preserved in conversion
        val collection = collectionEntity.toCollection()
        assertEquals("ID should match", collectionEntity.id, collection.id)
        assertEquals("Name should match", collectionEntity.name, collection.name)
        assertEquals("Description should match", collectionEntity.description, collection.description)
        assertEquals("Color should match", collectionEntity.color, collection.color)
        assertEquals("Default status should match", collectionEntity.isDefault, collection.isDefault)
        assertEquals("Bookmark count should match", collectionEntity.bookmarkCount, collection.bookmarkCount)
        assertEquals("Shared status should match", collectionEntity.isShared, collection.isShared)
        assertEquals("Created date should match", collectionEntity.createdAt, collection.createdAt)
        assertEquals("Updated date should match", collectionEntity.updatedAt, collection.updatedAt)
    }
}