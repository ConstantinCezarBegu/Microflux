package com.example.constaflux2.module

import com.example.constaflux2.repository.ConstafluxRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
object ViewModelModule {
    @Provides
    @Singleton
    fun provideViewmodel(
        context: CoroutineContext,
        constafluxRepository: ConstafluxRepository
    ) = ViewmodelFactory(
        context = context,
        constafluxRepository = constafluxRepository
    )
}