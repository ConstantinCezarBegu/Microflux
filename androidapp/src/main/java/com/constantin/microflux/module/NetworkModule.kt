package com.constantin.microflux.module

import com.constantin.microflux.network.MinifluxService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun providesNetwork() = MinifluxService()
}