package com.secondbrain.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.ui.bookmark.BookmarkListScreen
import com.secondbrain.app.ui.bookmark.BookmarkUiState
import com.secondbrain.app.ui.theme.SecondBrainTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class BookmarkListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bookmarkListScreen_displaysEmptyState_whenNoBookmarks() {
        // Given
        val emptyState = BookmarkUiState(
            bookmarks = emptyList(),
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = emptyState,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("No bookmarks yet")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add your first bookmark")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkListScreen_displaysLoadingState_whenLoading() {
        // Given
        val loadingState = BookmarkUiState(
            bookmarks = emptyList(),
            isLoading = true,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = loadingState,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Loading")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkListScreen_displaysBookmarks_whenBookmarksAvailable() {
        // Given
        val testBookmarks = listOf(
            createTestBookmark(id = 1, title = "First Bookmark", url = "https://example1.com"),
            createTestBookmark(id = 2, title = "Second Bookmark", url = "https://example2.com")
        )
        
        val stateWithBookmarks = BookmarkUiState(
            bookmarks = testBookmarks,
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = stateWithBookmarks,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("First Bookmark")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Second Bookmark")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("https://example1.com")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("https://example2.com")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkListScreen_searchField_acceptsInput() {
        // Given
        val stateWithBookmarks = BookmarkUiState(
            bookmarks = listOf(createTestBookmark()),
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )
        
        var searchQuery = ""

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = stateWithBookmarks,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = { searchQuery = it },
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Search bookmarks...")
            .assertIsDisplayed()
            .performTextInput("test search")

        assert(searchQuery == "test search")
    }

    @Test
    fun bookmarkListScreen_enterSelectionMode_whenLongClick() {
        // Given
        val testBookmarks = listOf(createTestBookmark(id = 1, title = "Test Bookmark"))
        val stateWithBookmarks = BookmarkUiState(
            bookmarks = testBookmarks,
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )
        
        var longClickedBookmark: Bookmark? = null

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = stateWithBookmarks,
                    onBookmarkClick = {},
                    onBookmarkLongClick = { longClickedBookmark = it },
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then - Long click on bookmark
        composeTestRule
            .onNodeWithText("Test Bookmark")
            .performTouchInput { longClick() }

        assert(longClickedBookmark?.id == 1L)
    }

    @Test
    fun bookmarkListScreen_displaysSelectionActions_whenInSelectionMode() {
        // Given
        val testBookmarks = listOf(createTestBookmark(id = 1, title = "Selected Bookmark"))
        val selectionState = BookmarkUiState(
            bookmarks = testBookmarks,
            isLoading = false,
            isSelectionMode = true,
            selectedBookmarks = setOf(testBookmarks[0]),
            errorMessage = null,
            searchQuery = ""
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = selectionState,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then - Check selection actions are visible
        composeTestRule
            .onNodeWithContentDescription("Delete selected")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithContentDescription("Toggle favorite for selected")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithContentDescription("Archive selected")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkListScreen_clickActions_triggerCallbacks() {
        // Given
        val testBookmark = createTestBookmark(id = 1, title = "Clickable Bookmark")
        val stateWithBookmarks = BookmarkUiState(
            bookmarks = listOf(testBookmark),
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )
        
        var clickedBookmark: Bookmark? = null
        var addBookmarkClicked = false

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = stateWithBookmarks,
                    onBookmarkClick = { clickedBookmark = it },
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = { addBookmarkClicked = true }
                )
            }
        }

        // Then - Click on bookmark
        composeTestRule
            .onNodeWithText("Clickable Bookmark")
            .performClick()

        assert(clickedBookmark?.id == 1L)

        // Click add bookmark FAB
        composeTestRule
            .onNodeWithContentDescription("Add bookmark")
            .performClick()

        assert(addBookmarkClicked)
    }

    @Test
    fun bookmarkListScreen_displaysErrorSnackbar_whenErrorOccurs() {
        // Given
        val errorState = BookmarkUiState(
            bookmarks = emptyList(),
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = "Test error message",
            searchQuery = ""
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = errorState,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Test error message")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkListScreen_favoriteBookmarks_showFavoriteIcon() {
        // Given
        val favoriteBookmark = createTestBookmark(
            id = 1,
            title = "Favorite Bookmark",
            isFavorite = true
        )
        val stateWithFavorite = BookmarkUiState(
            bookmarks = listOf(favoriteBookmark),
            isLoading = false,
            isSelectionMode = false,
            selectedBookmarks = emptySet(),
            errorMessage = null,
            searchQuery = ""
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkListScreen(
                    uiState = stateWithFavorite,
                    onBookmarkClick = {},
                    onBookmarkLongClick = {},
                    onSearchQueryChange = {},
                    onDeleteSelected = {},
                    onToggleFavoriteSelected = {},
                    onArchiveSelected = {},
                    onClearSelection = {},
                    onErrorDismiss = {},
                    onAddBookmark = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Favorite")
            .assertIsDisplayed()
    }

    private fun createTestBookmark(
        id: Long = 1L,
        collectionId: Long = 1L,
        title: String = "Test Bookmark",
        url: String = "https://example.com",
        description: String? = "Test description",
        isFavorite: Boolean = false,
        isArchived: Boolean = false,
        tags: List<String> = listOf("test")
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