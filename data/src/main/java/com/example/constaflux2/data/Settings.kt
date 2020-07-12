package com.example.constaflux2.data

inline class SettingsTheme(val theme: Int) {
    companion object {
        val AUTO = SettingsTheme(0)
        val LIGHT = SettingsTheme(1)
        val DARK = SettingsTheme(2)
    }
}

inline class SettingsAllowImagePreview(val allowImagePreview: Boolean){
    companion object{
        val ON = SettingsAllowImagePreview(true)
        val OFF = SettingsAllowImagePreview(false)
    }
}