package com.example.constaflux2.module

import android.content.Context
import com.example.constaflux2.database.ConstafluxDatabase
import com.squareup.sqldelight.db.SqlDriver
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