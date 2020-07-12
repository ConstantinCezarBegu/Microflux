package com.example.constaflux2.util

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use

inline val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

@ColorInt
fun Context.getThemeColor(
    @AttrRes attrResId: Int,
    @ColorInt defaultValue: Int = Color.BLACK
) = obtainStyledAttributes(null, intArrayOf(attrResId)).use { it.getColor(0, defaultValue) }

@ColorInt
fun Context.getAttributeColor(
    @AttrRes attrResId: Int
) = resources.getColor(
    TypedValue().also {
        theme.resolveAttribute(attrResId, it, true)
    }.resourceId,
    theme
)

