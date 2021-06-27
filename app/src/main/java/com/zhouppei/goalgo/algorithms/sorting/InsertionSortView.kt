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
            j = i - 1

            sortingInterval = Pair(0, i)

            captionText = "key = $key"
            items[i].state = ItemState.CURRENT
            update()
            delay(config.sortingSpeed)
            items[i].state = ItemState.UNSORTED

            while (j >= 0 && items[j].value > key) {
                items[j + 1].value = items[j].value
                items[j].value = key

                items[j + 1].state = ItemState.CURRENT
                items[j].state = ItemState.CURRENT
                update()
                delay(config.sortingSpeed)
                items[j + 1].state = ItemState.UNSORTED
                items[j].state = ItemState.UNSORTED

                j -= 1
            }

            items[j + 1].value = key

            items[j + 1].state = ItemState.CURRENT
            update()
            delay(config.sortingSpeed)
            items[j + 1].state = ItemState.UNSORTED
        }

        complete()
    }
}