package com.example.constaflux2.network

import com.example.constaflux2.data.Result
import com.example.constaflux2.network.data.*
import com.example.constaflux2.network.util.Credentials
import com.example.constaflux2.network.util.get
import com.example.constaflux2.network.util.put
import com.example.constaflux2.util.forEachAsync
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EntryNetwork(
    private val client: HttpClient
) {

    companion object {
        const val ENTRY_URL = "/v1/entries"
    }

    suspend fun get(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        entryStatus: EntryStatus,
        entryStarred: EntryStarred,
        entryAfter: EntryAfter
    ): Result<EntryListResponse> = client.get(
        urlString = "${accountUrl}${ENTRY_URL}?status=${entryStatus}${setStarred(entryStarred)}${setBefore(
            entryAfter
        )}&direction=desc",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )


    suspend fun getFeed(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        feedId: FeedId,
        entryStatus: EntryStatus,
        entryStarred: EntryStarred,
        entryAfter: EntryAfter
    ): Result<EntryListResponse> = client.get(
        urlString = "${accountUrl}${FeedNetwork.FEED_URL}/${feedId}/entries?status=${entryStatus}${setStarred(
            entryStarred
        )}${setBefore(
            entryAfter
        )}&direction=desc",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )

    suspend fun getSearch(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        entrySearch: EntrySearch,
        entryAfter: EntryAfter
    ): Result<EntryListResponse> = client.get(
        urlString = "${accountUrl}${ENTRY_URL}?search=${entrySearch}${setBefore(entryAfter)}&direction=desc",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )


    suspend fun updateStatus(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        entryIds: List<EntryId>,
        status: EntryStatus
    ): Result<Unit> = client.put(
        urlString = "${accountUrl}${ENTRY_URL}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    ) {
        contentType(ContentType.Application.Json)
        body = UpdateEntryStatusRequest(
            entryIds = entryIds,
            status = status
        )
    }

    suspend fun updateStarred(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        entryIds: List<EntryId>
    ): Result<Unit> {
        var totalResult: Result<Unit> = Result.success()
        entryIds.forEachAsync {
            val result = client.put<Unit>(
                urlString = "${accountUrl}${ENTRY_URL}/${it}/bookmark",
                auth = Credentials.basic(
                    userName = accountUsername,
                    password = accountPassword
                )
            )
            if (result is Result.Error) totalResult = result
        }
        return totalResult
    }

    private fun setStarred(
        starred: Boolean
    ) = if (starred) "&starred" else ""

    private fun setBefore(
        entryAfter: EntryAfter
    ) = if (entryAfter.isNotEmpty()) "&before=${entryAfter}" else ""
}