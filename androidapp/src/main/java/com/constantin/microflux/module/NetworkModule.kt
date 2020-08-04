package com.constantin.microflux.module

import com.constantin.microflux.network.MinifluxService
import dagger.Module
import dagger.Provides
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun providesNetwork() = MinifluxService(
        engine = Android
    )
}