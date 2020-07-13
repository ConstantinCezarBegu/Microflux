package com.constantin.microflux.util

import android.content.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope,
    crossinline action: suspend () -> Unit
) {
    val pendingResult = goAsync()
    coroutineScope.launch {
        try {
            action()
        } finally {
            pendingResult.finish()
        }
    }
}
