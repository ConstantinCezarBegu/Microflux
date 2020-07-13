package com.constantin.microflux.module.util

import com.constantin.microflux.data.Result
import kotlinx.coroutines.flow.MutableStateFlow

suspend inline fun MutableStateFlow<Result<Unit>>.load(
    triggerLoading: Boolean = true,
    crossinline load: suspend () -> Result<Unit>
) {
    if (triggerLoading) {
        value = Result.inProgress()
        value = load()
        value = Result.complete()
    } else load()
}
