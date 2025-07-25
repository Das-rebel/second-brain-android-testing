package com.secondbrain.app.data.model

/**
 * Represents the type of change made to an entity.
 */
enum class ChangeType {
    /** A new entity was created. */
    CREATE,
    
    /** An existing entity was updated. */
    UPDATE,
    
    /** An existing entity was deleted. */
    DELETE
}
