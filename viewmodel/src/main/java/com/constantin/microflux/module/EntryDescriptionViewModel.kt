package com.constantin.microflux.module

import androidx.lifecycle.viewModelScope
import com.constantin.microflux.data.EntryId
import com.constantin.microflux.data.EntryStatus
import com.constantin.microflux.data.Result
import com.constantin.microflux.module.util.BaseViewModel
import com.constantin.microflux.module.util.load
import com.constantin.microflux.repository.ConstafluxRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EntryDescriptionViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository,
    private val entryId: EntryId
) : BaseViewModel() {

    val currentAccount get() = repository.accountRepository.currentAccount

    val entry = viewModelScope.async(context) {
        repository.entryRepository.getEntry(entryId = entryId).first()
    }

    private val _updateEntryStatusProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val updateEntryStatusProgression: StateFlow<Result<Unit>> = _updateEntryStatusProgression

    fun updateEntryStatus(entryStatus: EntryStatus) = viewModelScope.launch {
        _updateEntryStatusProgression.load {
            repository.entryRepository.updateStatus(
                entryIds = listOf(entryId),
                entryStatus = entryStatus
            )
        }
    }

    private val _updateEntryStarredProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val updateEntryStarredProgression: StateFlow<Result<Unit>> = _updateEntryStarredProgression

    fun updateEntryStarred() = viewModelScope.launch {
        _updateEntryStarredProgression.load {
            repository.entryRepository.updateStarred(
                entryIds = listOf(entryId)
            )
        }
    }

}