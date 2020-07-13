package com.constantin.microflux.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator


enum class Direction(val flag: Int) {
    LEFT(ItemTouchHelper.LEFT),
    RIGHT(ItemTouchHelper.RIGHT),
    START(ItemTouchHelper.START),
    END(ItemTouchHelper.END),
    UP(ItemTouchHelper.UP),
    DOWN(ItemTouchHelper.DOWN)
}

fun RecyclerView.disableAnimations() {
    (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
        false
}

fun Array<out Direction>.fold() = this.fold(0) { acc, direction -> acc or direction.flag }

val DEFAULT_SWIPE = arrayOf(Direction.START, Direction.END)

fun ItemTouchHelper.SimpleCallback.stopSwipes() {
    this.setDefaultSwipeDirs(0)
}

fun ItemTouchHelper.SimpleCallback.setSwipes(vararg directions: Direction = DEFAULT_SWIPE) {
    this.setDefaultSwipeDirs(directions.fold())
}

inline fun RecyclerView.onSwipe(
    vararg directions: Direction = DEFAULT_SWIPE,
    crossinline action: (RecyclerView.ViewHolder, Direction) -> Unit
): ItemTouchHelper.SimpleCallback {
    val swipeDirFlags = directions.fold()

    val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, swipeDirFlags) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val swipedDirection = Direction.values().single { it.flag == direction }
            action(viewHolder, swipedDirection)
        }
    }

    ItemTouchHelper(simpleCallback).attachToRecyclerView(this)

    return simpleCallback
}