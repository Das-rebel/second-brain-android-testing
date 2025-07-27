package com.secondbrain.app.data.remote.mappers

import com.secondbrain.app.data.model.Bookmark
import com.secondbrain.app.data.model.Collection
import com.secondbrain.app.data.remote.dto.SupabaseBookmark
import com.secondbrain.app.data.remote.dto.SupabaseCollection
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Mappers for converting between Supabase DTOs and local domain models.
 */

fun SupabaseBookmark.toBookmark(): Bookmark {
    return Bookmark(
        id = id ?: 0L,
        url = url,
        title = title,
        description = description,
        domain = domain,
        faviconUrl = faviconUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        openCount = openCount,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        collectionId = collectionId,
        createdAt = parseTimestampToDate(createdAt),
        updatedAt = parseTimestampToDate(updatedAt)
    )
}

fun Bookmark.toSupabaseBookmark(): SupabaseBookmark {
    return SupabaseBookmark(
        id = if (id == 0L) null else id,
        url = url,
        title = title,
        description = description,
        domain = domain,
        faviconUrl = faviconUrl,
        isFavorite = isFavorite,
        isArchived = isArchived,
        openCount = openCount,
        tags = if (tags.isNotEmpty()) tags.joinToString(",") else null,
        collectionId = collectionId,
        createdAt = formatDateToTimestamp(createdAt),
        updatedAt = formatDateToTimestamp(updatedAt)
    )
}

fun SupabaseCollection.toCollection(): Collection {
    return Collection(
        id = id ?: 0L,
        name = name,
        description = description,
        color = color,
        isShared = isShared,
        isDefault = isDefault,
        bookmarkCount = bookmarkCount,
        createdAt = parseTimestampToDate(createdAt),
        updatedAt = parseTimestampToDate(updatedAt)
    )
}

fun Collection.toSupabaseCollection(): SupabaseCollection {
    return SupabaseCollection(
        id = if (id == 0L) null else id,
        name = name,
        description = description,
        color = color,
        isShared = isShared,
        isDefault = isDefault,
        bookmarkCount = bookmarkCount,
        createdAt = formatDateToTimestamp(createdAt),
        updatedAt = formatDateToTimestamp(updatedAt)
    )
}

private fun parseTimestampToDate(timestamp: String?): Date {
    return try {
        timestamp?.let {
            Date(Instant.parse(it).toEpochMilli())
        } ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

private fun formatDateToTimestamp(date: Date): String {
    return try {
        Instant.ofEpochMilli(date.time)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_INSTANT)
    } catch (e: Exception) {
        Instant.now().toString()
    }
}