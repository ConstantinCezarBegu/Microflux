package com.constantin.microflux.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.NestedScrollingChild
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.constantin.microflux.R
import com.constantin.microflux.util.getThemeColor

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

