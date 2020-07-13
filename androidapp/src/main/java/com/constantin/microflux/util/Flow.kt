package com.constantin.microflux.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <T> Flow<Flow<T>?>.observeFilter(
    owner: LifecycleOwner,
    crossinline fistRunAction: () -> Unit = {},
    crossinline stateChangeAction: () -> Unit = {},
    crossinline action: (T) -> Unit
) {
    var job: Job? = null
    var stateChange: Boolean
    var isFirstRun = true
    onEach { childFlow ->
        stateChange = true
        job?.cancel()
        job = childFlow?.onEach {
            action(it)
            if (isFirstRun) {
                fistRunAction()
                isFirstRun = false
                stateChange = false
            } else if (stateChange) {
                stateChangeAction()
                stateChange = false
            }
        }?.launchIn(owner.lifecycleScope)
    }.launchIn(owner.lifecycleScope)
}

inline fun <T> Flow<LiveData<T>?>.observeFilterLiveData(
    owner: LifecycleOwner,
    crossinline fistRunAction: () -> Unit = {},
    crossinline stateChangeAction: () -> Unit = {},
    crossinline action: (T) -> Unit
) {
    var job: LiveData<T>? = null
    var stateChange: Boolean
    var isFirstRun = true
    onEach { childFlow ->
        stateChange = true
        job?.removeObservers(owner)
        job = childFlow.also { liveData ->
            liveData?.observe(owner) {
                action(it)
                if (isFirstRun) {
                    fistRunAction()
                    isFirstRun = false
                    stateChange = false
                } else if (stateChange) {
                    stateChangeAction()
                    stateChange = false
                }
            }
        }
    }.launchIn(owner.lifecycleScope)
}