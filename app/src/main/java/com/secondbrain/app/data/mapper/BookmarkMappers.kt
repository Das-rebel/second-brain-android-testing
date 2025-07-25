package com.secondbrain.app.data.mapper

import com.secondbrain.app.data.local.entity.BookmarkEntity
import com.secondbrain.app.data.network.dto.BookmarkDto
import com.secondbrain.app.data.network.dto.BookmarkRequest

/**
 * Extension function to convert a [BookmarkEntity] to a [Bookmark] (domain model).
 */
fun BookmarkEntity.toBookmark(): com.secondbrain.app.domain.model.Bookmark {
    return com.secondbrain.app.domain.model.Bookmark(
        id = id,
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        faviconUrl = faviconUrl,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        serverIsFavorite = serverIsFavorite,
        serverIsArchived = serverIsArchived,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        openCount = openCount,
        isSynced = isSynced,
        isDeleted = isDeleted,
        isSelected = false
    )
}

/**
 * Extension function to convert a [Bookmark] domain model to a [BookmarkEntity].
 */
fun com.secondbrain.app.domain.model.Bookmark.toBookmarkEntity(): BookmarkEntity {
    return BookmarkEntity(
        id = id,
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        faviconUrl = faviconUrl,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        serverIsFavorite = serverIsFavorite,
        serverIsArchived = serverIsArchived,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        openCount = openCount,
        isSynced = isSynced,
        isDeleted = isDeleted,
        isLocalId = id == 0L || id < 0L
    )
}

/**
 * Extension function to convert a list of [BookmarkEntity] to a list of [Bookmark] domain models.
 */
fun List<BookmarkEntity>.toBookmarks(): List<com.secondbrain.app.domain.model.Bookmark> {
    return map { it.toBookmark() }
}

/**
 * Extension function to convert a list of [Bookmark] domain models to a list of [BookmarkEntity].
 */
fun List<com.secondbrain.app.domain.model.Bookmark>.toBookmarkEntities(): List<BookmarkEntity> {
    return map { it.toBookmarkEntity() }
}

/**
 * Extension function to convert a [BookmarkDto] to a [Bookmark] domain model.
 */
fun BookmarkDto.toBookmark(): com.secondbrain.app.domain.model.Bookmark {
    return com.secondbrain.app.domain.model.Bookmark(
        id = id,
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        faviconUrl = faviconUrl,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        serverIsFavorite = serverIsFavorite,
        serverIsArchived = serverIsArchived,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        openCount = openCount,
        isSynced = true, // DTOs always come from the server, so they're synced
        isDeleted = false,
        isSelected = false
    )
}

/**
 * Extension function to convert a [Bookmark] domain model to a [BookmarkRequest].
 */
fun com.secondbrain.app.domain.model.Bookmark.toBookmarkRequest(): BookmarkRequest {
    return BookmarkRequest(
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        tags = tags
    )
}

/**
 * Extension function to convert a [Bookmark] domain model to a [BookmarkDto].
 */
fun com.secondbrain.app.domain.model.Bookmark.toBookmarkDto(): BookmarkDto {
    return BookmarkDto(
        id = id,
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        faviconUrl = faviconUrl,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        serverIsFavorite = serverIsFavorite,
        serverIsArchived = serverIsArchived,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        openCount = openCount
    )
}

/**
 * Extension function to convert a [BookmarkEntity] to a [BookmarkDto].
 */
fun BookmarkEntity.toBookmarkDto(): BookmarkDto {
    return BookmarkDto(
        id = id,
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        faviconUrl = faviconUrl,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        serverIsFavorite = serverIsFavorite,
        serverIsArchived = serverIsArchived,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        openCount = openCount
    )
}

/**
 * Extension function to convert a list of [BookmarkDto] to a list of [Bookmark] domain models.
 */
fun List<BookmarkDto>.toBookmarks(): List<com.secondbrain.app.domain.model.Bookmark> {
    return map { it.toBookmark() }
}

/**
 * Extension function to convert a list of [Bookmark] domain models to a list of [BookmarkRequest].
 */
fun List<com.secondbrain.app.domain.model.Bookmark>.toBookmarkRequests(): List<BookmarkRequest> {
    return map { it.toBookmarkRequest() }
}

/**
 * Extension function to convert a list of [Bookmark] domain models to a list of [BookmarkDto].
 */
fun List<com.secondbrain.app.domain.model.Bookmark>.toBookmarkDtos(): List<BookmarkDto> {
    return map { it.toBookmarkDto() }
}

/**
 * Extension function to convert a [BookmarkDto] to a [BookmarkEntity] with additional parameters.
 */
fun BookmarkDto.toBookmarkEntity(collectionId: Long, isSynced: Boolean = true): BookmarkEntity {
    return BookmarkEntity(
        id = id,
        collectionId = collectionId,
        title = title,
        url = url,
        description = description,
        faviconUrl = faviconUrl,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        serverIsFavorite = serverIsFavorite,
        serverIsArchived = serverIsArchived,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        openCount = openCount,
        isSynced = isSynced,
        isDeleted = false,
        isLocalId = false
    )
}
