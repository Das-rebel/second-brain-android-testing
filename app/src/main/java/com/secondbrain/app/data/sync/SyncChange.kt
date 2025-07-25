package com.secondbrain.app.data.sync

import com.secondbrain.app.data.model.ChangeType

/**
 * Represents a single change that needs to be synchronized with the server.
 *
 * @property userId The ID of the user who made the change
 * @property entityType The type of entity being changed (e.g., "collection", "bookmark")
 * @property entityId The ID of the entity being changed
 * @property changeType The type of change (CREATE, UPDATE, DELETE)
 * @property changeData The data associated with the change
 * @property timestamp When the change was made (millis since epoch)
 * @property retryCount How many times this change has been retried
 */
data class SyncChange(
    val userId: String,
    val entityType: String,
    val entityId: String,
    val changeType: ChangeType,
    val changeData: Any,
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
) {
    /**
     * Creates a new instance of this change with an incremented retry count.
     */
    fun withIncrementedRetry(): SyncChange {
        return copy(retryCount = retryCount + 1)
    }
}
