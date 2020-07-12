package com.example.constaflux2.module

import com.example.constaflux2.repository.ConstafluxRepository
import com.example.constaflux2.module.util.BaseViewModel
import kotlin.coroutines.CoroutineContext

class ViewmodelFactory(
    private val context: CoroutineContext,
    private val constafluxRepository: ConstafluxRepository
) {
    fun create(state: State): BaseViewModel {
        return when (state) {
            is State.CreateAccount -> CreateAccountViewModel(
                context = context,
                repository = constafluxRepository
            )
            is State.UpdateAccount -> UpdateAccountViewModel(
                context = context,
                repository = constafluxRepository,
                serverId = state.serverId,
                userId = state.userId
            )
            is State.AccountDialog -> AccountDialogViewmodel(
                context = context,
                repository = constafluxRepository
            )
            is State.Entries -> AllEntryViewModel(
                context = context,
                repository = constafluxRepository
            )
            is State.EntryDescription -> EntryDescriptionViewModel(
                context = context,
                repository = constafluxRepository,
                entryId = state.entryId
            )
            is State.FeedEntries -> FeedEntryViewModel(
                context = context,
                repository = constafluxRepository,
                feedId = state.feedId
            )
            is State.Feed -> AllFeedViewModel(
                context = context,
                repository = constafluxRepository
            )
            is State.FeedDialog -> FeedDialogViewModel(
                context = context,
                repository = constafluxRepository,
                feedId = state.feedId
            )
            is State.CategoryFeeds -> CategoryFeedViewModel(
                context = context,
                repository = constafluxRepository,
                categoryId = state.categoryId
            )
            is State.Category -> CategoryViewModel(
                context = context,
                repository = constafluxRepository
            )
            is State.CategoryDialog -> CategoryDialogViewModel(
                context = context,
                repository = constafluxRepository,
                categoryId = state.categoryId
            )
            is State.Navigation -> NavigationViewModel(
                context = context,
                repository = constafluxRepository
            )
            is State.Settings -> SettingsViewModel(
                context = context,
                repository = constafluxRepository
            )
        }
    }
}