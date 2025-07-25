package com.secondbrain.app.data.model

import java.util.*

/**
 * Represents the sharing settings for a collection.
 *
 * @property isShared Whether the collection is shared or not
 * @property accessLevel The level of access granted to others (e.g., "view", "edit")
 * @property shareUrl The URL that can be used to access the shared collection
 * @property expiryDate Optional date when the share will expire
 * @property password Optional password required to access the shared collection
 */
data class CollectionShareSettings(
    val isShared: Boolean = false,
    val accessLevel: String = "view",
    val shareUrl: String? = null,
    val expiryDate: Date? = null,
    val password: String? = null
)
