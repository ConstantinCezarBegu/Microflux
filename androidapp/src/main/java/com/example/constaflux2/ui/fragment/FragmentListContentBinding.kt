package com.example.constaflux2.ui.fragment

import com.example.constaflux2.R
import com.example.constaflux2.data.Result
import com.example.constaflux2.databinding.FragmentListContentBinding
import com.example.constaflux2.util.EventSnackbar
import com.example.constaflux2.util.makeSnackbar
import com.example.constaflux2.util.toAndroidString


fun FragmentListContentBinding.onRefresh(result: Result<Unit>) {
    when (result) {
        is Result.InProgress -> {
            contentRefresh.isRefreshing = true
        }
        is Result.Complete -> {
            contentRefresh.isRefreshing = false
        }
        else -> {
        }
    }
}

fun FragmentListContentBinding.onError(result: Result<Unit>, eventSnackbar: EventSnackbar) {
    if (result is Result.Error) {
        val stringRes = if (result is Result.Error.NetworkError) R.string.no_connectivity_error
        else R.string.error
        val snackbar = root.makeSnackbar(stringRes.toAndroidString()).setAnchorView(bottomAppBar)
        eventSnackbar.set(snackbar)
    }
}