package com.constantin.microflux.module

import com.constantin.microflux.data.*

sealed class State {
    object CreateAccount : State()
    data class UpdateAccount(val serverId: ServerId, val userId: UserId) : State()
    object AccountDialog : State()
    object Entries : State()
    data class EntryDescription(val entryId: EntryId) : State()
    object Feed : State()
    data class FeedDialog(val feedId: FeedId) : State()
    data class FeedEntries(val feedId: FeedId) : State()
    object Category : State()
    data class CategoryDialog(val categoryId: CategoryId) : State()
    data class CategoryFeeds(val categoryId: CategoryId) : State()
    object Navigation : State()
    object Settings : State()
}