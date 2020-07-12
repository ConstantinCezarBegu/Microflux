package com.example.constaflux2.repository.util

import android.util.Base64

fun String.decodeBase64(): ByteArray =
    Base64.decode(
        this.replace(Regex("^(image/.*;base64,)"), "")
            .toByteArray(), 0
    )