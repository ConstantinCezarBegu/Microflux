package com.constantin.microflux.util

import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar

fun Toolbar.replaceMenu(@MenuRes newMenu: Int) {
    menu.clear()
    inflateMenu(newMenu)
}