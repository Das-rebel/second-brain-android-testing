package com.secondbrain.app.domain.usecase

import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.ValidationResult
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class BookmarkValidationUseCaseTest {

    private lateinit var bookmarkValidationUseCase: BookmarkValidationUseCase

    @Before
    fun setup() {
        bookmarkValidationUseCase = BookmarkValidationUseCase()
    }

    @Test
    fun `validateBookmark returns success for valid bookmark`() {
        // Given
        val validBookmark = createValidBookmark()

        // When
        val result = bookmarkValidationUseCase.validateBookmark(validBookmark)

        // Then
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateBookmark returns error for empty title`() {
        // Given
        val bookmark = createValidBookmark().copy(title = "")

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.TITLE_EMPTY))
    }

    @Test
    fun `validateBookmark returns error for blank title`() {
        // Given
        val bookmark = createValidBookmark().copy(title = "   ")

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.TITLE_EMPTY))
    }

    @Test
    fun `validateBookmark returns error for title too long`() {
        // Given
        val longTitle = "a".repeat(501) // Max is 500 characters
        val bookmark = createValidBookmark().copy(title = longTitle)

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.TITLE_TOO_LONG))
    }

    @Test
    fun `validateBookmark returns error for empty URL`() {
        // Given
        val bookmark = createValidBookmark().copy(url = "")

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.URL_EMPTY))
    }

    @Test
    fun `validateBookmark returns error for invalid URL format`() {
        // Given
        val bookmark = createValidBookmark().copy(url = "not-a-valid-url")

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.URL_INVALID))
    }

    @Test
    fun `validateBookmark accepts valid HTTP URLs`() {
        // Given
        val bookmark = createValidBookmark().copy(url = "http://example.com")

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateBookmark accepts valid HTTPS URLs`() {
        // Given
        val bookmark = createValidBookmark().copy(url = "https://example.com")

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateBookmark returns error for description too long`() {
        // Given
        val longDescription = "a".repeat(1001) // Max is 1000 characters
        val bookmark = createValidBookmark().copy(description = longDescription)

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.DESCRIPTION_TOO_LONG))
    }

    @Test
    fun `validateBookmark returns error for too many tags`() {
        // Given
        val tooManyTags = (1..21).map { "tag$it" } // Max is 20 tags
        val bookmark = createValidBookmark().copy(tags = tooManyTags)

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.TOO_MANY_TAGS))
    }

    @Test
    fun `validateBookmark returns error for empty tags`() {
        // Given
        val tagsWithEmpty = listOf("valid-tag", "", "another-tag")
        val bookmark = createValidBookmark().copy(tags = tagsWithEmpty)

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.TAG_EMPTY))
    }

    @Test
    fun `validateBookmark returns error for tags too long`() {
        // Given
        val longTag = "a".repeat(51) // Max is 50 characters per tag
        val tagsWithLong = listOf("valid-tag", longTag)
        val bookmark = createValidBookmark().copy(tags = tagsWithLong)

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.TAG_TOO_LONG))
    }

    @Test
    fun `validateBookmark returns error for invalid collection ID`() {
        // Given
        val bookmark = createValidBookmark().copy(collectionId = 0L)

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertTrue(errors.contains(ValidationResult.ValidationError.COLLECTION_ID_INVALID))
    }

    @Test
    fun `validateBookmark returns multiple errors for multiple issues`() {
        // Given
        val bookmark = createValidBookmark().copy(
            title = "", // Empty title
            url = "invalid-url", // Invalid URL
            tags = listOf("", "valid-tag") // Empty tag
        )

        // When
        val result = bookmarkValidationUseCase.validateBookmark(bookmark)

        // Then
        assertTrue(result is ValidationResult.Error)
        val errors = (result as ValidationResult.Error).errors
        assertEquals(3, errors.size)
        assertTrue(errors.contains(ValidationResult.ValidationError.TITLE_EMPTY))
        assertTrue(errors.contains(ValidationResult.ValidationError.URL_INVALID))
        assertTrue(errors.contains(ValidationResult.ValidationError.TAG_EMPTY))
    }

    @Test
    fun `sanitizeBookmark trims whitespace from fields`() {
        // Given
        val bookmark = Bookmark(
            id = 1L,
            collectionId = 1L,
            title = "  Test Title  ",
            url = "  https://example.com  ",
            description = "  Test description  ",
            tags = listOf("  tag1  ", "  tag2  ")
        )

        // When
        val sanitized = bookmarkValidationUseCase.sanitizeBookmark(bookmark)

        // Then
        assertEquals("Test Title", sanitized.title)
        assertEquals("https://example.com", sanitized.url)
        assertEquals("Test description", sanitized.description)
        assertEquals(listOf("tag1", "tag2"), sanitized.tags)
    }

    @Test
    fun `sanitizeBookmark removes empty tags`() {
        // Given
        val bookmark = createValidBookmark().copy(
            tags = listOf("valid-tag", "", "   ", "another-tag")
        )

        // When
        val sanitized = bookmarkValidationUseCase.sanitizeBookmark(bookmark)

        // Then
        assertEquals(listOf("valid-tag", "another-tag"), sanitized.tags)
    }

    @Test
    fun `sanitizeBookmark handles null description`() {
        // Given
        val bookmark = createValidBookmark().copy(description = null)

        // When
        val sanitized = bookmarkValidationUseCase.sanitizeBookmark(bookmark)

        // Then
        assertNull(sanitized.description)
    }

    @Test
    fun `sanitizeBookmark converts empty description to null`() {
        // Given
        val bookmark = createValidBookmark().copy(description = "   ")

        // When
        val sanitized = bookmarkValidationUseCase.sanitizeBookmark(bookmark)

        // Then
        assertNull(sanitized.description)
    }

    @Test
    fun `isValidUrl returns true for valid URLs`() {
        assertTrue(bookmarkValidationUseCase.isValidUrl("https://example.com"))
        assertTrue(bookmarkValidationUseCase.isValidUrl("http://example.com"))
        assertTrue(bookmarkValidationUseCase.isValidUrl("https://www.example.com/path?query=value"))
        assertTrue(bookmarkValidationUseCase.isValidUrl("http://localhost:8080"))
    }

    @Test
    fun `isValidUrl returns false for invalid URLs`() {
        assertFalse(bookmarkValidationUseCase.isValidUrl(""))
        assertFalse(bookmarkValidationUseCase.isValidUrl("not-a-url"))
        assertFalse(bookmarkValidationUseCase.isValidUrl("ftp://example.com"))
        assertFalse(bookmarkValidationUseCase.isValidUrl("example.com"))
        assertFalse(bookmarkValidationUseCase.isValidUrl("http://"))
    }

    private fun createValidBookmark(): Bookmark {
        return Bookmark(
            id = 1L,
            collectionId = 1L,
            title = "Valid Test Bookmark",
            url = "https://example.com",
            description = "A valid test bookmark",
            tags = listOf("test", "bookmark"),
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}