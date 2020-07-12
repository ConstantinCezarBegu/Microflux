package com.example.constaflux2.util

import androidx.appcompat.app.AppCompatDelegate
import com.example.constaflux2.data.SettingsTheme

fun SettingsTheme?.toAndroidDelegate() = when (this) {
    SettingsTheme.AUTO -> defaultTheme
    SettingsTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
    SettingsTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    else -> defaultTheme
}

val defaultTheme =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    } else {
        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    }