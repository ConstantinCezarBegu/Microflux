package com.example.constaflux2.module

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.constaflux2.data.*
import com.example.constaflux2.database.Account
import com.example.constaflux2.database.EntryListPreview
import com.example.constaflux2.module.util.BaseViewModel
import com.example.constaflux2.module.util.load
import com.example.constaflux2.repository.ConstafluxRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class EntryViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : BaseViewModel() {

    val isLogin: Boolean get() = repository.accountRepository.isAccountAvailable
    val currentAccount: Account get() = repository.accountRepository.currentAccount

    val currentTheme = viewModelScope.async(context) {
        repository.settingsRepository.getTheme().first()
    }

    protected val protectedEntriesId = MutableStateFlow<Flow<List<EntryId>>?>(null)
    val entriesId: StateFlow<Flow<List<EntryId>>?> = protectedEntriesId

    abstract fun getEntriesId(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    )

    protected val protectedEntries = MutableStateFlow<LiveData<PagedList<EntryListPreview>>?>(null)
    val entries: StateFlow<LiveData<PagedList<EntryListPreview>>?> = protectedEntries
    protected val pageSize = 20
    protected fun provideBoundaryCallback(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) = object : PagedList.BoundaryCallback<EntryListPreview>() {
        override fun onZeroItemsLoaded() {
            fetchEntry(
                entryStatus = entryStatus,
                entryStarred = entryStarred,
                showAnimations = false
            )
        }

        override fun onItemAtEndLoaded(itemAtEnd: EntryListPreview) {
            fetchEntry(
                entryStatus = entryStatus,
                entryStarred = entryStarred,
                entryAfter = itemAtEnd.entryPublishedAtUnix,
                clearPrevious = false,
                showAnimations = false
            )
        }
    }

    abstract fun getEntries(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    )

    protected val protectedFetchEntryProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val fetchEntryProgression: StateFlow<Result<Unit>> = protectedFetchEntryProgression

    abstract fun fetchEntry(
        entryStatus: EntryStatus = EntryStatus.ALL,
        entryStarred: EntryStarred = EntryStarred.UN_STARRED,
        entryAfter: EntryPublishedAtUnix = EntryPublishedAtUnix.EMPTY,
        clearPrevious: Boolean = true,
        showAnimations: Boolean = true
    ): Job

    private val _updateEntryStatusProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val updateEntryStatusProgression = _updateEntryStatusProgression

    fun updateEntryStatus(
        entryIds: List<EntryId>,
        entryStatus: EntryStatus
    ) = viewModelScope.launch {
        _updateEntryStatusProgression.load {
            repository.entryRepository.updateStatus(
                entryIds = entryIds,
                entryStatus = entryStatus.not()
            )
        }
    }

    private val _updateEntryStarredProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val updateEntryStarredProgression: StateFlow<Result<Unit>> = _updateEntryStarredProgression

    fun updateEntryStarred(
        entryIds: List<EntryId>
    ) = viewModelScope.launch {
        _updateEntryStarredProgression.load {
            repository.entryRepository.updateStarred(
                entryIds = entryIds
            )
        }
    }
}

class AllEntryViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : EntryViewModel(context, repository) {

    override fun getEntriesId(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) {
        protectedEntriesId.value = repository.entryRepository.getAllEntriesId(
            entryStatus = entryStatus,
            entryStarred = entryStarred
        )
    }

    override fun getEntries(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) {
        protectedEntries.value = repository.entryRepository.getAllEntries(
            entryStatus = entryStatus,
            entryStarred = entryStarred
        ).toLiveData(
            pageSize = pageSize,
            boundaryCallback = provideBoundaryCallback(
                entryStatus = entryStatus,
                entryStarred = entryStarred
            )
        )
    }

    override fun fetchEntry(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred,
        entryAfter: EntryPublishedAtUnix,
        clearPrevious: Boolean,
        showAnimations: Boolean
    ) = viewModelScope.launch {
        protectedFetchEntryProgression.load(showAnimations) {
            repository.entryRepository.fetch(
                entryStarred = entryStarred,
                entryAfter = entryAfter,
                entryStatus = entryStatus,
                clearPrevious = clearPrevious
            )
        }
    }
}

class FeedEntryViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository,
    private val feedId: FeedId
) : EntryViewModel(context, repository) {

    override fun getEntriesId(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) {
        protectedEntriesId.value =
            repository.entryRepository.getAllEntriesId(
                entryStatus = entryStatus,
                entryStarred = entryStarred,
                feedId = feedId
            )
    }

    override fun getEntries(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) {
        protectedEntries.value =
            repository.entryRepository.getAllEntries(
                entryStatus = entryStatus,
                entryStarred = entryStarred,
                feedId = feedId
            ).toLiveData(
                pageSize = pageSize,
                boundaryCallback = provideBoundaryCallback(
                    entryStatus = entryStatus,
                    entryStarred = entryStarred
                )
            )
    }

    override fun fetchEntry(
        entryStatus: EntryStatus,
        entryStarred: EntryStarred,
        entryAfter: EntryPublishedAtUnix,
        clearPrevious: Boolean,
        showAnimations: Boolean
    ) = viewModelScope.launch {
        protectedFetchEntryProgression.load(showAnimations) {
            repository.entryRepository.fetchFeed(
                feedId = feedId,
                entryStarred = entryStarred,
                entryAfter = entryAfter,
                entryStatus = entryStatus,
                clearPrevious = clearPrevious
            )
        }
    }
}