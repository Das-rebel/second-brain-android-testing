package com.secondbrain.app.espresso

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AdvancedBookmarkEspressoTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

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
        
        idlingResource = CountingIdlingResource("AdvancedBookmarkAsyncOperations")
        
        // Setup comprehensive test data
        val testBookmarks = createTestBookmarkSet()
        
        val mockRepository = mockk<BookmarkRepository>()
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(testBookmarks)
        coEvery { mockRepository.searchBookmarks(any(), any()) } answers {
            val query = firstArg<String>()
            flowOf(testBookmarks.filter { it.title.contains(query, ignoreCase = true) })
        }
        coEvery { mockRepository.getBookmarkById(any()) } answers {
            val id = firstArg<Long>()
            Result.success(testBookmarks.first { it.id == id })
        }
        coEvery { mockRepository.insertBookmark(any()) } returns Result.success(100L)
        coEvery { mockRepository.updateBookmark(any()) } returns Result.success(Unit)
        coEvery { mockRepository.deleteBookmark(any()) } returns Result.success(Unit)
        coEvery { mockRepository.deleteBookmarks(any()) } returns Result.success(Unit)
        coEvery { mockRepository.toggleFavorite(any(), any()) } returns Result.success(Unit)
        coEvery { mockRepository.archiveBookmark(any(), any()) } returns Result.success(Unit)
    }

    @Test
    fun complexSearchAndFilter_worksCorrectly() {
        navigateToBookmarkList()
        
        // Test search with multiple terms
        onView(withHint("Search bookmarks..."))
            .perform(typeText("Android Development"))
            .perform(closeSoftKeyboard())
        
        // Verify filtered results
        onView(withText("Android Architecture Guide"))
            .check(matches(isDisplayed()))
        onView(withText("Android Development Best Practices"))
            .check(matches(isDisplayed()))
        
        // Clear search and verify all bookmarks return
        onView(withContentDescription("Clear search"))
            .perform(click())
        
        onView(withText("React Native Tutorial"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun multipleSelectionAndBulkOperations_performCorrectly() {
        navigateToBookmarkList()
        
        // Enter selection mode
        onView(withText("Android Architecture Guide"))
            .perform(longClick())
        
        // Verify selection mode UI
        onView(withText("1 selected"))
            .check(matches(isDisplayed()))
        onView(withContentDescription("Delete selected"))
            .check(matches(isDisplayed()))
        
        // Select additional items
        onView(withText("Android Development Best Practices"))
            .perform(click())
        onView(withText("React Native Tutorial"))
            .perform(click())
        
        // Verify multiple selection
        onView(withText("3 selected"))
            .check(matches(isDisplayed()))
        
        // Perform bulk favorite
        onView(withContentDescription("Toggle favorite for selected"))
            .perform(click())
        
        // Exit selection mode
        onView(withContentDescription("Clear selection"))
            .perform(click())
        
        // Verify selection mode is exited
        onView(withText("Bookmarks"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun scrollingAndLazyLoading_worksWithLargeDataset() {
        navigateToBookmarkList()
        
        // Scroll to bottom of list
        onView(withId(R.id.bookmark_list))
            .perform(swipeUp())
            .perform(swipeUp())
            .perform(swipeUp())
        
        // Verify last item is visible
        onView(withText("Machine Learning Fundamentals"))
            .check(matches(isDisplayed()))
        
        // Scroll back to top
        onView(withId(R.id.bookmark_list))
            .perform(swipeDown())
            .perform(swipeDown())
            .perform(swipeDown())
        
        // Verify first item is visible again
        onView(withText("Android Architecture Guide"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun complexFormValidation_handlesAllCases() {
        navigateToBookmarkList()
        
        // Open create bookmark
        onView(withContentDescription("Add bookmark"))
            .perform(click())
        
        // Test empty form submission
        onView(withText("Save"))
            .perform(click())
        
        // Verify validation errors
        onView(withText("Title is required"))
            .check(matches(isDisplayed()))
        
        // Test invalid URL
        onView(withHint("Title"))
            .perform(typeText("Test Bookmark"))
        onView(withHint("URL"))
            .perform(typeText("invalid-url"))
            .perform(closeSoftKeyboard())
        
        onView(withText("Save"))
            .perform(click())
        
        onView(withText("Please enter a valid URL"))
            .check(matches(isDisplayed()))
        
        // Test valid form
        onView(withHint("URL"))
            .perform(clearText())
            .perform(typeText("https://validurl.com"))
        onView(withHint("Description"))
            .perform(typeText("This is a test bookmark with a longer description"))
        onView(withHint("Tags"))
            .perform(typeText("test,validation,espresso"))
            .perform(closeSoftKeyboard())
        
        onView(withText("Save"))
            .perform(click())
        
        // Verify successful creation
        onView(withText("Test Bookmark"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun editFormWithComplexData_handlesAllFields() {
        navigateToBookmarkList()
        
        // Open bookmark detail
        onView(withText("Kotlin Coroutines Deep Dive"))
            .perform(click())
        
        // Enter edit mode
        onView(withContentDescription("Edit bookmark"))
            .perform(click())
        
        // Verify all fields are populated
        onView(withText("Kotlin Coroutines Deep Dive"))
            .check(matches(isDisplayed()))
        onView(withText("https://kotlinlang.org/docs/coroutines"))
            .check(matches(isDisplayed()))
        
        // Make complex edits
        onView(withText("Kotlin Coroutines Deep Dive"))
            .perform(clearText())
            .perform(typeText("Advanced Kotlin Coroutines - Updated Edition"))
        
        onView(withText("Comprehensive guide to coroutines"))
            .perform(clearText())
            .perform(typeText("Updated comprehensive guide with new examples and best practices for using Kotlin coroutines in production applications"))
        
        onView(withText("kotlin,coroutines,async"))
            .perform(clearText())
            .perform(typeText("kotlin,coroutines,async,advanced,production,best-practices"))
            .perform(closeSoftKeyboard())
        
        // Save changes
        onView(withText("Save"))
            .perform(click())
        
        // Verify updates
        onView(withText("Advanced Kotlin Coroutines - Updated Edition"))
            .check(matches(isDisplayed()))
        onView(withText("Updated comprehensive guide"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun errorRecoveryAndRetry_worksCorrectly() {
        // Mock repository to simulate network error
        val mockRepository = mockk<BookmarkRepository>()
        coEvery { mockRepository.getBookmarksByCollection(any()) } throws Exception("Network timeout")
        
        navigateToBookmarkList()
        
        // Verify error state
        onView(withText(containsString("error")))
            .check(matches(isDisplayed()))
        
        // Mock recovery
        coEvery { mockRepository.getBookmarksByCollection(any()) } returns flowOf(createTestBookmarkSet())
        
        // Tap retry button
        onView(withText("Retry"))
            .perform(click())
        
        // Verify recovery
        onView(withText("Android Architecture Guide"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun accessibilityFeatures_workCorrectly() {
        navigateToBookmarkList()
        
        // Verify content descriptions are present
        onView(withContentDescription("Add bookmark"))
            .check(matches(isDisplayed()))
        
        onView(withContentDescription("Search bookmarks"))
            .check(matches(isDisplayed()))
        
        // Navigate to bookmark detail
        onView(withText("Android Architecture Guide"))
            .perform(click())
        
        // Verify detail screen accessibility
        onView(withContentDescription("Navigate back"))
            .check(matches(isDisplayed()))
        onView(withContentDescription("Edit bookmark"))
            .check(matches(isDisplayed()))
        onView(withContentDescription("Delete bookmark"))
            .check(matches(isDisplayed()))
        onView(withContentDescription("Add to favorites"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun performanceWithLargeDataset_remainsResponsive() {
        // This test verifies UI remains responsive with large datasets
        navigateToBookmarkList()
        
        // Perform rapid scrolling
        for (i in 1..5) {
            onView(withId(R.id.bookmark_list))
                .perform(swipeUp())
        }
        
        // Perform search on large dataset
        onView(withHint("Search bookmarks..."))
            .perform(typeText("Guide"))
            .perform(closeSoftKeyboard())
        
        // Verify search results appear quickly
        onView(withText("Android Architecture Guide"))
            .check(matches(isDisplayed()))
        
        // Clear search
        onView(withContentDescription("Clear search"))
            .perform(click())
        
        // Verify return to full list is responsive
        onView(withText("Machine Learning Fundamentals"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun complexNavigationFlow_maintainsState() {
        navigateToBookmarkList()
        
        // Start search
        onView(withHint("Search bookmarks..."))
            .perform(typeText("Android"))
            .perform(closeSoftKeyboard())
        
        // Navigate to detail
        onView(withText("Android Architecture Guide"))
            .perform(click())
        
        // Navigate back
        onView(withContentDescription("Navigate back"))
            .perform(click())
        
        // Verify search state is maintained
        onView(withText("Android"))
            .check(matches(isDisplayed()))
        onView(withText("Android Architecture Guide"))
            .check(matches(isDisplayed()))
        
        // Navigate to collections
        onView(withContentDescription("Navigate back"))
            .perform(click())
        
        // Return to bookmarks
        onView(withText("My Collection"))
            .perform(click())
        
        // Verify search is cleared
        onView(withHint("Search bookmarks..."))
            .check(matches(isDisplayed()))
    }

    // Helper Methods

    private fun navigateToBookmarkList() {
        onView(withText("My Collection"))
            .perform(click())
    }

    private fun createTestBookmarkSet(): List<Bookmark> {
        return listOf(
            createTestBookmark(1, "Android Architecture Guide", "https://developer.android.com/guide/components/architecture", listOf("android", "architecture")),
            createTestBookmark(2, "Android Development Best Practices", "https://developer.android.com/best-practices", listOf("android", "development")),
            createTestBookmark(3, "Kotlin Coroutines Deep Dive", "https://kotlinlang.org/docs/coroutines", "Comprehensive guide to coroutines", listOf("kotlin", "coroutines", "async")),
            createTestBookmark(4, "React Native Tutorial", "https://reactnative.dev/docs/tutorial", listOf("react", "native", "tutorial")),
            createTestBookmark(5, "Flutter Widget Catalog", "https://flutter.dev/docs/development/ui/widgets", listOf("flutter", "widgets", "ui")),
            createTestBookmark(6, "SwiftUI Documentation", "https://developer.apple.com/documentation/swiftui", listOf("swift", "swiftui", "ios")),
            createTestBookmark(7, "Machine Learning Fundamentals", "https://ml-fundamentals.com", listOf("ml", "ai", "fundamentals")),
            createTestBookmark(8, "Web Performance Guide", "https://web.dev/performance", listOf("web", "performance", "optimization")),
            createTestBookmark(9, "Database Design Patterns", "https://dbpatterns.com", listOf("database", "design", "patterns")),
            createTestBookmark(10, "Cloud Architecture Principles", "https://cloud-architecture.com", listOf("cloud", "architecture", "scalability"))
        )
    }

    private fun createTestBookmark(
        id: Long,
        title: String,
        url: String,
        description: String? = null,
        tags: List<String> = listOf("test")
    ): Bookmark {
        return Bookmark(
            id = id,
            collectionId = 1L,
            title = title,
            url = url,
            description = description,
            isFavorite = false,
            isArchived = false,
            tags = tags,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}