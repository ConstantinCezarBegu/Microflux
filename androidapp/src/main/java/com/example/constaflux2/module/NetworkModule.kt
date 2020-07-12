package com.example.constaflux2.module

import com.example.constaflux2.network.MinifluxService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun providesNetwork() = MinifluxService()
}