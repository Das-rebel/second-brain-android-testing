package com.secondbrain.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.secondbrain.app.ui.bookmark.BookmarkDetailScreen
import com.secondbrain.app.ui.bookmark.BookmarkListScreen
import com.secondbrain.app.ui.collection.detail.CollectionDetailScreen
import com.secondbrain.app.ui.collection.edit.EditCollectionScreen
import com.secondbrain.app.ui.collection.list.CollectionListScreen
import com.secondbrain.app.ui.collection.share.ShareCollectionScreen
import com.secondbrain.app.ui.components.WindowSizeClass
import com.secondbrain.app.ui.settings.SettingsScreen
import com.secondbrain.app.ui.settings.about.AboutScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.CollectionList.route
) {
    val actions = remember(navController) { AppActions(navController) }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Collection List Screen
        composable(Screen.CollectionList.route) {
            CollectionListScreen(
                onNavigateToCollection = { collectionId ->
                    navController.navigate(Screen.CollectionDetail.createRoute(collectionId))
                },
                onNavigateToCreateCollection = {
                    navController.navigate(Screen.CreateCollection.route)
                },
                onNavigateToShareCollection = { collectionId ->
                    navController.navigate(Screen.ShareCollection.createRoute(collectionId))
                }
            )
        }
        
        // Collection Detail Screen
        composable(
            route = Screen.CollectionDetail.route,
            arguments = listOf(navArgument(Screen.COLLECTION_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong(Screen.COLLECTION_ID_ARG) ?: return@composable
            CollectionDetailScreen(
                collectionId = collectionId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEditCollection = {
                    navController.navigate(Screen.EditCollection.createRoute(collectionId))
                }
            )
        }
        
        // Create Collection Screen
        composable(Screen.CreateCollection.route) {
            EditCollectionScreen(
                onNavigateBack = { navController.navigateUp() },
                onCollectionSaved = { navController.navigateUp() }
            )
        }
        
        // Edit Collection Screen
        composable(
            route = Screen.EditCollection.route,
            arguments = listOf(navArgument(Screen.COLLECTION_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong(Screen.COLLECTION_ID_ARG) ?: return@composable
            EditCollectionScreen(
                collectionId = collectionId,
                onNavigateBack = { navController.navigateUp() },
                onCollectionSaved = { navController.navigateUp() }
            )
        }
        
        // Share Collection Screen
        composable(
            route = Screen.ShareCollection.route,
            arguments = listOf(navArgument(Screen.COLLECTION_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong(Screen.COLLECTION_ID_ARG) ?: return@composable
            ShareCollectionScreen(
                collectionId = collectionId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }
        
        // About Screen
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Bookmark List Screen
        composable(
            route = Screen.BookmarkList.route,
            arguments = listOf(navArgument(Screen.COLLECTION_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong(Screen.COLLECTION_ID_ARG) ?: return@composable
            BookmarkListScreen(
                onNavigateToBookmark = { bookmarkId ->
                    navController.navigate(Screen.BookmarkDetail.createRoute(bookmarkId))
                },
                onNavigateToAddBookmark = {
                    navController.navigate(Screen.CreateBookmark.route)
                },
                windowSizeClass = WindowSizeClass.Compact
            )
        }
        
        // Bookmark Detail Screen
        composable(
            route = Screen.BookmarkDetail.route,
            arguments = listOf(navArgument(Screen.BOOKMARK_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val bookmarkId = backStackEntry.arguments?.getLong(Screen.BOOKMARK_ID_ARG) ?: return@composable
            BookmarkDetailScreen(
                bookmarkId = bookmarkId,
                onBackClick = { navController.navigateUp() },
                onBookmarkDeleted = { navController.navigateUp() }
            )
        }
        
        // Create Bookmark Screen
        composable(Screen.CreateBookmark.route) {
            // For now, just navigate back - this would be implemented later
            navController.navigateUp()
        }
        
        // Edit Bookmark Screen
        composable(
            route = Screen.EditBookmark.route,
            arguments = listOf(navArgument(Screen.BOOKMARK_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val bookmarkId = backStackEntry.arguments?.getLong(Screen.BOOKMARK_ID_ARG) ?: return@composable
            // For now, just navigate back - this would be implemented later
            navController.navigateUp()
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class AppActions(private val navController: NavHostController) {
    fun navigateToCollectionList() {
        navController.navigate(Screen.CollectionList.route) {
            popUpTo(Screen.CollectionList.route) { inclusive = true }
        }
    }
    
    fun navigateToCollectionDetail(collectionId: Long) {
        navController.navigate(Screen.CollectionDetail.createRoute(collectionId))
    }
    
    fun navigateToCreateCollection() {
        navController.navigate(Screen.CreateCollection.route)
    }
    
    fun navigateToEditCollection(collectionId: Long) {
        navController.navigate(Screen.EditCollection.createRoute(collectionId))
    }
    
    fun navigateToShareCollection(collectionId: Long) {
        navController.navigate(Screen.ShareCollection.createRoute(collectionId))
    }
    
    fun navigateToSettings() {
        navController.navigate(Screen.Settings.route)
    }
    
    fun navigateToAbout() {
        navController.navigate(Screen.About.route)
    }
    
    fun navigateBack() {
        navController.navigateUp()
    }
}
