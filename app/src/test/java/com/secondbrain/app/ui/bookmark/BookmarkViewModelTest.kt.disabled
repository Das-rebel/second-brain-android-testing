package com.secondbrain.app.ui.bookmark

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.BookmarkFilter
import com.secondbrain.app.domain.usecase.BookmarkUseCases
import com.secondbrain.app.util.ErrorHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var bookmarkUseCases: BookmarkUseCases
    private lateinit var errorHandler: ErrorHandler
    private lateinit var bookmarkViewModel: BookmarkViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        bookmarkUseCases = mockk()
        errorHandler = mockk()
        
        every { errorHandler.handleError(any()) } returns "Test error message"
        
        bookmarkViewModel = BookmarkViewModel(bookmarkUseCases, errorHandler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        // Then
        with(bookmarkViewModel.uiState.value) {
            assertTrue(bookmarks.isEmpty())
            assertFalse(isLoading)
            assertFalse(isSelectionMode)
            assertTrue(selectedBookmarks.isEmpty())
            assertNull(errorMessage)
            assertEquals("", searchQuery)
        }
    }

    @Test
    fun `loadBookmarks sets loading state and loads bookmarks successfully`() = runTest {
        // Given
        val collectionId = 1L
        val testBookmarks = listOf(
            createTestBookmark(id = 1, collectionId = collectionId),
            createTestBookmark(id = 2, collectionId = collectionId)
        )
        coEvery { bookmarkUseCases.getFilteredBookmarks(any()) } returns flowOf(testBookmarks)

        // When
        bookmarkViewModel.loadBookmarks(collectionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertEquals(testBookmarks, bookmarks)
            assertFalse(isLoading)
            assertNull(errorMessage)
        }
        
        coVerify { 
            bookmarkUseCases.getFilteredBookmarks(
                BookmarkFilter(collectionId = collectionId, searchQuery = "")
            ) 
        }
    }

    @Test
    fun `loadBookmarks handles error correctly`() = runTest {
        // Given
        val collectionId = 1L
        val exception = RuntimeException("Test error")
        coEvery { bookmarkUseCases.getFilteredBookmarks(any()) } throws exception

        // When
        bookmarkViewModel.loadBookmarks(collectionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertTrue(bookmarks.isEmpty())
            assertFalse(isLoading)
            assertEquals("Test error message", errorMessage)
        }
        
        coVerify { errorHandler.handleError(exception) }
    }

    @Test
    fun `updateSearchQuery triggers bookmark reload with filter`() = runTest {
        // Given
        val collectionId = 1L
        val searchQuery = "test query"
        val filteredBookmarks = listOf(createTestBookmark(title = "Test Bookmark"))
        
        coEvery { bookmarkUseCases.getFilteredBookmarks(any()) } returns flowOf(filteredBookmarks)
        
        // Load initial bookmarks
        bookmarkViewModel.loadBookmarks(collectionId)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        bookmarkViewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(searchQuery, bookmarkViewModel.uiState.value.searchQuery)
        assertEquals(filteredBookmarks, bookmarkViewModel.uiState.value.bookmarks)
        
        coVerify { 
            bookmarkUseCases.getFilteredBookmarks(
                BookmarkFilter(collectionId = collectionId, searchQuery = searchQuery)
            ) 
        }
    }

    @Test
    fun `toggleBookmarkSelection adds bookmark to selection`() {
        // Given
        val bookmark = createTestBookmark(id = 1)
        
        // When
        bookmarkViewModel.toggleBookmarkSelection(bookmark)

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertTrue(isSelectionMode)
            assertTrue(selectedBookmarks.contains(bookmark))
        }
    }

    @Test
    fun `toggleBookmarkSelection removes bookmark from selection`() {
        // Given
        val bookmark = createTestBookmark(id = 1)
        
        // Select bookmark first
        bookmarkViewModel.toggleBookmarkSelection(bookmark)
        
        // When
        bookmarkViewModel.toggleBookmarkSelection(bookmark)

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertFalse(isSelectionMode)
            assertFalse(selectedBookmarks.contains(bookmark))
        }
    }

    @Test
    fun `clearSelection clears all selected bookmarks and exits selection mode`() {
        // Given
        val bookmark1 = createTestBookmark(id = 1)
        val bookmark2 = createTestBookmark(id = 2)
        
        bookmarkViewModel.toggleBookmarkSelection(bookmark1)
        bookmarkViewModel.toggleBookmarkSelection(bookmark2)

        // When
        bookmarkViewModel.clearSelection()

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertFalse(isSelectionMode)
            assertTrue(selectedBookmarks.isEmpty())
        }
    }

    @Test
    fun `deleteSelectedBookmarks calls use case and clears selection on success`() = runTest {
        // Given
        val bookmark1 = createTestBookmark(id = 1)
        val bookmark2 = createTestBookmark(id = 2)
        val bookmarkIds = listOf(1L, 2L)
        
        coEvery { bookmarkUseCases.deleteMultipleBookmarks(bookmarkIds) } returns Result.success(Unit)
        
        bookmarkViewModel.toggleBookmarkSelection(bookmark1)
        bookmarkViewModel.toggleBookmarkSelection(bookmark2)

        // When
        bookmarkViewModel.deleteSelectedBookmarks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertFalse(isSelectionMode)
            assertTrue(selectedBookmarks.isEmpty())
            assertNull(errorMessage)
        }
        
        coVerify { bookmarkUseCases.deleteMultipleBookmarks(bookmarkIds) }
    }

    @Test
    fun `deleteSelectedBookmarks handles error correctly`() = runTest {
        // Given
        val bookmark = createTestBookmark(id = 1)
        val exception = RuntimeException("Delete failed")
        
        coEvery { bookmarkUseCases.deleteMultipleBookmarks(any()) } returns Result.failure(exception)
        
        bookmarkViewModel.toggleBookmarkSelection(bookmark)

        // When
        bookmarkViewModel.deleteSelectedBookmarks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        with(bookmarkViewModel.uiState.value) {
            assertTrue(isSelectionMode) // Should remain in selection mode on error
            assertEquals("Test error message", errorMessage)
        }
        
        coVerify { errorHandler.handleError(exception) }
    }

    @Test
    fun `toggleFavoriteForSelected calls use case with correct bookmark IDs`() = runTest {
        // Given
        val bookmark1 = createTestBookmark(id = 1, isFavorite = false)
        val bookmark2 = createTestBookmark(id = 2, isFavorite = false)
        val bookmarkIds = listOf(1L, 2L)
        
        coEvery { bookmarkUseCases.toggleFavoriteForMultiple(bookmarkIds, true) } returns Result.success(Unit)
        
        bookmarkViewModel.toggleBookmarkSelection(bookmark1)
        bookmarkViewModel.toggleBookmarkSelection(bookmark2)

        // When
        bookmarkViewModel.toggleFavoriteForSelected(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { bookmarkUseCases.toggleFavoriteForMultiple(bookmarkIds, true) }
    }

    @Test
    fun `archiveSelectedBookmarks calls use case with correct bookmark IDs`() = runTest {
        // Given
        val bookmark1 = createTestBookmark(id = 1)
        val bookmark2 = createTestBookmark(id = 2)
        val bookmarkIds = listOf(1L, 2L)
        
        coEvery { bookmarkUseCases.archiveMultipleBookmarks(bookmarkIds, true) } returns Result.success(Unit)
        
        bookmarkViewModel.toggleBookmarkSelection(bookmark1)
        bookmarkViewModel.toggleBookmarkSelection(bookmark2)

        // When
        bookmarkViewModel.archiveSelectedBookmarks(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { bookmarkUseCases.archiveMultipleBookmarks(bookmarkIds, true) }
    }

    @Test
    fun `clearErrorMessage clears error state`() {
        // Given - simulate error state
        val collectionId = 1L
        coEvery { bookmarkUseCases.getFilteredBookmarks(any()) } throws RuntimeException("Test error")
        
        runTest {
            bookmarkViewModel.loadBookmarks(collectionId)
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // When
        bookmarkViewModel.clearErrorMessage()

        // Then
        assertNull(bookmarkViewModel.uiState.value.errorMessage)
    }

    @Test
    fun `filteredBookmarks computed property returns correct bookmarks based on search`() = runTest {
        // Given
        val allBookmarks = listOf(
            createTestBookmark(id = 1, title = "Test Bookmark"),
            createTestBookmark(id = 2, title = "Another Bookmark"),
            createTestBookmark(id = 3, title = "Different Title")
        )
        
        coEvery { bookmarkUseCases.getFilteredBookmarks(any()) } returns flowOf(allBookmarks)
        
        bookmarkViewModel.loadBookmarks(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Access computed property
        val filteredBookmarks = bookmarkViewModel.uiState.value.filteredBookmarks

        // Then
        assertEquals(allBookmarks, filteredBookmarks)
    }

    private fun createTestBookmark(
        id: Long = 1L,
        collectionId: Long = 1L,
        title: String = "Test Bookmark",
        url: String = "https://example.com",
        isFavorite: Boolean = false,
        isArchived: Boolean = false
    ): Bookmark {
        return Bookmark(
            id = id,
            collectionId = collectionId,
            title = title,
            url = url,
            description = "Test description",
            isFavorite = isFavorite,
            isArchived = isArchived,
            tags = listOf("test"),
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}