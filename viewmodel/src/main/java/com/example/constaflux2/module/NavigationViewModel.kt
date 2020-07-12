package com.example.constaflux2.module

import com.example.constaflux2.module.util.BaseViewModel
import com.example.constaflux2.repository.ConstafluxRepository
import kotlin.coroutines.CoroutineContext

class NavigationViewModel(
    context: CoroutineContext,
    repository: ConstafluxRepository
) : BaseViewModel() {
    val currentAccount = repository.accountRepository.currentAccount
}