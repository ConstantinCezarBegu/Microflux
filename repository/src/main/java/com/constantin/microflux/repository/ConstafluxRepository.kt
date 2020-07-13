package com.constantin.microflux.repository

import com.constantin.microflux.database.ConstafluxDatabase
import com.constantin.microflux.network.MinifluxService
import kotlin.coroutines.CoroutineContext

class ConstafluxRepository(
    context: CoroutineContext,
    constafluxDatabase: ConstafluxDatabase,
    minifluxService: MinifluxService
) {

    private val workRepository =
        WorkRepository(
            context = context,
            constafluxDatabase = constafluxDatabase,
            minifluxService = minifluxService
        )

    val accountRepository =
        AccountRepository(
            context = context,
            minifluxService = minifluxService,
            constafluxDatabase = constafluxDatabase
        )

    val meRepository =
        MeRepository(
            context = context,
            minifluxService = minifluxService,
            constafluxDatabase = constafluxDatabase,
            getCurrentAccount = accountRepository::currentAccount
        )

    val settingsRepository =
        SettingsRepository(
            context = context,
            constafluxDatabase = constafluxDatabase,
            getCurrentAccount = accountRepository::currentAccount
        )

    val categoryRepository =
        CategoryRepository(
            context = context,
            minifluxService = minifluxService,
            constafluxDatabase = constafluxDatabase,
            getCurrentAccount = accountRepository::currentAccount
        )

    val feedRepository =
        FeedRepository(
            context = context,
            minifluxService = minifluxService,
            constafluxDatabase = constafluxDatabase,
            getCurrentAccount = accountRepository::currentAccount,
            syncCategory = categoryRepository::fetch
        )

    val entryRepository =
        EntryRepository(
            context = context,
            minifluxService = minifluxService,
            constafluxDatabase = constafluxDatabase,
            getCurrentAccount = accountRepository::currentAccount,
            syncEntry = workRepository::syncEntry,
            syncFeed = feedRepository::fetch

        )

    val backGroundProcessRepository =
        NotificationRepository(
            constafluxDatabase = constafluxDatabase,
            accountRepository = accountRepository,
            categoryRepository = categoryRepository,
            feedRepository = feedRepository,
            entryRepository = entryRepository
        )
}