package com.constantin.microflux.repository

import com.constantin.microflux.data.Result
import com.constantin.microflux.database.Account
import com.constantin.microflux.database.ConstafluxDatabase
import com.constantin.microflux.database.upsert
import com.constantin.microflux.database.util.flowMapToOne
import com.constantin.microflux.network.MinifluxService
import com.constantin.microflux.repository.transformation.toMe
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MeRepository(
    private val context: CoroutineContext,
    private val minifluxService: MinifluxService,
    private val constafluxDatabase: ConstafluxDatabase,
    private val getCurrentAccount: () -> Account
) {

    fun getMe(
        account: Account = getCurrentAccount()
    ) = constafluxDatabase.meQueries.select(
        serverId = account.serverId,
        userId = account.userId
    ).flowMapToOne(context)

    suspend fun fetch(
        account: Account = getCurrentAccount()
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.me.get(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password
        )
        if (result is Result.Success) {
            constafluxDatabase.meQueries.upsert(
                me = result.data.toMe(
                    serverId = account.serverId,
                    userId = account.userId
                )
            )
            Result.success()
        } else result.extractError()
    }
}