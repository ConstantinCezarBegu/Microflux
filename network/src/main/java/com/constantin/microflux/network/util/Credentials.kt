package com.constantin.microflux.network.util

import android.util.Base64

class Credentials {
    companion object {
        fun basic(userName: String, password: String) = "${userName}:${password}".run {
            Base64.encodeToString(
                toByteArray(charset("UTF-8")),
                Base64.NO_WRAP
            ).run {
                "Basic $this"
            }
        }
    }
}