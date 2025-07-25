package com.secondbrain.app.data.repository

import com.secondbrain.app.data.local.dao.BookmarkDao
import com.secondbrain.app.data.local.entity.BookmarkEntity
import com.secondbrain.app.data.network.BookmarkApiService
import com.secondbrain.app.data.network.NetworkConnectivityManager
import com.secondbrain.app.data.network.dto.BookmarkDto
import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.util.ErrorHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response
import java.util.*

class BookmarkRepositoryImplTest {

    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var apiService: BookmarkApiService
    private lateinit var networkConnectivityManager: NetworkConnectivityManager
    private lateinit var errorHandler: ErrorHandler
    private lateinit var repository: BookmarkRepositoryImpl

    @Before
    fun setup() {
        bookmarkDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        networkConnectivityManager = mockk()
        errorHandler = mockk(relaxed = true)
        
        repository = BookmarkRepositoryImpl(
            bookmarkDao,
            apiService,
            networkConnectivityManager,
            errorHandler
        )
    }

    @Test
    fun `getBookmarksByCollection returns flow of bookmarks from dao`() = runTest {
        // Given
        val collectionId = 1L
        val entities = listOf(
            createTestBookmarkEntity(id = 1, collectionId = collectionId),
            createTestBookmarkEntity(id = 2, collectionId = collectionId)
        )
        coEvery { bookmarkDao.observeBookmarksByCollection(collectionId) } returns flowOf(entities)

        // When
        val result = repository.getBookmarksByCollection(collectionId).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals(1L, result[0][0].id)
        assertEquals(2L, result[0][1].id)
        
        coVerify { bookmarkDao.observeBookmarksByCollection(collectionId) }
    }

