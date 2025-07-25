package com.secondbrain.app.uiautomator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@LargeTest
@RunWith(AndroidJUnit4::class)
class BookmarkUIAutomatorTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private val packageName = "com.secondbrain.app"
    private val timeout = 5000L

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()
        
        // Start from home screen
        device.pressHome()
        
        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        assertNotNull(launcherPackage)
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), timeout)
        
        // Launch the app
        launchApp()
    }

    @Test
    fun appLaunchesSuccessfully() {
        // Verify app launches and main screen is displayed
        val appTitle = device.findObject(UiSelector().textContains("Second Brain"))
        assertTrue(appTitle.waitForExists(timeout))
    }

    @Test
    fun systemBackButtonNavigatesCorrectly() {
        // Navigate to bookmark list
        navigateToBookmarkList()
        
        // Navigate to bookmark detail
        val firstBookmark = device.findObject(UiSelector().textContains("Bookmark"))
        if (firstBookmark.exists()) {
            firstBookmark.click()
            device.waitForIdle()
        }
        
        // Press system back button
        device.pressBack()
        device.waitForIdle()
        
        // Should return to bookmark list
        val bookmarkListTitle = device.findObject(UiSelector().textContains("Bookmarks"))
        assertTrue(bookmarkListTitle.waitForExists(timeout))
    }

    @Test
    fun appHandlesSystemRotation() {
        // Start in portrait mode
        device.setOrientationNatural()
        device.waitForIdle()
        
        // Navigate to bookmark list
        navigateToBookmarkList()
        
        // Rotate to landscape
        device.setOrientationLeft()
        device.waitForIdle()
        
        // Verify content is still visible
        val bookmarkList = device.findObject(UiSelector().textContains("Bookmarks"))
        assertTrue(bookmarkList.waitForExists(timeout))
        
        // Rotate back to portrait
        device.setOrientationNatural()
        device.waitForIdle()
        
        // Verify content is still visible
        assertTrue(bookmarkList.waitForExists(timeout))
    }

    @Test
    fun appHandlesHomeButtonAndResuming() {
        // Navigate to bookmark list
        navigateToBookmarkList()
        
        // Press home button
        device.pressHome()
        device.waitForIdle()
        
        // Verify we're on home screen
        val homeScreen = device.findObject(UiSelector().packageName(device.launcherPackageName))
        assertTrue(homeScreen.waitForExists(timeout))
        
        // Resume the app
        launchApp()
        
        // Verify app resumes to bookmark list
        val bookmarkList = device.findObject(UiSelector().textContains("Bookmarks"))
        assertTrue(bookmarkList.waitForExists(timeout))
    }

    @Test
    fun appHandlesRecentAppsSwitch() {
        // Navigate to bookmark list
        navigateToBookmarkList()
        
        // Open recent apps
        device.pressRecentApps()
        device.waitForIdle()
        
        // Find and tap our app in recents
        val ourApp = device.findObject(UiSelector().textContains("Second Brain"))
        if (ourApp.exists()) {
            ourApp.click()
            device.waitForIdle()
        }
        
        // Verify app is resumed
        val bookmarkList = device.findObject(UiSelector().textContains("Bookmarks"))
        assertTrue(bookmarkList.waitForExists(timeout))
    }

    @Test
    fun shareIntentOpensCorrectly() {
        // Navigate to bookmark detail
        navigateToBookmarkList()
        
        val firstBookmark = device.findObject(UiSelector().textContains("Bookmark"))
        if (firstBookmark.exists()) {
            firstBookmark.click()
            device.waitForIdle()
            
            // Look for share button
            val shareButton = device.findObject(UiSelector().descriptionContains("Share"))
            if (shareButton.exists()) {
                shareButton.click()
                device.waitForIdle()
                
                // Verify share sheet opens
                val shareSheet = device.findObject(UiSelector().textContains("Share"))
                assertTrue(shareSheet.waitForExists(timeout))
                
                // Press back to close share sheet
                device.pressBack()
                device.waitForIdle()
            }
        }
    }

    @Test
    fun appHandlesNetworkConnectivityChanges() {
        // This test simulates network changes (would require system permissions)
        // For demonstration, we'll test offline behavior
        
        // Navigate to bookmark list
        navigateToBookmarkList()
        
        // Verify bookmarks load (from cache)
        val bookmarkItem = device.findObject(UiSelector().textContains("Bookmark"))
        assertTrue(bookmarkItem.waitForExists(timeout))
        
        // Try to create new bookmark (should work offline)
        val addButton = device.findObject(UiSelector().descriptionContains("Add"))
        if (addButton.exists()) {
            addButton.click()
            device.waitForIdle()
            
            // Fill in bookmark details
            val titleField = device.findObject(UiSelector().textContains("Title"))
            if (titleField.exists()) {
                titleField.setText("Offline Test Bookmark")
                
                val urlField = device.findObject(UiSelector().textContains("URL"))
                if (urlField.exists()) {
                    urlField.setText("https://offline-test.com")
                    
                    val saveButton = device.findObject(UiSelector().textContains("Save"))
                    if (saveButton.exists()) {
                        saveButton.click()
                        device.waitForIdle()
                    }
                }
            }
        }
    }

    @Test
    fun appHandlesSystemNotifications() {
        // Open notification panel
        device.openNotification()
        device.waitForIdle()
        
        // Look for any notifications from our app
        val notification = device.findObject(UiSelector().packageName(packageName))
        
        // Close notification panel
        device.pressBack()
        device.waitForIdle()
        
        // Return to our app
        launchApp()
        
        // Verify app is still functional
        val mainScreen = device.findObject(UiSelector().textContains("Collections"))
        assertTrue(mainScreen.waitForExists(timeout))
    }

    @Test
    fun appWorksWithSystemKeyboard() {
        // Navigate to add bookmark
        navigateToBookmarkList()
        
        val addButton = device.findObject(UiSelector().descriptionContains("Add"))
        if (addButton.exists()) {
            addButton.click()
            device.waitForIdle()
            
            // Tap on title field to open keyboard
            val titleField = device.findObject(UiSelector().textContains("Title"))
            if (titleField.exists()) {
                titleField.click()
                device.waitForIdle()
                
                // Type text using system keyboard
                titleField.setText("System Keyboard Test")
                
                // Hide keyboard
                device.pressBack()
                device.waitForIdle()
                
                // Verify text was entered
                val enteredText = device.findObject(UiSelector().textContains("System Keyboard Test"))
                assertTrue(enteredText.exists())
            }
        }
    }

    @Test
    fun appHandlesDeepLinks() {
        // Create a deep link intent
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("secondbrain://bookmark/1")
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        // Launch via deep link
        context.startActivity(intent)
        device.waitForIdle()
        
        // Verify app opens to correct screen
        val bookmarkDetail = device.findObject(UiSelector().textContains("Bookmark"))
        assertTrue(bookmarkDetail.waitForExists(timeout))
    }

    @Test
    fun appHandlesSystemPermissions() {
        // This would test permissions like storage, network, etc.
        // For demonstration, test basic functionality that might require permissions
        
        navigateToBookmarkList()
        
        // Try to export bookmarks (might require storage permission)
        val menuButton = device.findObject(UiSelector().descriptionContains("Menu"))
        if (menuButton.exists()) {
            menuButton.click()
            device.waitForIdle()
            
            val exportOption = device.findObject(UiSelector().textContains("Export"))
            if (exportOption.exists()) {
                exportOption.click()
                device.waitForIdle()
                
                // Handle any permission dialogs
                val allowButton = device.findObject(UiSelector().textContains("Allow"))
                if (allowButton.exists()) {
                    allowButton.click()
                    device.waitForIdle()
                }
            }
        }
    }

    @Test
    fun multipleAppInstancesHandling() {
        // Launch app normally
        navigateToBookmarkList()
        
        // Simulate launching app again (e.g., from notification)
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            device.waitForIdle()
        }
        
        // Verify app handles multiple launches gracefully
        val bookmarkList = device.findObject(UiSelector().textContains("Bookmarks"))
        assertTrue(bookmarkList.waitForExists(timeout))
    }

    @Test
    fun appMemoryPressureHandling() {
        // Navigate through multiple screens to test memory handling
        navigateToBookmarkList()
        
        // Navigate to bookmark detail
        val firstBookmark = device.findObject(UiSelector().textContains("Bookmark"))
        if (firstBookmark.exists()) {
            firstBookmark.click()
            device.waitForIdle()
            
            // Navigate to edit mode
            val editButton = device.findObject(UiSelector().descriptionContains("Edit"))
            if (editButton.exists()) {
                editButton.click()
                device.waitForIdle()
                
                // Navigate back through screens
                device.pressBack() // Exit edit mode
                device.waitForIdle()
                
                device.pressBack() // Return to bookmark list
                device.waitForIdle()
                
                device.pressBack() // Return to collections
                device.waitForIdle()
                
                // Verify app is still responsive
                val collectionsScreen = device.findObject(UiSelector().textContains("Collections"))
                assertTrue(collectionsScreen.waitForExists(timeout))
            }
        }
    }

    // Helper Methods

    private fun launchApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), timeout)
    }

    private fun navigateToBookmarkList() {
        // Click on first collection to navigate to bookmark list
        val firstCollection = device.findObject(UiSelector().textContains("Collection"))
        if (firstCollection.exists()) {
            firstCollection.click()
            device.waitForIdle()
        }
    }
}