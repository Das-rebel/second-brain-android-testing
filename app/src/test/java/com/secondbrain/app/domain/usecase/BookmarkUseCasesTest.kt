package com.secondbrain.app.domain.usecase

import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.BookmarkFilter
import com.secondbrain.app.domain.model.ValidationResult
import com.secondbrain.app.domain.repository.BookmarkRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class BookmarkUseCasesTest {

    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var bookmarkValidationUseCase: BookmarkValidationUseCase
    private lateinit var bookmarkUseCases: BookmarkUseCases

    @Before
    fun setup() {
        bookmarkRepository = mockk()
        bookmarkValidationUseCase = mockk()
        bookmarkUseCases = BookmarkUseCases(bookmarkRepository, bookmarkValidationUseCase)
    }

    @Test
    fun `getAllBookmarks returns flow of bookmarks from repository`() = runTest {
        // Given
        val expectedBookmarks = listOf(
            createTestBookmark(id = 1, title = "Test Bookmark 1"),
            createTestBookmark(id = 2, title = "Test Bookmark 2")
        )
        coEvery { bookmarkRepository.getAllBookmarks() } returns flowOf(expectedBookmarks)

        // When
        val result = bookmarkUseCases.getAllBookmarks()

        // Then
        result.collect { bookmarks ->
            assertEquals(expectedBookmarks, bookmarks)
        }
        coVerify { bookmarkRepository.getAllBookmarks() }
    }

    @Test
    fun `getBookmarksByCollection returns filtered bookmarks`() = runTest {
        // Given
        val collectionId = 1L
        val expectedBookmarks = listOf(
            createTestBookmark(id = 1, collectionId = collectionId)
        )
        coEvery { bookmarkRepository.getBookmarksByCollection(collectionId) } returns flowOf(expectedBookmarks)

        // When
        val result = bookmarkUseCases.getBookmarksByCollection(collectionId)

        // Then
        result.collect { bookmarks ->
            assertEquals(expectedBookmarks, bookmarks)
        }
        coVerify { bookmarkRepository.getBookmarksByCollection(collectionId) }
    }

    @Test
    fun `getBookmarkById returns success when bookmark exists`() = runTest {
        // Given
        val bookmarkId = 1L
        val expectedBookmark = createTestBookmark(id = bookmarkId)
        coEvery { bookmarkRepository.getBookmarkById(bookmarkId) } returns Result.success(expectedBookmark)

        // When
        val result = bookmarkUseCases.getBookmarkById(bookmarkId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedBookmark, result.getOrNull())
        coVerify { bookmarkRepository.getBookmarkById(bookmarkId) }
    }

    @Test
    fun `getBookmarkById returns failure when bookmark does not exist`() = runTest {
        // Given
        val bookmarkId = 999L
        val exception = NoSuchElementException("Bookmark not found")
        coEvery { bookmarkRepository.getBookmarkById(bookmarkId) } returns Result.failure(exception)

        // When
        val result = bookmarkUseCases.getBookmarkById(bookmarkId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { bookmarkRepository.getBookmarkById(bookmarkId) }
    }

    @Test
    fun `createBookmark validates input and calls repository on success`() = runTest {
        // Given
        val bookmark = createTestBookmark()
        val validationResult = ValidationResult.Success
        coEvery { bookmarkValidationUseCase.validateBookmark(bookmark) } returns validationResult
        coEvery { bookmarkRepository.insertBookmark(bookmark) } returns Result.success(1L)

        // When
        val result = bookmarkUseCases.createBookmark(bookmark)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { bookmarkValidationUseCase.validateBookmark(bookmark) }
        coVerify { bookmarkRepository.insertBookmark(bookmark) }
    }

    @Test
    fun `createBookmark returns validation error when input is invalid`() = runTest {
        // Given
        val bookmark = createTestBookmark(title = "")
        val validationResult = ValidationResult.Error(listOf(ValidationResult.ValidationError.TITLE_EMPTY))
        coEvery { bookmarkValidationUseCase.validateBookmark(bookmark) } returns validationResult

        // When
        val result = bookmarkUseCases.createBookmark(bookmark)

        // Then
        assertTrue(result.isFailure)
        coVerify { bookmarkValidationUseCase.validateBookmark(bookmark) }
        coVerify(exactly = 0) { bookmarkRepository.insertBookmark(any()) }
    }

    @Test
    fun `updateBookmark validates input and calls repository on success`() = runTest {
        // Given
        val bookmark = createTestBookmark()
        val validationResult = ValidationResult.Success
        coEvery { bookmarkValidationUseCase.validateBookmark(bookmark) } returns validationResult
        coEvery { bookmarkRepository.updateBookmark(bookmark) } returns Result.success(Unit)

        // When
        val result = bookmarkUseCases.updateBookmark(bookmark)

        // Then
        assertTrue(result.isSuccess)
        coVerify { bookmarkValidationUseCase.validateBookmark(bookmark) }
        coVerify { bookmarkRepository.updateBookmark(bookmark) }
    }

    @Test
    fun `deleteBookmark calls repository with correct id`() = runTest {
        // Given
        val bookmarkId = 1L
        coEvery { bookmarkRepository.deleteBookmark(bookmarkId) } returns Result.success(Unit)

        // When
        val result = bookmarkUseCases.deleteBookmark(bookmarkId)

        // Then
        assertTrue(result.isSuccess)
        coVerify { bookmarkRepository.deleteBookmark(bookmarkId) }
    }

    @Test
    fun `toggleFavorite calls repository with correct parameters`() = runTest {
        // Given
        val bookmarkId = 1L
        val isFavorite = true
        coEvery { bookmarkRepository.toggleFavorite(bookmarkId, isFavorite) } returns Result.success(Unit)

        // When
        val result = bookmarkUseCases.toggleFavorite(bookmarkId, isFavorite)

        // Then
        assertTrue(result.isSuccess)
        coVerify { bookmarkRepository.toggleFavorite(bookmarkId, isFavorite) }
    }

    @Test
    fun `archiveBookmark calls repository with correct parameters`() = runTest {
        // Given
        val bookmarkId = 1L
        val isArchived = true
        coEvery { bookmarkRepository.archiveBookmark(bookmarkId, isArchived) } returns Result.success(Unit)

        // When
        val result = bookmarkUseCases.archiveBookmark(bookmarkId, isArchived)

        // Then
        assertTrue(result.isSuccess)
        coVerify { bookmarkRepository.archiveBookmark(bookmarkId, isArchived) }
    }

    @Test
    fun `searchBookmarks returns filtered results`() = runTest {
        // Given
        val query = "test"
        val expectedBookmarks = listOf(createTestBookmark(title = "Test Bookmark"))
        coEvery { bookmarkRepository.searchBookmarks(query) } returns flowOf(expectedBookmarks)

        // When
        val result = bookmarkUseCases.searchBookmarks(query)

        // Then
        result.collect { bookmarks ->
            assertEquals(expectedBookmarks, bookmarks)
        }
        coVerify { bookmarkRepository.searchBookmarks(query) }
    }

    @Test
    fun `getFilteredBookmarks applies filter correctly`() = runTest {
        // Given
        val filter = BookmarkFilter(
            collectionId = 1L,
            isFavorite = true,
            searchQuery = "test"
        )
        val expectedBookmarks = listOf(createTestBookmark(isFavorite = true))
        coEvery { bookmarkRepository.getFilteredBookmarks(filter) } returns flowOf(expectedBookmarks)

        // When
        val result = bookmarkUseCases.getFilteredBookmarks(filter)

        // Then
        result.collect { bookmarks ->
            assertEquals(expectedBookmarks, bookmarks)
        }
        coVerify { bookmarkRepository.getFilteredBookmarks(filter) }
    }

    @Test
    fun `deleteMultipleBookmarks calls repository for each bookmark`() = runTest {
        // Given
        val bookmarkIds = listOf(1L, 2L, 3L)
        coEvery { bookmarkRepository.deleteMultipleBookmarks(bookmarkIds) } returns Result.success(Unit)

        // When
        val result = bookmarkUseCases.deleteMultipleBookmarks(bookmarkIds)

        // Then
        assertTrue(result.isSuccess)
        coVerify { bookmarkRepository.deleteMultipleBookmarks(bookmarkIds) }
    }

    @Test
    fun `updateLastOpened calls repository with correct id`() = runTest {
        // Given
        val bookmarkId = 1L
        coEvery { bookmarkRepository.updateLastOpened(bookmarkId) } returns Result.success(Unit)

        // When
        val result = bookmarkUseCases.updateLastOpened(bookmarkId)

        // Then
        assertTrue(result.isSuccess)
        coVerify { bookmarkRepository.updateLastOpened(bookmarkId) }
    }

    @Test
    fun `isValidBookmarkId returns true for positive ids`() {
        assertTrue(bookmarkUseCases.isValidBookmarkId(1L))
        assertTrue(bookmarkUseCases.isValidBookmarkId(100L))
    }

    @Test
    fun `isValidBookmarkId returns false for non-positive ids`() {
        assertFalse(bookmarkUseCases.isValidBookmarkId(0L))
        assertFalse(bookmarkUseCases.isValidBookmarkId(-1L))
    }

    @Test
    fun `sanitizeSearchQuery trims whitespace and handles empty strings`() {
        assertEquals("test", bookmarkUseCases.sanitizeSearchQuery("  test  "))
        assertEquals("", bookmarkUseCases.sanitizeSearchQuery("   "))
        assertEquals("", bookmarkUseCases.sanitizeSearchQuery(""))
        assertEquals("multi word", bookmarkUseCases.sanitizeSearchQuery("  multi word  "))
    }

    private fun createTestBookmark(
        id: Long = 1L,
        collectionId: Long = 1L,
        title: String = "Test Bookmark",
        url: String = "https://example.com",
        description: String? = "Test description",
        isFavorite: Boolean = false,
        isArchived: Boolean = false,
        tags: List<String> = emptyList()
    ): Bookmark {
        return Bookmark(
            id = id,
            collectionId = collectionId,
            title = title,
            url = url,
            description = description,
            isFavorite = isFavorite,
            isArchived = isArchived,
            tags = tags,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}