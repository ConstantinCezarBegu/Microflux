package com.constantin.microflux.module

import android.content.Context
import com.constantin.microflux.database.ConstafluxDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        context: Context
    ) = ConstafluxDatabase(
        context = context
    )
}