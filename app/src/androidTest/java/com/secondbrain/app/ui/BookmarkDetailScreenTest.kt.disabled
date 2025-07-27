package com.secondbrain.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.ui.bookmark.BookmarkDetailScreen
import com.secondbrain.app.ui.bookmark.BookmarkDetailState
import com.secondbrain.app.ui.theme.SecondBrainTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class BookmarkDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bookmarkDetailScreen_displaysBookmarkInfo_whenLoaded() {
        // Given
        val testBookmark = createTestBookmark(
            title = "Test Bookmark Title",
            url = "https://example.com",
            description = "Test bookmark description",
            tags = listOf("tag1", "tag2", "technology")
        )
        
        val state = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = null
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Test Bookmark Title")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("https://example.com")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Test bookmark description")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("tag1")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("tag2")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("technology")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkDetailScreen_displaysLoadingState_whenLoading() {
        // Given
        val loadingState = BookmarkDetailState(
            bookmark = null,
            isLoading = true,
            isEditing = false,
            errorMessage = null
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = loadingState,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Loading")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkDetailScreen_showsFavoriteIcon_whenBookmarkIsFavorite() {
        // Given
        val favoriteBookmark = createTestBookmark(isFavorite = true)
        val state = BookmarkDetailState(
            bookmark = favoriteBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = null
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then - Check that favorite icon is displayed
        composeTestRule
            .onNodeWithContentDescription("Remove from favorites")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkDetailScreen_showsUnfavoriteIcon_whenBookmarkIsNotFavorite() {
        // Given
        val nonFavoriteBookmark = createTestBookmark(isFavorite = false)
        val state = BookmarkDetailState(
            bookmark = nonFavoriteBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = null
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then - Check that unfavorite icon is displayed
        composeTestRule
            .onNodeWithContentDescription("Add to favorites")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkDetailScreen_togglesFavorite_whenFavoriteButtonClicked() {
        // Given
        val testBookmark = createTestBookmark(isFavorite = false)
        val state = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = null
        )
        
        var favoriteToggled = false

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = state,
                    onNavigateBack = {},
                    onToggleFavorite = { favoriteToggled = true },
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Add to favorites")
            .performClick()

        assert(favoriteToggled)
    }

    @Test
    fun bookmarkDetailScreen_entersEditMode_whenEditButtonClicked() {
        // Given
        val testBookmark = createTestBookmark()
        val state = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = null
        )
        
        var editClicked = false

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = { editClicked = true },
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Edit bookmark")
            .performClick()

        assert(editClicked)
    }

    @Test
    fun bookmarkDetailScreen_showsEditFields_whenInEditMode() {
        // Given
        val testBookmark = createTestBookmark(
            title = "Editable Title",
            url = "https://editable.com",
            description = "Editable description"
        )
        val editState = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = true,
            errorMessage = null
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = editState,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then - Check that edit fields are displayed
        composeTestRule
            .onNodeWithText("Title")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("URL")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Description")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Tags")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkDetailScreen_saveEditedBookmark_whenSaveButtonClicked() {
        // Given
        val testBookmark = createTestBookmark(
            title = "Original Title",
            url = "https://original.com",
            description = "Original description"
        )
        val editState = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = true,
            errorMessage = null
        )
        
        var savedTitle = ""
        var savedUrl = ""
        var savedDescription = ""
        var savedTags = ""

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = editState,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { title, url, description, tags ->
                        savedTitle = title
                        savedUrl = url
                        savedDescription = description ?: ""
                        savedTags = tags
                    },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Edit the fields
        composeTestRule
            .onNodeWithText("Original Title")
            .performTextClearance()
        composeTestRule
            .onNodeWithText("Title")
            .performTextInput("New Title")

        // Save the changes
        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        // Then
        assert(savedTitle == "New Title")
    }

    @Test
    fun bookmarkDetailScreen_cancelsEdit_whenCancelButtonClicked() {
        // Given
        val testBookmark = createTestBookmark()
        val editState = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = true,
            errorMessage = null
        )
        
        var cancelClicked = false

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = editState,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = { cancelClicked = true },
                    onErrorDismiss = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        assert(cancelClicked)
    }

    @Test
    fun bookmarkDetailScreen_displaysMetadata_whenBookmarkHasMetadata() {
        // Given
        val testDate = Date()
        val testBookmark = createTestBookmark(
            createdAt = testDate,
            updatedAt = testDate,
            lastOpened = testDate,
            openCount = 5
        )
        val state = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = null
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = state,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then - Check metadata is displayed
        composeTestRule
            .onNodeWithText("Created")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Last Updated")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Opened 5 times")
            .assertIsDisplayed()
    }

    @Test
    fun bookmarkDetailScreen_displaysErrorSnackbar_whenErrorOccurs() {
        // Given
        val testBookmark = createTestBookmark()
        val errorState = BookmarkDetailState(
            bookmark = testBookmark,
            isLoading = false,
            isEditing = false,
            errorMessage = "Test error occurred"
        )

        // When
        composeTestRule.setContent {
            SecondBrainTheme {
                BookmarkDetailScreen(
                    uiState = errorState,
                    onNavigateBack = {},
                    onToggleFavorite = {},
                    onToggleArchive = {},
                    onDelete = {},
                    onEdit = {},
                    onSave = { _, _, _, _ -> },
                    onCancel = {},
                    onErrorDismiss = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Test error occurred")
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
        tags: List<String> = listOf("test"),
        createdAt: Date = Date(),
        updatedAt: Date = Date(),
        lastOpened: Date? = null,
        openCount: Int = 0
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
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastOpened = lastOpened,
            openCount = openCount
        )
    }
}