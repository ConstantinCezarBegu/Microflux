package com.constantin.microflux.module.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel constructor() : ViewModel() {
    val clientScope: CoroutineScope = viewModelScope
    override fun onCleared() {
        super.onCleared()
    }
}