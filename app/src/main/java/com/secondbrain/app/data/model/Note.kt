package com.secondbrain.app.data.model

import java.util.*

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val isSelected: Boolean = false
)
