package com.zhouppei.goalgo.models

import android.graphics.RectF

class SortItem(
    var value: Int,
    var type: SortItemType = SortItemType.UNSORTED
) {
    var coordinates: RectF = RectF(0f, 0f, 0f, 0f)
    var isPivot = false
}

enum class SortItemType {
    UNSORTED, SORTED, CURRENT
}