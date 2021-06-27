package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.ItemState
import kotlinx.coroutines.delay

class SelectionSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun sort() {
        super.sort()

        var minIdx = 0
        for (i in 0 until items.size-1) {
            minIdx = i
            captionText = "Min: [$minIdx] = ${items[minIdx].value}"
            items[i].isPivot = true
            update()
            delay(config.sortingSpeed)

            for (j in i+1 until items.size) {
                compare(minIdx, j)
                delay(config.sortingSpeed)
                items[j].state = ItemState.UNSORTED

                if (items[j].value < items[minIdx].value) {
                    items[minIdx].state = ItemState.UNSORTED

                    minIdx = j
                    captionText = "Min: [$minIdx] = ${items[minIdx].value}"

                    items[minIdx].state = ItemState.CURRENT
                    update()
                    delay(config.sortingSpeed)
                }
            }

            items[items.size - 1].state = ItemState.UNSORTED
            items[minIdx].state = ItemState.CURRENT
            items[i].state = ItemState.CURRENT
            items[i].isPivot = false
            update()
            delay(config.sortingSpeed)

            captionText = ""
            swap(i, minIdx)
            delay(config.sortingSpeed)

            items[minIdx].state = ItemState.UNSORTED
            items[i].state = ItemState.SORTED
            update()
            delay(config.sortingSpeed)
        }

        complete()
    }
}