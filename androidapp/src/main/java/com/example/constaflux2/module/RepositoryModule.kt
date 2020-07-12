package com.example.constaflux2.module

import com.example.constaflux2.database.ConstafluxDatabase
import com.example.constaflux2.network.MinifluxService
import com.example.constaflux2.repository.ConstafluxRepository
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