    @Test
    fun `getBookmarkById returns success when bookmark exists`() = runTest {
        // Given
        val bookmarkId = 1L
        val entity = createTestBookmarkEntity(id = bookmarkId)
        coEvery { bookmarkDao.getBookmarkById(bookmarkId) } returns entity

        // When
        val result = repository.getBookmarkById(bookmarkId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(bookmarkId, result.getOrNull()?.id)
        
        coVerify { bookmarkDao.getBookmarkById(bookmarkId) }
    }

    @Test
    fun `getBookmarkById returns failure when bookmark not found`() = runTest {
        // Given
        val bookmarkId = 999L
        coEvery { bookmarkDao.getBookmarkById(bookmarkId) } returns null

        // When
        val result = repository.getBookmarkById(bookmarkId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoSuchElementException)
        
        coVerify { bookmarkDao.getBookmarkById(bookmarkId) }
    }

    @Test
    fun `insertBookmark saves to local database and syncs when connected`() = runTest {
        // Given
        val bookmark = createTestBookmark()
        val insertedId = 1L
        
        every { networkConnectivityManager.isConnected() } returns true
        coEvery { bookmarkDao.insertBookmark(any()) } returns insertedId
        coEvery { apiService.createBookmark(any()) } returns createTestBookmarkDto(id = insertedId)

        // When
        val result = repository.insertBookmark(bookmark)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(insertedId, result.getOrNull())
        
        coVerify { bookmarkDao.insertBookmark(any()) }
        coVerify { apiService.createBookmark(any()) }
    }

    @Test
    fun `insertBookmark saves locally when offline`() = runTest {
        // Given
        val bookmark = createTestBookmark()
        val insertedId = 1L
        
        every { networkConnectivityManager.isConnected() } returns false
        coEvery { bookmarkDao.insertBookmark(any()) } returns insertedId

        // When
        val result = repository.insertBookmark(bookmark)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(insertedId, result.getOrNull())
        
        coVerify { bookmarkDao.insertBookmark(any()) }
        coVerify(exactly = 0) { apiService.createBookmark(any()) }
    }

    @Test
    fun `updateBookmark updates local database and syncs when connected`() = runTest {
        // Given
        val bookmark = createTestBookmark(id = 1L)
        
        every { networkConnectivityManager.isConnected() } returns true
        coEvery { bookmarkDao.updateBookmark(any()) } returns Unit
        coEvery { apiService.updateBookmark(any(), any()) } returns createTestBookmarkDto(id = 1L)

        // When
        val result = repository.updateBookmark(bookmark)

        // Then
        assertTrue(result.isSuccess)
        
        coVerify { bookmarkDao.updateBookmark(any()) }
        coVerify { apiService.updateBookmark(1L, any()) }
    }

    @Test
    fun `deleteBookmark marks as deleted locally and syncs when connected`() = runTest {
        // Given
        val bookmarkId = 1L
        val entity = createTestBookmarkEntity(id = bookmarkId)
        
        every { networkConnectivityManager.isConnected() } returns true
        coEvery { bookmarkDao.getBookmarkById(bookmarkId) } returns entity
        coEvery { bookmarkDao.updateBookmark(any()) } returns Unit
        coEvery { apiService.deleteBookmark(bookmarkId) } returns Response.success(Unit)

        // When
        val result = repository.deleteBookmark(bookmarkId)

        // Then
        assertTrue(result.isSuccess)
        
        coVerify { bookmarkDao.getBookmarkById(bookmarkId) }
        coVerify { bookmarkDao.updateBookmark(any()) }
        coVerify { apiService.deleteBookmark(bookmarkId) }
    }

    @Test
    fun `toggleFavorite updates local state and syncs when connected`() = runTest {
        // Given
        val bookmarkId = 1L
        val isFavorite = true
        
        every { networkConnectivityManager.isConnected() } returns true
        coEvery { bookmarkDao.updateFavoriteStatus(any(), any(), any()) } returns Unit
        coEvery { bookmarkDao.getBookmarkById(bookmarkId) } returns createTestBookmarkEntity(id = bookmarkId)
        coEvery { bookmarkDao.updateBookmark(any()) } returns Unit
        coEvery { apiService.updateBookmarkFavorite(any(), any()) } returns Response.success(Unit)

        // When
        val result = repository.toggleFavorite(bookmarkId, isFavorite)

        // Then
        assertTrue(result.isSuccess)
        
        coVerify { bookmarkDao.updateFavoriteStatus(bookmarkId, isFavorite, any()) }
        coVerify { apiService.updateBookmarkFavorite(bookmarkId, mapOf("is_favorite" to isFavorite)) }
    }

    @Test
    fun `archiveBookmark updates local state and syncs when connected`() = runTest {
        // Given
        val bookmarkId = 1L
        val isArchived = true
        
        every { networkConnectivityManager.isConnected() } returns true
        coEvery { bookmarkDao.updateArchiveStatus(any(), any(), any()) } returns Unit
        coEvery { bookmarkDao.getBookmarkById(bookmarkId) } returns createTestBookmarkEntity(id = bookmarkId)
        coEvery { bookmarkDao.updateBookmark(any()) } returns Unit
        coEvery { apiService.updateBookmarkArchive(any(), any()) } returns Response.success(Unit)

        // When
        val result = repository.archiveBookmark(bookmarkId, isArchived)

        // Then
        assertTrue(result.isSuccess)
        
        coVerify { bookmarkDao.updateArchiveStatus(bookmarkId, isArchived, any()) }
        coVerify { apiService.updateBookmarkArchive(bookmarkId, mapOf("is_archived" to isArchived)) }
    }

    @Test
    fun `updateLastOpened updates local timestamp`() = runTest {
        // Given
        val bookmarkId = 1L
        
        coEvery { bookmarkDao.updateLastOpened(any(), any()) } returns Unit

        // When
        val result = repository.updateLastOpened(bookmarkId)

        // Then
        assertTrue(result.isSuccess)
        
        coVerify { bookmarkDao.updateLastOpened(bookmarkId, any()) }
    }

    @Test
    fun `searchBookmarks returns flow from dao`() = runTest {
        // Given
        val query = "test"
        val entities = listOf(createTestBookmarkEntity(title = "Test Bookmark"))
        
        coEvery { bookmarkDao.searchBookmarks(query) } returns flowOf(entities)

        // When
        val result = repository.searchBookmarks(query).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
        assertEquals("Test Bookmark", result[0][0].title)
        
        coVerify { bookmarkDao.searchBookmarks(query) }
    }

    @Test
    fun `refreshBookmarks fetches from API and updates local database when connected`() = runTest {
        // Given
        val collectionId = 1L
        val remoteBookmarks = listOf(createTestBookmarkDto(id = 1, collectionId = collectionId))
        
        every { networkConnectivityManager.isConnected() } returns true
        coEvery { apiService.getBookmarksByCollection(collectionId) } returns remoteBookmarks
        coEvery { bookmarkDao.observeBookmarksByCollection(collectionId) } returns flowOf(emptyList())
        coEvery { bookmarkDao.runInTransaction(any()) } coAnswers {
            firstArg<suspend () -> Unit>().invoke()
        }
        coEvery { bookmarkDao.insertBookmarks(any()) } returns Unit

        // When
        repository.refreshBookmarks(collectionId)

        // Then
        coVerify { apiService.getBookmarksByCollection(collectionId) }
        coVerify { bookmarkDao.insertBookmarks(any()) }
    }

    @Test
    fun `refreshBookmarks does nothing when offline`() = runTest {
        // Given
        val collectionId = 1L
        
        every { networkConnectivityManager.isConnected() } returns false

        // When
        repository.refreshBookmarks(collectionId)

        // Then
        coVerify(exactly = 0) { apiService.getBookmarksByCollection(any()) }
    }

    @Test
    fun `syncBookmarks handles offline scenario gracefully`() = runTest {
        // Given
        val collectionId = 1L
        
        every { networkConnectivityManager.isConnected() } returns false

        // When
        repository.syncBookmarks(collectionId)

        // Then
        // Should not make any API calls when offline
        coVerify(exactly = 0) { apiService.createBookmark(any()) }
        coVerify(exactly = 0) { apiService.updateBookmark(any(), any()) }
        coVerify(exactly = 0) { apiService.deleteBookmark(any()) }
    }

    private fun createTestBookmark(
        id: Long = 1L,
        collectionId: Long = 1L,
        title: String = "Test Bookmark",
        url: String = "https://example.com"
    ): Bookmark {
        return Bookmark(
            id = id,
            collectionId = collectionId,
            title = title,
            url = url,
            description = "Test description",
            tags = listOf("test"),
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    private fun createTestBookmarkEntity(
        id: Long = 1L,
        collectionId: Long = 1L,
        title: String = "Test Bookmark",
        url: String = "https://example.com"
    ): BookmarkEntity {
        return BookmarkEntity(
            id = id,
            collectionId = collectionId,
            title = title,
            url = url,
            description = "Test description",
            tags = listOf("test"),
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    private fun createTestBookmarkDto(
        id: Long = 1L,
        collectionId: Long = 1L,
        title: String = "Test Bookmark",
        url: String = "https://example.com"
    ): BookmarkDto {
        return BookmarkDto(
            id = id,
            collectionId = collectionId,
            title = title,
            url = url,
            description = "Test description",
            tags = listOf("test"),
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}