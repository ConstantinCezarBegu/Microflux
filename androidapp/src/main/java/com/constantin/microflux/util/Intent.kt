package com.constantin.microflux.util

import android.content.Intent

fun shareArticleIntent(title: String, url: String): Intent {
    val sendIntent = Intent(Intent.ACTION_SEND)
    sendIntent.putExtra(Intent.EXTRA_TITLE, title)
    sendIntent.putExtra(Intent.EXTRA_TEXT, url)
    sendIntent.type = "text/plain"
    return Intent.createChooser(sendIntent, null)
}