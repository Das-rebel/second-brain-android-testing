package com.secondbrain.app.util

import android.content.Context
import com.secondbrain.app.R
import com.secondbrain.app.domain.model.ValidationResult
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles errors and provides user-friendly messages.
 */
@Singleton
class ErrorHandler @Inject constructor(
    private val context: Context
) {
    
    /**
     * Handles an exception and returns a user-friendly error message.
     */
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    401 -> context.getString(R.string.error_unauthorized)
                    403 -> context.getString(R.string.error_forbidden)
                    404 -> context.getString(R.string.error_not_found)
                    408 -> context.getString(R.string.error_timeout)
                    500 -> context.getString(R.string.error_server)
                    else -> context.getString(R.string.error_network, throwable.code())
                }
            }
            is SocketTimeoutException -> context.getString(R.string.error_timeout)
            is UnknownHostException -> context.getString(R.string.error_no_internet)
            is IOException -> context.getString(R.string.error_network_io)
            else -> context.getString(R.string.error_unknown, throwable.message)
        }
    }
    
    /**
     * Alias for handleError to match ViewModel usage.
     */
    fun getUserFriendlyErrorMessage(throwable: Throwable): String {
        return handleError(throwable)
    }
    
    /**
     * Handles ValidationResult and returns appropriate error message.
     */
    fun handleValidationResult(result: ValidationResult): String? {
        return when (result) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> result.errors.joinToString(", ") { it.message }
        }
    }
    
    /**
     * Logs an error to the appropriate logging service.
     */
    fun logError(message: String, throwable: Throwable? = null) {
        // In a real app, this would log to Crashlytics or another service
        if (throwable != null) {
            println("Error: $message - ${throwable.message}")
            throwable.printStackTrace()
        } else {
            println("Error: $message")
        }
    }
}
