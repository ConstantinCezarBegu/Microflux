package com.example.constaflux2.util

import android.content.Context
import androidx.annotation.StringRes

sealed class AndroidString {
    data class Res(@StringRes val resId: Int) : AndroidString()
    data class Raw(val string: String) : AndroidString()
}

fun Context.getString(androidString: AndroidString): String {
    return when (androidString) {
        is AndroidString.Res -> getString(androidString.resId)
        is AndroidString.Raw -> androidString.string
    }
}

fun @receiver:StringRes Int.toAndroidString() = AndroidString.Res(this)

fun String.toAndroidString() = AndroidString.Raw(this)