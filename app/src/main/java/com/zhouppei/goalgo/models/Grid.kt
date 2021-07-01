package com.zhouppei.goalgo.models

import android.graphics.RectF

class Grid(var row: Int, var col: Int) {
    var items = MutableList(row) { MutableList(col) { GridItem() } }
}

class GridItem {
    var hasTopWall = true
    var hasLeftWall = true
    var hasRightWall = true
    var hasBottomWall = true
    var type = GridItemType.NONE
    var coordinate = Pair(0f, 0f)
    var boundingRectF = RectF(0f, 0f, 0f, 0f)
}

enum class GridItemType {
    NONE
}