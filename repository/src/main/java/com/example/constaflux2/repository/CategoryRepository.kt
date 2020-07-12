package com.example.constaflux2.repository

import com.example.constaflux2.data.CategoryId
import com.example.constaflux2.data.Result
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.database.*
import com.example.constaflux2.database.util.flowMapToList
import com.example.constaflux2.database.util.flowMapToOne
import com.example.constaflux2.network.MinifluxService
import com.example.constaflux2.network.data.CategoryRequest
import com.example.constaflux2.repository.transformation.toCategory
import com.example.constaflux2.repository.transformation.toCategoryList
import com.example.constaflux2.repository.transformation.toCategoryResponse
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CategoryRepository(
    private val context: CoroutineContext,
    private val minifluxService: MinifluxService,
    private val constafluxDatabase: ConstafluxDatabase,
    private val getCurrentAccount: () -> Account
) {

    fun getAllCategories(
        account: Account = getCurrentAccount()
    ) = constafluxDatabase.categoryQueries.selectAll(
        serverId = account.serverId,
        userId = account.userId
    ).flowMapToList(context)

    fun getCategory(
        account: Account = getCurrentAccount(),
        categoryId: CategoryId
    ) = constafluxDatabase.categoryQueries.select(
        serverId = account.serverId,
        categoryId = categoryId
    ).flowMapToOne(context)

    suspend fun fetch(
        account: Account = getCurrentAccount()
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.category.get(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password
        )
        if (result is Result.Success) {
            constafluxDatabase.categoryQueries.refreshAll(
                serverId = account.serverId,
                userId = account.userId,
                categoryList = result.data.toCategoryList(account.serverId)
            )
            Result.success()
        } else result.extractError()
    }

    suspend fun add(
        account: Account = getCurrentAccount(),
        categoryRequest: CategoryRequest
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.category.add(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            categoryRequest = categoryRequest
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                constafluxDatabase.categoryQueries.upsert(result.data.toCategory(account.serverId))
            }
            Result.success()
        } else result.extractError()
    }

    suspend fun update(
        account: Account = getCurrentAccount(),
        category: Category
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.category.update(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            categoryId = category.categoryId.id,
            categoryResponse = category.toCategoryResponse()
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                constafluxDatabase.categoryQueries.upsert(category)
            }
            Result.success()
        } else result.extractError()
    }

    suspend fun delete(
        account: Account = getCurrentAccount(),
        serverId: ServerId,
        categoryId: CategoryId
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.category.delete(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            categoryId = categoryId.id
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                constafluxDatabase.categoryQueries.delete(
                    serverId = serverId,
                    categoryId = categoryId
                )
            }
        }
        result
    }
}