package com.secondbrain.app.robolectric

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.repository.BookmarkRepository
import com.secondbrain.app.domain.usecase.BookmarkUseCases
import com.secondbrain.app.ui.bookmark.BookmarkListViewModel
import com.secondbrain.app.ui.bookmark.BookmarkDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28]) // Use Android API 28 for Robolectric
class BookmarkViewModelRobolectricTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<BookmarkRepository>()
    private val mockBookmarkUseCases = mockk<BookmarkUseCases>()
    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun bookmarkListViewModel_initialState_isCorrect() = runTest {
        // Given
        val testBookmarks = listOf(
            createTestBookmark(1, "Test Bookmark 1"),
            createTestBookmark(2, "Test Bookmark 2")
        )
        
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        
        // When
        val viewModel = BookmarkListViewModel(mockRepository, savedStateHandle)
        
        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(2, initialState.bookmarks.size)
            assertFalse(initialState.isLoading)
            assertFalse(initialState.isSelectionMode)
            assertTrue(initialState.selectedBookmarks.isEmpty())
            assertEquals("", initialState.searchQuery)
        }
    }

    @Test
    fun bookmarkListViewModel_searchQuery_filtersBookmarks() = runTest {
        // Given
        val testBookmarks = listOf(
            createTestBookmark(1, "GitHub Repository"),
            createTestBookmark(2, "Android Documentation"),
            createTestBookmark(3, "GitHub Issues")
        )
        
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        coEvery { mockRepository.searchBookmarks(any(), any()) } returns flowOf(
            testBookmarks.filter { it.title.contains("GitHub") }
        )
        
        val viewModel = BookmarkListViewModel(mockRepository, savedStateHandle)
        
        // When
        viewModel.onSearchQueryChange("GitHub")
        advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("GitHub", state.searchQuery)
            assertEquals(2, state.bookmarks.size)
            assertTrue(state.bookmarks.all { it.title.contains("GitHub") })
        }
    }

    @Test
    fun bookmarkListViewModel_toggleSelection_updatesSelectionMode() = runTest {
        // Given
        val testBookmarks = listOf(createTestBookmark(1, "Test Bookmark"))
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        
        val viewModel = BookmarkListViewModel(mockRepository, savedStateHandle)
        
        // When
        viewModel.onBookmarkLongClick(testBookmarks[0])
        advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
            assertEquals(1, state.selectedBookmarks.size)
            assertTrue(state.selectedBookmarks.contains(testBookmarks[0]))
        }
    }

    @Test
    fun bookmarkListViewModel_deleteSelected_callsRepository() = runTest {
        // Given
        val testBookmarks = listOf(
            createTestBookmark(1, "Test Bookmark 1"),
            createTestBookmark(2, "Test Bookmark 2")
        )
        
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        coEvery { mockRepository.deleteBookmarks(any()) } returns Result.success(Unit)
        
        val viewModel = BookmarkListViewModel(mockRepository, savedStateHandle)
        
        // When - Enter selection mode and select bookmarks
        viewModel.onBookmarkLongClick(testBookmarks[0])
        viewModel.onBookmarkClick(testBookmarks[1]) // Additional selection
        viewModel.onDeleteSelected()
        advanceUntilIdle()
        
        // Then
        coVerify { mockRepository.deleteBookmarks(testBookmarks) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSelectionMode)
            assertTrue(state.selectedBookmarks.isEmpty())
        }
    }

    @Test
    fun bookmarkListViewModel_toggleFavoriteSelected_callsRepository() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark", isFavorite = false)
        val testBookmarks = listOf(testBookmark)
        
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        coEvery { mockRepository.toggleFavorite(any(), any()) } returns Result.success(Unit)
        
        val viewModel = BookmarkListViewModel(mockRepository, savedStateHandle)
        
        // When
        viewModel.onBookmarkLongClick(testBookmark)
        viewModel.onToggleFavoriteSelected()
        advanceUntilIdle()
        
        // Then
        coVerify { mockRepository.toggleFavorite(testBookmark.id, true) }
    }

    @Test
    fun bookmarkDetailViewModel_initialState_loadsBookmark() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark Detail")
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(testBookmark)
        
        // When
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testBookmark, state.bookmark)
            assertFalse(state.isLoading)
            assertFalse(state.isEditing)
        }
    }

    @Test
    fun bookmarkDetailViewModel_toggleFavorite_updatesBookmark() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark", isFavorite = false)
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(testBookmark)
        coEvery { mockRepository.toggleFavorite(1L, true) } returns Result.success(Unit)
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(
            testBookmark.copy(isFavorite = true)
        )
        
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // When
        viewModel.onToggleFavorite()
        advanceUntilIdle()
        
        // Then
        coVerify { mockRepository.toggleFavorite(1L, true) }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.bookmark?.isFavorite ?: false)
        }
    }

    @Test
    fun bookmarkDetailViewModel_enterEditMode_updatesState() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark")
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(testBookmark)
        
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // When
        viewModel.onEdit()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isEditing)
            assertEquals(testBookmark, state.bookmark)
        }
    }

    @Test
    fun bookmarkDetailViewModel_saveChanges_updatesBookmark() = runTest {
        // Given
        val originalBookmark = createTestBookmark(1, "Original Title")
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(originalBookmark)
        coEvery { mockRepository.updateBookmark(any()) } returns Result.success(Unit)
        
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        viewModel.onEdit()
        
        // When
        viewModel.onSave(
            title = "Updated Title",
            url = "https://updated.com",
            description = "Updated description",
            tags = "updated,test"
        )
        advanceUntilIdle()
        
        // Then
        coVerify { 
            mockRepository.updateBookmark(match { bookmark ->
                bookmark.title == "Updated Title" &&
                bookmark.url == "https://updated.com" &&
                bookmark.description == "Updated description"
            })
        }
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isEditing)
        }
    }

    @Test
    fun bookmarkDetailViewModel_cancelEdit_returnsToViewMode() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark")
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(testBookmark)
        
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        viewModel.onEdit()
        
        // When
        viewModel.onCancel()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isEditing)
        }
    }

    @Test
    fun bookmarkDetailViewModel_deleteBookmark_callsRepository() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark")
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(testBookmark)
        coEvery { mockRepository.deleteBookmark(1L) } returns Result.success(Unit)
        
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // When
        viewModel.onDelete()
        advanceUntilIdle()
        
        // Then
        coVerify { mockRepository.deleteBookmark(1L) }
    }

    @Test
    fun bookmarkDetailViewModel_archiveBookmark_callsRepository() = runTest {
        // Given
        val testBookmark = createTestBookmark(1, "Test Bookmark", isArchived = false)
        savedStateHandle["bookmarkId"] = "1"
        
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.success(testBookmark)
        coEvery { mockRepository.archiveBookmark(1L, true) } returns Result.success(Unit)
        
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // When
        viewModel.onToggleArchive()
        advanceUntilIdle()
        
        // Then
        coVerify { mockRepository.archiveBookmark(1L, true) }
    }

    @Test
    fun bookmarkListViewModel_errorHandling_showsErrorMessage() = runTest {
        // Given
        coEvery { mockRepository.getBookmarksByCollection(any()) } throws Exception("Network error")
        
        // When
        val viewModel = BookmarkListViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.errorMessage?.contains("error") == true)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun bookmarkDetailViewModel_errorHandling_showsErrorMessage() = runTest {
        // Given
        savedStateHandle["bookmarkId"] = "1"
        coEvery { mockRepository.getBookmarkById(1L) } returns Result.failure(Exception("Failed to load"))
        
        // When
        val viewModel = BookmarkDetailViewModel(mockRepository, savedStateHandle)
        advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.errorMessage?.contains("Failed") == true)
            assertFalse(state.isLoading)
        }
    }

    // Helper method
    private fun createTestBookmark(
        id: Long,
        title: String,
        url: String = "https://example.com",
        description: String? = "Test description",
        isFavorite: Boolean = false,
        isArchived: Boolean = false
    ): Bookmark {
        return Bookmark(
            id = id,
            collectionId = 1L,
            title = title,
            url = url,
            description = description,
            isFavorite = isFavorite,
            isArchived = isArchived,
            tags = listOf("test"),
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}