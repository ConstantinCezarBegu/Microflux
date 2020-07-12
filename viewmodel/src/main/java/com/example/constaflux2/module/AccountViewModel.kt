package com.example.constaflux2.module

import androidx.lifecycle.viewModelScope
import com.example.constaflux2.data.*
import com.example.constaflux2.database.Account
import com.example.constaflux2.module.util.BaseViewModel
import com.example.constaflux2.module.util.load
import com.example.constaflux2.repository.ConstafluxRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class AccountViewmodel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : BaseViewModel() {
    abstract val account: Deferred<Account>?

    private val _upsertAccountProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val upsertAccountProgression: StateFlow<Result<Unit>> = _upsertAccountProgression

    fun upsertAccount(
        serverUrl: ServerUrl,
        userName: UserName,
        userPassword: UserPassword
    ) = viewModelScope.launch {
        _upsertAccountProgression.load {
            repository.accountRepository.upsertAccount(
                serverUrl = serverUrl,
                userName = userName,
                userPassword = userPassword
            )
        }
    }

    abstract fun deleteAccount(): Job?
}

class CreateAccountViewModel(
    context: CoroutineContext,
    repository: ConstafluxRepository
) : AccountViewmodel(context, repository) {
    override val account: Deferred<Account>? = null
    override fun deleteAccount(): Job? = null
}

class UpdateAccountViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository,
    serverId: ServerId,
    userId: UserId
) : AccountViewmodel(context, repository) {
    override val account = viewModelScope.async(context) {
        repository.accountRepository.getAccount(
            serverId = serverId,
            userId = userId
        ).first()
    }

    override fun deleteAccount() = viewModelScope.launch {
        repository.accountRepository.deleteCurrentAccount()
    }

}

class AccountDialogViewmodel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : BaseViewModel() {

    val currentAccount = repository.accountRepository.getCurrentAccount()

    val nonCurrentAccounts = repository.accountRepository.getNonCurrentAccounts()

    fun changeAccounts(
        account: Account
    ) = viewModelScope.launch {
        repository.accountRepository.changeAccount(
            serverId = account.serverId,
            userId = account.userId
        )
    }
}