package com.zhouppei.goalgo.models

class Item(
    val value: Int,
    var state: ItemState = ItemState.UNSORTED
) {
}

enum class ItemState {
    UNSORTED, SORTED, CURRENT
}