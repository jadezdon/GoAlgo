package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.ItemState
import com.zhouppei.goalgo.views.SortView

class SelectionSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var minIdx = 0
        for (i in 0 until items.size-1) {
            minIdx = i
            captionText = "Min: [$minIdx] = ${items[minIdx].value}"
            items[i].isPivot = true
            update()

            for (j in i+1 until items.size) {
                compare(minIdx, j)

                if (items[j].value < items[minIdx].value) {
                    items[minIdx].state = ItemState.UNSORTED

                    minIdx = j
                    captionText = "Min: [$minIdx] = ${items[minIdx].value}"

                    items[minIdx].state = ItemState.CURRENT
                    update()
                }
            }

            items[items.size - 1].state = ItemState.UNSORTED
            items[minIdx].state = ItemState.CURRENT
            items[i].state = ItemState.CURRENT
            items[i].isPivot = false
            update()

            captionText = ""
            swap(i, minIdx)

            items[minIdx].state = ItemState.UNSORTED
            items[i].state = ItemState.SORTED
            update()
        }

        complete()
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}