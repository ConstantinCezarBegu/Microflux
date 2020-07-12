package com.example.constaflux2.repository

import com.example.constaflux2.data.SettingsAllowImagePreview
import com.example.constaflux2.data.SettingsTheme
import com.example.constaflux2.database.Account
import com.example.constaflux2.database.ConstafluxDatabase
import com.example.constaflux2.database.util.flowMapToOne
import com.example.constaflux2.database.util.flowMapToOneOrNull
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SettingsRepository(
    private val context: CoroutineContext,
    private val constafluxDatabase: ConstafluxDatabase,
    private val getCurrentAccount: () -> Account
) {

    fun getTheme(
        account: Account = getCurrentAccount()
    ) = constafluxDatabase.settingsQueries.theme(
        serverId = account.serverId,
        userId = account.userId
    ).flowMapToOneOrNull(context)

    fun getSettings(
        account: Account = getCurrentAccount()
    ) = constafluxDatabase.settingsQueries.select(
        serverId = account.serverId,
        userId = account.userId
    ).flowMapToOne(context)

    suspend fun changeSettingsTheme(
        account: Account = getCurrentAccount(),
        settingsTheme: SettingsTheme
    ) {
        withContext(context + NonCancellable) {
            constafluxDatabase.settingsQueries.updateSettingsTheme(
                serverId = account.serverId,
                userId = account.userId,
                settingsTheme = settingsTheme
            )
        }
    }

    suspend fun changeAllowImagePreview(
        account: Account = getCurrentAccount(),
        settingsAllowImagePreview: SettingsAllowImagePreview
    ) {
        withContext(context + NonCancellable) {
            constafluxDatabase.settingsQueries.updateSettingsAllowImagePreview(
                serverId = account.serverId,
                userId = account.userId,
                settingsAllowImagePreview = settingsAllowImagePreview
            )
        }
    }
}