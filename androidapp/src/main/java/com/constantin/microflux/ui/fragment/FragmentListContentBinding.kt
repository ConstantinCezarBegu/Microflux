package com.constantin.microflux.ui.fragment

import com.constantin.microflux.R
import com.constantin.microflux.data.Result
import com.constantin.microflux.databinding.FragmentListContentBinding
import com.constantin.microflux.util.EventSnackbar
import com.constantin.microflux.util.makeSnackbar
import com.constantin.microflux.util.toAndroidString


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