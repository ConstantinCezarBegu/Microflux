package com.constantin.microflux.util

import android.view.View
import com.constantin.microflux.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

enum class SnackbarLength(val flag: Int) {
    INDEFINITE(Snackbar.LENGTH_INDEFINITE),
    SHORT(Snackbar.LENGTH_SHORT),
    LONG(Snackbar.LENGTH_LONG)
}

inline fun View.makeSnackbar(
    text: AndroidString,
    actionText: AndroidString? = null,
    length: SnackbarLength = SnackbarLength.SHORT,
    show: Boolean = false,
    crossinline action: () -> Unit = { }
): Snackbar {
    val textString = context.getString(text)
    val actionTextString = actionText?.let(context::getString)
    return Snackbar.make(this, textString, length.flag).apply {
        if (actionTextString != null) {
            setAction(actionTextString) { action() }
            val oppositePrimaryColor = context.getColor(R.color.color_primary_opposite)
            setActionTextColor(oppositePrimaryColor)
        }
        if (show) {
            show()
        }
    }
}

class EventSnackbar {

    private var eventSnackbar: Snackbar? = null
    private var eventSnackbarCallback: Snackbar.Callback? = null

    fun set(snackbar: Snackbar?, onFinish: () -> Unit = { }) {
        val validDismissEvent = BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE
        eventSnackbarCallback?.onDismissed(eventSnackbar, validDismissEvent)

        snackbar ?: return

        eventSnackbarCallback = object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                transientBottomBar ?: return
                if (event != DISMISS_EVENT_MANUAL && event != DISMISS_EVENT_CONSECUTIVE) {
                    onFinish()
                }
                eventSnackbar = null
                eventSnackbarCallback = null
            }
        }
        eventSnackbar = snackbar.apply {
            addCallback(eventSnackbarCallback)
            show()
        }
    }
}
