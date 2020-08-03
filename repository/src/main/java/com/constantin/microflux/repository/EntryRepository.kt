package com.constantin.microflux.repository

import com.constantin.microflux.data.*
import com.constantin.microflux.database.*
import com.constantin.microflux.database.util.flowMapToList
import com.constantin.microflux.database.util.flowMapToOne
import com.constantin.microflux.network.MinifluxService
import com.constantin.microflux.network.data.EntryListResponse
import com.constantin.microflux.repository.transformation.toEntryList
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class EntryRepository(
    private val context: CoroutineContext,
    private val minifluxService: MinifluxService,
    private val constafluxDatabase: ConstafluxDatabase,
    private val getCurrentAccount: () -> Account,
    private val syncEntry: suspend (Account) -> Result<Unit>,
    private val syncFeed: suspend (Account) -> Unit
) {

    fun getAllEntriesId(
        account: Account = getCurrentAccount(),
        feedId: FeedId = FeedId.NO_FEED,
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) = constafluxDatabase.entryQueries.selectAllId(
        entryStatus = entryStatus,
        entryStarred = entryStarred,
        serverId = account.serverId,
        userId = account.userId,
        feedId = feedId
    ).flowMapToList(context)

    fun getAllEntries(
        account: Account = getCurrentAccount(),
        feedId: FeedId = FeedId.NO_FEED,
        entryStatus: EntryStatus,
        entryStarred: EntryStarred
    ) = constafluxDatabase.entryQueries.selectAll(
        entryStatus = entryStatus,
        entryStarred = entryStarred,
        serverId = account.serverId,
        userId = account.userId,
        feedId = feedId
    ).flowMapToList(context)

    fun getEntry(
        account: Account = getCurrentAccount(),
        entryId: EntryId
    ) = constafluxDatabase.entryQueries.select(
        serverId = account.serverId,
        entryId = entryId
    ).flowMapToOne(context)

    suspend fun fetch(
        account: Account = getCurrentAccount(),
        entryStarred: EntryStarred,
        entryStatus: EntryStatus,
        entryAfter: EntryPublishedAtUnix = EntryPublishedAtUnix.EMPTY,
        clearPrevious: Boolean,
        clearNotification: Boolean = true
    ): Result<Unit> = withContext(context) {
        val syncEntryResult = syncEntry(account)
        if (syncEntryResult is Result.Success) {
            val resultUnreadAsync = async {
                if (entryStatus == EntryStatus.UN_READ || entryStatus == EntryStatus.ALL) minifluxService.entry.get(
                    accountUrl = account.serverUrl.url,
                    accountUsername = account.userName.name,
                    accountPassword = account.userPassword.password,
                    entryStatus = EntryStatus.UN_READ.status,
                    entryStarred = entryStarred.starred,
                    entryAfter = entryAfter.publishedAt.toString()
                )
                else Result.success(EntryListResponse.EMPTY)
            }
            val resultReadAsync = async {
                if (entryStatus == EntryStatus.READ || entryStatus == EntryStatus.ALL) minifluxService.entry.get(
                    accountUrl = account.serverUrl.url,
                    accountUsername = account.userName.name,
                    accountPassword = account.userPassword.password,
                    entryStatus = EntryStatus.READ.status,
                    entryStarred = entryStarred.starred,
                    entryAfter = entryAfter.publishedAt.toString()
                )
                else Result.success(EntryListResponse.EMPTY)
            }
            val resultUnread = resultUnreadAsync.await()
            val resultRead = resultReadAsync.await()
            if (resultUnread is Result.Success && resultRead is Result.Success) {
                val saveDatabase = suspend {
                    constafluxDatabase.entryRefreshAll(
                        serverId = account.serverId,
                        userId = account.userId,
                        entryStarred = entryStarred,
                        entryStatus = entryStatus,
                        entryList = (resultUnread.data.entryList + resultRead.data.entryList)
                            .toEntryList(account.serverId),
                        clearPrevious = clearPrevious,
                        clearNotification = clearNotification
                    )
                }
                if (saveDatabase() !is Result.Success) {
                    syncFeed(account)
                    saveDatabase()
                } else Result.success()
            } else if (resultUnread !is Result.Success) resultUnread.extractError() else resultRead.extractError()
        } else syncEntryResult
    }


    suspend fun fetchFeed(
        account: Account = getCurrentAccount(),
        feedId: FeedId,
        entryStarred: EntryStarred,
        entryStatus: EntryStatus,
        entryAfter: EntryPublishedAtUnix = EntryPublishedAtUnix.EMPTY,
        clearPrevious: Boolean,
        clearNotification: Boolean = true
    ): Result<Unit> = withContext(context) {
        val syncEntryResult = syncEntry(account)
        if (syncEntryResult is Result.Success) {
            val resultUnreadAsync = async {
                if (entryStatus == EntryStatus.UN_READ || entryStatus == EntryStatus.ALL) minifluxService.entry.getFeed(
                    accountUrl = account.serverUrl.url,
                    accountUsername = account.userName.name,
                    accountPassword = account.userPassword.password,
                    feedId = feedId.id,
                    entryStatus = EntryStatus.UN_READ.status,
                    entryStarred = entryStarred.starred,
                    entryAfter = entryAfter.publishedAt.toString()
                )
                else Result.success(EntryListResponse.EMPTY)
            }
            val resultReadAsync = async {
                if (entryStatus == EntryStatus.READ || entryStatus == EntryStatus.ALL) minifluxService.entry.getFeed(
                    accountUrl = account.serverUrl.url,
                    accountUsername = account.userName.name,
                    accountPassword = account.userPassword.password,
                    feedId = feedId.id,
                    entryStatus = EntryStatus.READ.status,
                    entryStarred = entryStarred.starred,
                    entryAfter = entryAfter.publishedAt.toString()
                ) else Result.success(EntryListResponse.EMPTY)
            }
            val resultUnread = resultUnreadAsync.await()
            val resultRead = resultReadAsync.await()
            if (resultUnread is Result.Success && resultRead is Result.Success) {
                val saveDatabase = suspend {
                    constafluxDatabase.entryRefreshAll(
                        serverId = account.serverId,
                        userId = account.userId,
                        feedId = feedId,
                        entryStarred = entryStarred,
                        entryStatus = entryStatus,
                        entryList = (resultUnread.data.entryList + resultRead.data.entryList)
                            .toEntryList(account.serverId),
                        clearPrevious = clearPrevious,
                        clearNotification = clearNotification
                    )
                }
                if (saveDatabase() !is Result.Success) {
                    syncFeed(account)
                    saveDatabase()
                } else Result.success()
            } else if (resultUnread !is Result.Success) resultUnread.extractError() else resultRead.extractError()
        } else syncEntryResult
    }

    suspend fun updateStatus(
        account: Account = getCurrentAccount(),
        entryIds: List<EntryId>,
        entryStatus: EntryStatus
    ): Result<Unit> = withContext(context) {
        withContext(NonCancellable) {
            constafluxDatabase.entryQueries.updateStatus(
                entryId = entryIds,
                entryStatus = entryStatus
            )
        }
        val result = minifluxService.entry.updateStatus(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            entryIds = entryIds.toId(),
            status = entryStatus.status
        )
        if (result is Result.Error.NetworkError.IOError) {
            withContext(NonCancellable) {
                constafluxDatabase.workQueries.insertAll(
                    serverId = account.serverId,
                    userId = account.userId,
                    entryIds = entryIds,
                    workType = entryStatus.toWorkType()
                )
            }
        } else if (result is Result.Error) {
            withContext(NonCancellable) {
                constafluxDatabase.entryQueries.updateStatus(
                    entryId = entryIds,
                    entryStatus = entryStatus.not()
                )
            }
        }
        result
    }

    suspend fun updateStarred(
        account: Account = getCurrentAccount(),
        entryIds: List<EntryId>
    ): Result<Unit> = withContext(context) {
        withContext(NonCancellable) {
            constafluxDatabase.entryQueries.updateStar(
                entryId = entryIds
            )
        }
        val result = minifluxService.entry.updateStarred(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            entryIds = entryIds.toId()
        )
        if (result is Result.Error.NetworkError.IOError) {
            withContext(NonCancellable) {
                constafluxDatabase.workQueries.insertAll(
                    serverId = account.serverId,
                    userId = account.userId,
                    entryIds = entryIds,
                    workType = WorkType.STAR
                )
            }
        } else if (result is Result.Error) {
            withContext(NonCancellable) {
                constafluxDatabase.entryQueries.updateStar(
                    entryId = entryIds
                )
            }
        }
        result
    }
}