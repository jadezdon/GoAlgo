package com.zhouppei.goalgo.models

import android.graphics.RectF

class Item(
    var value: Int,
    var type: ItemType = ItemType.UNSORTED
) {
    var coordinates: RectF = RectF(0f, 0f, 0f, 0f)
    var isPivot = false
}

enum class ItemType {
    UNSORTED, SORTED, CURRENT
}