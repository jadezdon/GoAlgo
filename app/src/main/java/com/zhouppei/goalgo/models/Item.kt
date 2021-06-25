package com.zhouppei.goalgo.models

import android.graphics.RectF

class Item(
    var value: Int,
    var state: ItemState = ItemState.UNSORTED
) {
    var coordinates: RectF = RectF(0f, 0f, 0f, 0f)
}

enum class ItemState {
    UNSORTED, SORTED, CURRENT
}