package com.example.constaflux2.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.NestedScrollingChild
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.constaflux2.R
import com.example.constaflux2.util.getThemeColor

class RefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs), NestedScrollingChild {

    init {
        setThemeColorScheme()
    }

    private fun SwipeRefreshLayout.setThemeColorScheme() {
        val foregroundColor = context.getThemeColor(R.attr.colorPrimary)
        val backgroundColor = context.getThemeColor(R.attr.colorBackgroundFloating)
        setColorSchemeColors(foregroundColor)
        setProgressBackgroundColorSchemeColor(backgroundColor)
    }
}

