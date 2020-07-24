package com.constantin.microflux.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.constantin.microflux.R
import com.constantin.microflux.data.*
import com.constantin.microflux.databinding.FragmentLoginBinding
import com.constantin.microflux.module.AccountViewmodel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.util.BindingFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountFragment() : BindingFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: AccountViewmodel

    private val args: AccountFragmentArgs by navArgs()
    private var serverId = ServerId.NO_SERVER
    private var userId = UserId.NO_USER
    private var firstTimeLaunch = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        serverId = args.serverId.let(::ServerId)
        userId = args.userId.let(::UserId)
        firstTimeLaunch = args.firstTimeLaunch
        viewmodel =
            if (serverId == ServerId.NO_SERVER && userId == UserId.NO_USER) viewModelFactory.create(
                State.CreateAccount
            ) as AccountViewmodel
            else viewModelFactory.create(
                State.UpdateAccount(
                    serverId = serverId,
                    userId = userId
                )
            ) as AccountViewmodel
    }

    override fun onBindingCreated(
        binding: FragmentLoginBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            if (savedInstanceState == null) attachAccount()
            attachLoginButton()
            attachDeleteButton()
            attachBackButton()
            observeErrors()
        }
    }

    private fun FragmentLoginBinding.attachAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.account?.await()?.run {
                urlEditText.setText(serverUrl.url)
                usernameEditText.setText(userName.name)
                passwordEditText.setText(userPassword.password)
            }
        }
    }

    private fun FragmentLoginBinding.attachLoginButton() {
        this.run {
            loginButton.setOnClickListener { connectToMiniflux() }
        }
    }

    private fun FragmentLoginBinding.attachDeleteButton() {
        deleteButton.run {
            if (serverId != ServerId.NO_SERVER && userId != UserId.NO_USER) visibility =
                View.VISIBLE
            setOnClickListener {
                viewmodel.deleteAccount()?.invokeOnCompletion {
                    findNavController().navigate(
                        AccountFragmentDirections.actionAccountFragmentToEntryFragment()
                    )
                }
            }
        }
    }

    private fun FragmentLoginBinding.attachBackButton() {
        backButton.run {
            if (!firstTimeLaunch) visibility = View.VISIBLE
            setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun FragmentLoginBinding.observeErrors() {
        viewmodel.upsertAccountProgression.onEach {
            this.run {
                when (it) {
                    is Result.Error.NetworkError.ServerUrlError -> {
                        invalidURL(true)
                    }
                    is Result.Error.NetworkError.AuthorizationError -> {
                        invalidCredential(true)
                    }
                    is Result.Error.NetworkError -> {
                        showConnectivityError(true)
                    }
                    is Result.InProgress -> {
                        invalidURL(false)
                        invalidCredential(false)
                        showConnectivityError(false)
                        loadingLogin(true)
                    }
                    is Result.Success -> {
                        findNavController().navigate(
                            AccountFragmentDirections.actionAccountFragmentToEntryFragment()
                        )
                    }
                    is Result.Complete -> {
                        loadingLogin(false)
                    }
                    else -> {
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentLoginBinding.connectToMiniflux() {
        this.run {
            if (accountRequiredCheck()) {
                viewmodel.upsertAccount(
                    ServerUrl(urlEditText.text.toString()),
                    UserName(usernameEditText.text.toString()),
                    UserPassword(passwordEditText.text.toString())
                )
            }
        }
    }

    private fun FragmentLoginBinding.accountRequiredCheck(): Boolean {
        var check = true
        this.run {
            if (urlEditText.text.isNullOrBlank()) {
                urlTextInput.error = requireContext().getString(R.string.provide_url_server)
                urlTextInput.isErrorEnabled = true
                check = false
            } else {
                urlTextInput.isErrorEnabled = false
            }

            if (usernameEditText.text.isNullOrBlank()) {
                usernameTextInput.error = requireContext().getString(R.string.provide_username)
                usernameTextInput.isErrorEnabled = true
                check = false
            } else {
                usernameTextInput.isErrorEnabled = false
            }

            if (passwordEditText.text.isNullOrBlank()) {
                paswordTextInput.error = requireContext().getString(R.string.provide_password)
                paswordTextInput.isErrorEnabled = true
                check = false
            } else {
                paswordTextInput.isErrorEnabled = false
            }
        }
        return check
    }

    private fun FragmentLoginBinding.invalidURL(show: Boolean) {
        this.run {
            urlTextInput.error = requireContext().getString(R.string.invalid_url)
            urlTextInput.isErrorEnabled = show
        }
    }

    private fun FragmentLoginBinding.invalidCredential(show: Boolean) {
        this.run {
            usernameTextInput.error = requireContext().getString(R.string.invalid_username)
            usernameTextInput.isErrorEnabled = show
            paswordTextInput.error = requireContext().getString(R.string.invalid_password)
            paswordTextInput.isErrorEnabled = show
        }
    }

    private fun FragmentLoginBinding.showConnectivityError(show: Boolean) {
        this.run {
            if (show) {
                connectivityErrorWorning.visibility = View.VISIBLE
            } else {
                connectivityErrorWorning.visibility = View.GONE
            }
        }
    }

    private fun FragmentLoginBinding.loadingLogin(loading: Boolean) {
        if (loading) {
            if (serverId != ServerId.NO_SERVER && userId != UserId.NO_USER) deleteButton.visibility =
                View.INVISIBLE
            progressBarLoadingLogin.visibility = View.VISIBLE
            loginButton.visibility = View.INVISIBLE
        } else {
            if (serverId != ServerId.NO_SERVER && userId != UserId.NO_USER) deleteButton.visibility =
                View.VISIBLE
            progressBarLoadingLogin.visibility = View.INVISIBLE
            loginButton.visibility = View.VISIBLE
        }
    }
}