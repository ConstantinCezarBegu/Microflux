package com.example.constaflux2.module

import androidx.lifecycle.viewModelScope
import com.example.constaflux2.data.SettingsAllowImagePreview
import com.example.constaflux2.data.SettingsTheme
import com.example.constaflux2.module.util.BaseViewModel
import com.example.constaflux2.repository.ConstafluxRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingsViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : BaseViewModel() {

    val me = viewModelScope.async(context) {
        repository.meRepository.getMe().first()
    }

    val user get() = repository.accountRepository.currentAccount

    val settings = viewModelScope.async(context) {
        repository.settingsRepository.getSettings().first()
    }

    fun updateSettingsTheme(
        settingsTheme: SettingsTheme
    ) = viewModelScope.launch {
        repository.settingsRepository.changeSettingsTheme(
            settingsTheme = settingsTheme
        )
    }

    fun updateAllowImagePreview(
        settingsAllowImagePreview: SettingsAllowImagePreview
    ) = viewModelScope.launch {
        repository.settingsRepository.changeAllowImagePreview(
            settingsAllowImagePreview = settingsAllowImagePreview
        )
    }

    fun logout() = viewModelScope.launch {
        repository.accountRepository.deleteCurrentAccount()
    }
}