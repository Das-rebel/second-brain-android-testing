package com.secondbrain.app.domain.model

/**
 * Represents the result of a validation operation.
 */
sealed class ValidationResult {
    /**
     * Validation succeeded.
     */
    object Valid : ValidationResult()
    
    /**
     * Validation failed with specific errors.
     */
    data class Invalid(val errors: List<ValidationError>) : ValidationResult() {
        constructor(vararg errors: ValidationError) : this(errors.toList())
        constructor(error: String) : this(ValidationError.General(error))
    }
    
    /**
     * Returns true if validation is valid.
     */
    val isValid: Boolean
        get() = this is Valid
    
    /**
     * Returns true if validation is invalid.
     */
    val isInvalid: Boolean
        get() = this is Invalid
    
    /**
     * Returns the list of errors if invalid, empty list if valid.
     */
    val errors: List<ValidationError>
        get() = when (this) {
            is Valid -> emptyList()
            is Invalid -> this.errors
        }
    
    /**
     * Returns the first error message if invalid, null if valid.
     */
    val firstErrorMessage: String?
        get() = errors.firstOrNull()?.message
    
    /**
     * Returns all error messages concatenated.
     */
    val allErrorMessages: String
        get() = errors.joinToString(", ") { it.message }
}

/**
 * Represents different types of validation errors.
 */
sealed class ValidationError(val message: String) {
    /**
     * General validation error.
     */
    data class General(val errorMessage: String) : ValidationError(errorMessage)
    
    /**
     * Invalid URL format.
     */
    data class InvalidUrl(val url: String) : ValidationError("Invalid URL format: $url")
    
    /**
     * Empty or blank title.
     */
    object EmptyTitle : ValidationError("Title cannot be empty")
    
    /**
     * Invalid collection ID.
     */
    data class InvalidCollectionId(val id: Long) : ValidationError("Invalid collection ID: $id")
    
    /**
     * Invalid bookmark ID.
     */
    data class InvalidBookmarkId(val id: Long) : ValidationError("Invalid bookmark ID: $id")
    
    /**
     * Title too long.
     */
    data class TitleTooLong(val maxLength: Int) : ValidationError("Title cannot exceed $maxLength characters")
    
    /**
     * Description too long.
     */
    data class DescriptionTooLong(val maxLength: Int) : ValidationError("Description cannot exceed $maxLength characters")
    
    /**
     * Too many tags.
     */
    data class TooManyTags(val maxTags: Int) : ValidationError("Cannot have more than $maxTags tags")
    
    /**
     * Tag too long.
     */
    data class TagTooLong(val tag: String, val maxLength: Int) : ValidationError("Tag '$tag' cannot exceed $maxLength characters")
    
    /**
     * Search query too short.
     */
    data class SearchQueryTooShort(val minLength: Int) : ValidationError("Search query must be at least $minLength characters")
    
    /**
     * Empty bookmark list for bulk operations.
     */
    object EmptyBookmarkList : ValidationError("No bookmarks selected for operation")
}