package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.ItemState
import kotlinx.coroutines.delay

class QuickSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun sort() {
        super.sort()

        val stack = mutableListOf<Pair<Int, Int>>()
        var start = 0
        var end = items.size - 1

        stack.add(Pair(start, end))

        while (stack.isNotEmpty()) {
            val curr = stack.removeLast()
            start = curr.first
            end = curr.second

            sortingInterval = Pair(start, end)

            // rearrange elements across pivot
            var pivotIdx = start
            items[end].isPivot = true
            items[pivotIdx].state = ItemState.CURRENT
            update()
            delay(config.sortingSpeed)

            for (j in start until end) {
                compare(j, end)
                delay(config.sortingSpeed)
                items[j].state = ItemState.UNSORTED
                items[end].state = ItemState.UNSORTED

                if (items[j].value <= items[end].value) {
                    items[j].state = ItemState.CURRENT
                    swap(pivotIdx, j)
                    delay(config.sortingSpeed)
                    items[pivotIdx].state = ItemState.UNSORTED
                    items[j].state = ItemState.UNSORTED

                    pivotIdx += 1

                    items[pivotIdx].state = ItemState.CURRENT
                    update()
                    delay(config.sortingSpeed)
                }
            }
            items[end].state = ItemState.CURRENT
            swap(pivotIdx, end)
            delay(config.sortingSpeed)
            items[pivotIdx].state = ItemState.UNSORTED
            items[end].state = ItemState.UNSORTED
            items[end].isPivot = false

            if (start < pivotIdx-1) {
                stack.add(Pair(start, pivotIdx - 1))
            }

            if (pivotIdx + 1 < end) {
                stack.add(Pair(pivotIdx + 1, end))
            }
        }

        completeAnimation()

        complete()
    }
}