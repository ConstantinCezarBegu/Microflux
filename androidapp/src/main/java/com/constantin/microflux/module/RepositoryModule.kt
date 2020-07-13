package com.constantin.microflux.module

import com.constantin.microflux.database.ConstafluxDatabase
import com.constantin.microflux.network.MinifluxService
import com.constantin.microflux.repository.ConstafluxRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(
        context: CoroutineContext,
        constafluxDatabase: ConstafluxDatabase,
        minifluxService: MinifluxService
    ) = ConstafluxRepository(
        context = context,
        constafluxDatabase = constafluxDatabase,
        minifluxService = minifluxService
    )
}