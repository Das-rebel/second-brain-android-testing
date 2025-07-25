package com.secondbrain.app.espresso

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.secondbrain.app.MainActivity
import com.secondbrain.app.R
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.repository.BookmarkRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class BookmarkEspressoTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE
    )

    @Inject
    lateinit var bookmarkRepository: BookmarkRepository

    private lateinit var idlingResource: CountingIdlingResource

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Setup idling resource for async operations
        idlingResource = CountingIdlingResource("BookmarkAsyncOperations")
        
        // Setup mock repository with test data
        val mockRepository = mockk<BookmarkRepository>()
        val testBookmarks = listOf(
            createTestBookmark(1, "GitHub - Second Brain", "https://github.com/user/second-brain"),
            createTestBookmark(2, "Android Documentation", "https://developer.android.com"),
            createTestBookmark(3, "Kotlin Reference", "https://kotlinlang.org/docs")
        )
        
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        coEvery { mockRepository.getBookmarkById(any()) } returns Result.success(testBookmarks[0])
        coEvery { mockRepository.insertBookmark(any()) } returns Result.success(4L)
        coEvery { mockRepository.updateBookmark(any()) } returns Result.success(Unit)
        coEvery { mockRepository.deleteBookmark(any()) } returns Result.success(Unit)
        coEvery { mockRepository.toggleFavorite(any(), any()) } returns Result.success(Unit)
        coEvery { mockRepository.archiveBookmark(any(), any()) } returns Result.success(Unit)
        
        // Initialize intents for intent verification
        Intents.init()
        
        // Launch activity
        activityRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun userCanNavigateToBookmarkList() {
        // Given - App starts at collection list
        onView(withText("Collections"))
            .check(matches(isDisplayed()))

        // When - Click on first collection
        onView(allOf(withText(containsString("Collection")), isDisplayed()))
            .perform(click())

        // Then - Should navigate to bookmark list
        onView(withText("Bookmarks"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanSearchBookmarks() {
        // Given - Navigate to bookmark list
        navigateToBookmarkList()

        // When - Enter search query
        onView(withHint("Search bookmarks..."))
            .perform(typeText("GitHub"))
            .perform(closeSoftKeyboard())

        // Then - Should show filtered results
        onView(withText("GitHub - Second Brain"))
            .check(matches(isDisplayed()))
        
        // And - Should hide non-matching bookmarks
        onView(withText("Android Documentation"))
            .check(doesNotExist())
    }

    @Test
    fun userCanCreateNewBookmark() {
        // Given - Navigate to bookmark list
        navigateToBookmarkList()

        // When - Click add bookmark FAB
        onView(withContentDescription("Add bookmark"))
            .perform(click())

        // Then - Should open create bookmark screen
        onView(withText("Create Bookmark"))
            .check(matches(isDisplayed()))

        // When - Fill in bookmark details
        onView(withHint("Title"))
            .perform(typeText("New Test Bookmark"))
            
        onView(withHint("URL"))
            .perform(typeText("https://test.example.com"))
            
        onView(withHint("Description"))
            .perform(typeText("Test bookmark description"))
            .perform(closeSoftKeyboard())

        // And - Save bookmark
        onView(withText("Save"))
            .perform(click())

        // Then - Should return to bookmark list
        onView(withText("Bookmarks"))
            .check(matches(isDisplayed()))
            
        // And - Should show new bookmark in list
        onView(withText("New Test Bookmark"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanViewBookmarkDetails() {
        // Given - Navigate to bookmark list
        navigateToBookmarkList()

        // When - Click on a bookmark
        onView(withText("GitHub - Second Brain"))
            .perform(click())

        // Then - Should open bookmark detail screen
        onView(withText("GitHub - Second Brain"))
            .check(matches(isDisplayed()))
            
        onView(withText("https://github.com/user/second-brain"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanEditBookmark() {
        // Given - Navigate to bookmark detail
        navigateToBookmarkList()
        onView(withText("GitHub - Second Brain"))
            .perform(click())

        // When - Click edit button
        onView(withContentDescription("Edit bookmark"))
            .perform(click())

        // Then - Should enter edit mode
        onView(withText("Edit Bookmark"))
            .check(matches(isDisplayed()))

        // When - Modify title
        onView(withText("GitHub - Second Brain"))
            .perform(clearText())
            .perform(typeText("Updated GitHub Repository"))
            .perform(closeSoftKeyboard())

        // And - Save changes
        onView(withText("Save"))
            .perform(click())

        // Then - Should show updated title
        onView(withText("Updated GitHub Repository"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanToggleBookmarkFavorite() {
        // Given - Navigate to bookmark detail
        navigateToBookmarkList()
        onView(withText("GitHub - Second Brain"))
            .perform(click())

        // When - Click favorite button
        onView(withContentDescription("Add to favorites"))
            .perform(click())

        // Then - Should show as favorited
        onView(withContentDescription("Remove from favorites"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanDeleteBookmark() {
        // Given - Navigate to bookmark detail
        navigateToBookmarkList()
        onView(withText("Kotlin Reference"))
            .perform(click())

        // When - Click delete button
        onView(withContentDescription("Delete bookmark"))
            .perform(click())

        // And - Confirm deletion
        onView(withText("Delete"))
            .perform(click())

        // Then - Should return to bookmark list
        onView(withText("Bookmarks"))
            .check(matches(isDisplayed()))
            
        // And - Bookmark should be removed from list
        onView(withText("Kotlin Reference"))
            .check(doesNotExist())
    }

    @Test
    fun userCanPerformBulkSelection() {
        // Given - Navigate to bookmark list
        navigateToBookmarkList()

        // When - Long click on first bookmark to enter selection mode
        onView(withText("GitHub - Second Brain"))
            .perform(longClick())

        // Then - Should enter selection mode
        onView(withContentDescription("Delete selected"))
            .check(matches(isDisplayed()))
            
        onView(withContentDescription("Toggle favorite for selected"))
            .check(matches(isDisplayed()))

        // When - Select additional bookmark
        onView(withText("Android Documentation"))
            .perform(click())

        // And - Perform bulk favorite
        onView(withContentDescription("Toggle favorite for selected"))
            .perform(click())

        // Then - Both bookmarks should be favorited
        // (This would require checking the UI state or repository calls)
        onView(withText("2 selected"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanNavigateBackFromBookmarkDetail() {
        // Given - Navigate to bookmark detail
        navigateToBookmarkList()
        onView(withText("GitHub - Second Brain"))
            .perform(click())

        // When - Press back button
        onView(withContentDescription("Navigate back"))
            .perform(click())

        // Then - Should return to bookmark list
        onView(withText("Bookmarks"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun userCanClearSearchQuery() {
        // Given - Navigate to bookmark list and perform search
        navigateToBookmarkList()
        onView(withHint("Search bookmarks..."))
            .perform(typeText("GitHub"))
            .perform(closeSoftKeyboard())

        // When - Clear search
        onView(withContentDescription("Clear search"))
            .perform(click())

        // Then - Should show all bookmarks again
        onView(withText("GitHub - Second Brain"))
            .check(matches(isDisplayed()))
            
        onView(withText("Android Documentation"))
            .check(matches(isDisplayed()))
            
        onView(withText("Kotlin Reference"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun appShowsEmptyStateWhenNoBookmarks() {
        // Given - Mock empty repository
        val mockRepository = mockk<BookmarkRepository>()
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(emptyList())

        // When - Navigate to bookmark list
        navigateToBookmarkList()

        // Then - Should show empty state
        onView(withText("No bookmarks yet"))
            .check(matches(isDisplayed()))
            
        onView(withText("Add your first bookmark"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun appShowsLoadingStateWhileFetchingBookmarks() {
        // Given - Mock repository with delayed response
        val mockRepository = mockk<BookmarkRepository>()
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf() // Empty flow to simulate loading

        // When - Navigate to bookmark list
        navigateToBookmarkList()

        // Then - Should show loading indicator
        onView(withContentDescription("Loading"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun errorSnackbarAppearsOnNetworkError() {
        // Given - Mock repository that returns error
        val mockRepository = mockk<BookmarkRepository>()
        coEvery { mockRepository.getBookmarksByCollection(any()) } throws Exception("Network error")

        // When - Navigate to bookmark list
        navigateToBookmarkList()

        // Then - Should show error message
        onView(withText(containsString("error")))
            .check(matches(isDisplayed()))
    }

    // Helper Methods

    private fun navigateToBookmarkList() {
        // Click on first collection to navigate to bookmark list
        onView(allOf(withText(containsString("Collection")), isDisplayed()))
            .perform(click())
    }

    private fun createTestBookmark(
        id: Long,
        title: String,
        url: String,
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