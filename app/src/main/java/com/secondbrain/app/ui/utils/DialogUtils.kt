package com.secondbrain.app.ui.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.secondbrain.app.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Shows a confirmation dialog and returns the user's choice.
 * @return true if user confirmed, false otherwise
 */
suspend fun showDeleteConfirmationDialog(context: Context): Boolean {
    return suspendCancellableCoroutine { continuation ->
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                continuation.resume(true)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                continuation.resume(false)
            }
            .setOnCancelListener {
                continuation.resume(false)
            }
            .create()
            .apply { setCanceledOnTouchOutside(true) }
            .show()
    }
}

/**
 * Shows a confirmation dialog for bulk delete and returns the user's choice.
 * @param count Number of items to be deleted
 * @return true if user confirmed, false otherwise
 */
suspend fun showBulkDeleteConfirmationDialog(context: Context, count: Int): Boolean {
    return suspendCancellableCoroutine { continuation ->
        val message = context.resources.getQuantityString(
            R.plurals.confirm_bulk_delete_message,
            count,
            count
        )
        
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                continuation.resume(true)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                continuation.resume(false)
            }
            .setOnCancelListener {
                continuation.resume(false)
            }
            .create()
            .apply { setCanceledOnTouchOutside(true) }
            .show()
    }
}
