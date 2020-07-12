package com.example.constaflux2.repository

import com.example.constaflux2.data.*
import com.example.constaflux2.database.*
import com.example.constaflux2.database.util.flowMapToList
import com.example.constaflux2.database.util.flowMapToOne
import com.example.constaflux2.network.MinifluxService
import com.example.constaflux2.repository.transformation.toMe
import com.example.constaflux2.util.forEachAsync
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AccountRepository(
    private val context: CoroutineContext,
    private val minifluxService: MinifluxService,
    private val constafluxDatabase: ConstafluxDatabase
) {

    companion object {
        val NO_ACCOUNT: Account = Account(
            serverUrl = ServerUrl(""),
            serverId = ServerId.NO_SERVER,
            userId = UserId.NO_USER,
            userName = UserName(""),
            userPassword = UserPassword(""),
            userSelected = UserSelected.UNSELECTED,
            userFirstTimeRun = UserFirstTimeRun.TRUE
        )
    }

    var isAccountAvailable = false
        private set

    var currentAccount: Account = NO_ACCOUNT
        private set(value) {
            if (value != NO_ACCOUNT) {
                field = value
                isAccountAvailable = true
            } else {
                isAccountAvailable = false
            }
        }

    init {
        currentAccount =
            constafluxDatabase.userQueries.selectCurent().executeAsOneOrNull() ?: NO_ACCOUNT
    }

    fun getCurrentAccount() =
        constafluxDatabase.userQueries.selectCurent()
            .flowMapToOne(context)

    fun getNonCurrentAccounts() =
        constafluxDatabase.userQueries.selectNonCurent()
            .flowMapToList(context)

    fun getAccount(
        serverId: ServerId,
        userId: UserId
    ) = constafluxDatabase.userQueries.select(
        serverId = serverId,
        userId = userId
    ).flowMapToOne(context)

    suspend fun upsertAccount(
        serverUrl: ServerUrl,
        userName: UserName,
        userPassword: UserPassword
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.me.get(
            accountUrl = serverUrl.url,
            accountUsername = userName.name,
            accountPassword = userPassword.password
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                val serverId = constafluxDatabase.serverQueries.insert(serverUrl)
                currentAccount = constafluxDatabase.upsertAccount(
                    serverId = serverId,
                    userName = UserName(result.data.username),
                    userPassword = userPassword,
                    me = result.data.toMe(serverId, UserId(result.data.id))
                )
            }
            Result.success()
        } else {
            result.extractError()
        }
    }

    suspend fun deleteCurrentAccount() = withContext(context + NonCancellable) {
        currentAccount = (constafluxDatabase.userQueries.deleteCurrentAndSwitch() ?: NO_ACCOUNT)
    }

    suspend fun checkValidAccounts(): Result<List<Account>> = withContext(context) {
        val validUsers = mutableListOf<Account>()
        val invalidUsers = mutableListOf<Account>()
        constafluxDatabase.userQueries.selectAll().executeAsList().forEachAsync { user ->
            val result = fetchAccount(user)
            if (result is Result.Success) validUsers.add(user)
            else invalidUsers.add(user)
        }
        Result.userValidation(validUsers, invalidUsers)
    }


    suspend fun changeAccount(
        serverId: ServerId,
        userId: UserId
    ) = withContext(context) {
        withContext(NonCancellable) {
            currentAccount = constafluxDatabase.userQueries.selectUser(
                serverId = serverId,
                userId = userId
            )
        }
    }

    private suspend fun fetchAccount(
        account: Account
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.me.get(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                constafluxDatabase.meQueries.upsert(
                    me = result.data.toMe(
                        serverId = account.serverId,
                        userId = account.userId
                    )
                )
            }
            Result.success()
        } else result.extractError()
    }
}