package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.ItemState
import kotlinx.coroutines.delay

class InsertionSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun sort() {
        super.sort()
        var key = 0
        var j = 0
        for (i in 1 until items.size) {
            key = items[i].value
            captionText = "key = $key"
            items[i].state = ItemState.CURRENT
            items[i].isPivot = true
            j = i - 1
            update()
            delay(config.sortingSpeed)
            items[i].state = ItemState.UNSORTED
            items[i].isPivot = false

            while (j >= 0 && items[j].value > key) {
                items[j+1].state = ItemState.CURRENT
                items[j + 1].value = items[j].value
                items[j].value = 0
                update()
                delay(config.sortingSpeed)
                items[j+1].state = ItemState.UNSORTED

                j -= 1
            }

            items[j + 1].state = ItemState.CURRENT
            items[j + 1].value = key
            update()
            delay(config.sortingSpeed)
            items[j + 1].state = ItemState.UNSORTED
        }

        complete()
    }
}