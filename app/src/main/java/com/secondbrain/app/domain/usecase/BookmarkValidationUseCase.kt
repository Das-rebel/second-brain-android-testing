package com.secondbrain.app.domain.usecase

import com.secondbrain.app.domain.model.Bookmark
import com.secondbrain.app.domain.model.ValidationError
import com.secondbrain.app.domain.model.ValidationResult
import java.net.URL
import javax.inject.Inject

/**
 * Use case for validating bookmark data according to business rules.
 */
class BookmarkValidationUseCase @Inject constructor() {
    
    companion object {
        private const val MAX_TITLE_LENGTH = 500
        private const val MAX_DESCRIPTION_LENGTH = 2000
        private const val MAX_TAG_LENGTH = 50
        private const val MAX_TAGS_COUNT = 20
        private const val MIN_SEARCH_QUERY_LENGTH = 2
        private const val MAX_URL_LENGTH = 2000
    }
    
    /**
     * Validates a bookmark for creation.
     */
    fun validateForCreation(
        collectionId: Long,
        title: String,
        url: String,
        description: String?,
        tags: List<String>
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        // Validate collection ID
        if (collectionId <= 0) {
            errors.add(ValidationError.InvalidCollectionId(collectionId))
        }
        
        // Validate title
        errors.addAll(validateTitle(title))
        
        // Validate URL
        errors.addAll(validateUrl(url))
        
        // Validate description
        description?.let { errors.addAll(validateDescription(it)) }
        
        // Validate tags
        errors.addAll(validateTags(tags))
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
    
    /**
     * Validates a bookmark for update.
     */
    fun validateForUpdate(bookmark: Bookmark): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        // Validate bookmark ID
        if (bookmark.id <= 0) {
            errors.add(ValidationError.InvalidBookmarkId(bookmark.id))
        }
        
        // Validate collection ID
        if (bookmark.collectionId <= 0) {
            errors.add(ValidationError.InvalidCollectionId(bookmark.collectionId))
        }
        
        // Validate title
        errors.addAll(validateTitle(bookmark.title))
        
        // Validate URL
        errors.addAll(validateUrl(bookmark.url))
        
        // Validate description
        bookmark.description?.let { errors.addAll(validateDescription(it)) }
        
        // Validate tags
        errors.addAll(validateTags(bookmark.tags))
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
    
    /**
     * Validates a bookmark ID.
     */
    fun validateBookmarkId(bookmarkId: Long): ValidationResult {
        return if (bookmarkId > 0) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(ValidationError.InvalidBookmarkId(bookmarkId))
        }
    }
    
    /**
     * Validates a collection ID.
     */
    fun validateCollectionId(collectionId: Long): ValidationResult {
        return if (collectionId > 0) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(ValidationError.InvalidCollectionId(collectionId))
        }
    }
    
    /**
     * Validates a list of bookmark IDs for bulk operations.
     */
    fun validateBookmarkIds(bookmarkIds: List<Long>): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        if (bookmarkIds.isEmpty()) {
            errors.add(ValidationError.EmptyBookmarkList)
        } else {
            bookmarkIds.forEach { id ->
                if (id <= 0) {
                    errors.add(ValidationError.InvalidBookmarkId(id))
                }
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
    
    /**
     * Validates a search query.
     */
    fun validateSearchQuery(query: String?): ValidationResult {
        return if (query.isNullOrBlank()) {
            ValidationResult.Valid // Empty queries are allowed (means no search)
        } else {
            val trimmed = query.trim()
            if (trimmed.length >= MIN_SEARCH_QUERY_LENGTH) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(ValidationError.SearchQueryTooShort(MIN_SEARCH_QUERY_LENGTH))
            }
        }
    }
    
    /**
     * Sanitizes and validates a title.
     */
    private fun validateTitle(title: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        val trimmed = title.trim()
        
        if (trimmed.isEmpty()) {
            errors.add(ValidationError.EmptyTitle)
        } else if (trimmed.length > MAX_TITLE_LENGTH) {
            errors.add(ValidationError.TitleTooLong(MAX_TITLE_LENGTH))
        }
        
        return errors
    }
    
    /**
     * Validates a URL.
     */
    private fun validateUrl(url: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        val trimmed = url.trim()
        
        if (trimmed.isEmpty()) {
            errors.add(ValidationError.InvalidUrl(url))
            return errors
        }
        
        if (trimmed.length > MAX_URL_LENGTH) {
            errors.add(ValidationError.InvalidUrl("URL too long"))
            return errors
        }
        
        try {
            val urlObj = URL(trimmed)
            val protocol = urlObj.protocol.lowercase()
            if (protocol != "http" && protocol != "https") {
                errors.add(ValidationError.InvalidUrl("Only HTTP and HTTPS URLs are supported"))
            }
        } catch (e: Exception) {
            errors.add(ValidationError.InvalidUrl(trimmed))
        }
        
        return errors
    }
    
    /**
     * Validates a description.
     */
    private fun validateDescription(description: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        if (description.length > MAX_DESCRIPTION_LENGTH) {
            errors.add(ValidationError.DescriptionTooLong(MAX_DESCRIPTION_LENGTH))
        }
        
        return errors
    }
    
    /**
     * Validates a list of tags.
     */
    private fun validateTags(tags: List<String>): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        if (tags.size > MAX_TAGS_COUNT) {
            errors.add(ValidationError.TooManyTags(MAX_TAGS_COUNT))
        }
        
        tags.forEach { tag ->
            val trimmedTag = tag.trim()
            if (trimmedTag.length > MAX_TAG_LENGTH) {
                errors.add(ValidationError.TagTooLong(trimmedTag, MAX_TAG_LENGTH))
            }
        }
        
        return errors
    }
    
    /**
     * Sanitizes bookmark data by trimming whitespace and removing empty values.
     */
    fun sanitizeBookmarkData(
        title: String,
        url: String,
        description: String?,
        tags: List<String>
    ): SanitizedBookmarkData {
        return SanitizedBookmarkData(
            title = title.trim(),
            url = url.trim(),
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            tags = tags.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        )
    }
}

/**
 * Data class representing sanitized bookmark data.
 */
data class SanitizedBookmarkData(
    val title: String,
    val url: String,
    val description: String?,
    val tags: List<String>
)