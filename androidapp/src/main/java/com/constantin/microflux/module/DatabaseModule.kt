package com.constantin.microflux.module

import com.constantin.microflux.database.ConstafluxDatabase
import com.constantin.microflux.encryption.AesEncryption
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        sqlDriver: SqlDriver,
        aesEncryption: AesEncryption
    ) = ConstafluxDatabase(
        sqlDriver = sqlDriver,
        aesEncryption = aesEncryption
    )
}