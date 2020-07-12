package com.example.constaflux2.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.constaflux2.data.Result
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId
import com.example.constaflux2.databinding.DialogAccountBinding
import com.example.constaflux2.module.AccountDialogViewmodel
import com.example.constaflux2.module.State
import com.example.constaflux2.module.ViewmodelFactory
import com.example.constaflux2.ui.adapters.AccountListRecyclerViewAdapter
import com.example.constaflux2.util.BindingDialogFragment
import com.example.constaflux2.util.disableAnimations
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AccountDialog() : BindingDialogFragment<DialogAccountBinding>(
    DialogAccountBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: AccountDialogViewmodel

    private lateinit var recyclerViewAdapter: AccountListRecyclerViewAdapter

    private var serverIdToChange = ServerId.NO_SERVER
    private var userIdToChange = UserId.NO_USER

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewmodel = viewModelFactory.create(State.AccountDialog) as AccountDialogViewmodel
    }

    override fun onBindingCreated(binding: DialogAccountBinding, savedInstanceState: Bundle?) {
        binding.run {
            attachCurrentAccount()
            attachNonCurrentAccount()
            attachOpenCurrentAccountSettingsButton()
        }
    }

    private fun DialogAccountBinding.attachCurrentAccount() {
        viewmodel.currentAccount.onEach {
            currentAccountInfo.run {
                username.text = it.userName.name
                userUrl.text = it.serverUrl.url
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogAccountBinding.attachNonCurrentAccount() {
        nonCurrentAccount.disableAnimations()
        nonCurrentAccount.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        recyclerViewAdapter = AccountListRecyclerViewAdapter(
            accountClickCallback = { account, _, _ ->
                serverIdToChange = account.serverId
                userIdToChange = account.userId
                viewmodel.changeAccounts(
                    account = account
                )
                isCancelable = false
                findNavController().navigate(
                    AccountDialogDirections.actionAccountDialogToEntryFragment()
                )
            },
            addClickCallBack = { _, _ ->
                findNavController().navigate(
                    AccountDialogDirections.actionAccountDialogToAccountFragment()
                )
            }
        )

        nonCurrentAccount.adapter = recyclerViewAdapter

        viewmodel.nonCurrentAccounts.onEach { nonCurrentAccounts ->
            recyclerViewAdapter.submitList(nonCurrentAccounts)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogAccountBinding.attachOpenCurrentAccountSettingsButton() {
        accountSettingsButton.setOnClickListener {
            findNavController().navigate(
                AccountDialogDirections.actionAccountDialogToSettingsFragment()
            )
        }
    }
}