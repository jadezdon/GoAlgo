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

            for (j in start until end) {
                compare(j, end)

                if (items[j].value <= items[end].value) {
                    swap(pivotIdx, j)

                    pivotIdx += 1

                    items[pivotIdx].state = ItemState.CURRENT
                    update()
                }
            }
            swap(pivotIdx, end)
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

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}