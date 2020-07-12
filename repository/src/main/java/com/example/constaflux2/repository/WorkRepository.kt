package com.example.constaflux2.repository

import com.example.constaflux2.data.*
import com.example.constaflux2.database.Account
import com.example.constaflux2.database.ConstafluxDatabase
import com.example.constaflux2.database.toId
import com.example.constaflux2.network.MinifluxService
import com.example.constaflux2.util.forEachAsync
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class WorkRepository(
    private val context: CoroutineContext,
    private val minifluxService: MinifluxService,
    private val constafluxDatabase: ConstafluxDatabase
) {

    private fun getWork(
        account: Account
    ) = constafluxDatabase.workQueries.selectAll(
        serverId = account.serverId,
        userId = account.userId
    ).executeAsList()

    private fun deleteWork(
        serverId: ServerId,
        entryId: EntryId
    ) = constafluxDatabase.workQueries.delete(
        serverId = serverId,
        entryId = entryId
    )

    suspend fun syncEntry(
        account: Account
    ): Result<Unit> = withContext(context) {
        var result: Result<Unit> = Result.success()

        getWork(account).forEachAsync { work ->
            val workResult = when (work.workType) {
                WorkType.STATUS_MARK_AS_UNREAD -> {
                    minifluxService.entry.updateStatus(
                        accountUrl = account.serverUrl.url,
                        accountUsername = account.userName.name,
                        accountPassword = account.userPassword.password,
                        entryIds = listOf(work.entryId).toId(),
                        status = EntryStatus.UN_READ.status
                    )
                }
                WorkType.STATUS_MARK_AS_READ -> {
                    minifluxService.entry.updateStatus(
                        accountUrl = account.serverUrl.url,
                        accountUsername = account.userName.name,
                        accountPassword = account.userPassword.password,
                        entryIds = listOf(work.entryId).toId(),
                        status = EntryStatus.READ.status
                    )
                }
                WorkType.STAR -> {
                    minifluxService.entry.updateStarred(
                        accountUrl = account.serverUrl.url,
                        accountUsername = account.userName.name,
                        accountPassword = account.userPassword.password,
                        entryIds = listOf(work.entryId).toId()
                    )
                }
                else -> Result.success()
            }

            if (workResult is Result.Success) {
                withContext(NonCancellable) {
                    deleteWork(
                        serverId = work.serverId,
                        entryId = work.entryId
                    )
                }
            } else {
                result = workResult
            }
        }
        result
    }
}
