package com.secondbrain.app.data.local.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room to handle Date objects.
 */nclass DateConverter {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
