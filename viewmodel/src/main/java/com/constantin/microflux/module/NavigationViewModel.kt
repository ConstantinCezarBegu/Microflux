package com.constantin.microflux.module

import com.constantin.microflux.module.util.BaseViewModel
import com.constantin.microflux.repository.ConstafluxRepository
import kotlin.coroutines.CoroutineContext

class NavigationViewModel(
    context: CoroutineContext,
    repository: ConstafluxRepository
) : BaseViewModel() {
    val currentAccount = repository.accountRepository.currentAccount
}