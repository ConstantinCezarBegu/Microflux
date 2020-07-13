package com.constantin.microflux.module

import com.constantin.microflux.repository.ConstafluxRepository
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