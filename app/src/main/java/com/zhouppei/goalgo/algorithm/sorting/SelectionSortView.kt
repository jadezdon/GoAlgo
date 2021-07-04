package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.SortItemType
import com.zhouppei.goalgo.view.SortView

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
            setCaption("Min: [$minIdx] = ${items[minIdx].value}")
            items[i].isPivot = true
            update()

            for (j in i+1 until items.size) {
                compare(minIdx, j)

                if (items[j].value < items[minIdx].value) {
                    items[minIdx].type = SortItemType.UNSORTED

                    minIdx = j
                    setCaption("Min: [$minIdx] = ${items[minIdx].value}")

                    items[minIdx].type = SortItemType.CURRENT
                    update()
                }
            }

            items[items.size - 1].type = SortItemType.UNSORTED
            items[minIdx].type = SortItemType.CURRENT
            items[i].type = SortItemType.CURRENT
            items[i].isPivot = false
            update()

            setCaption("")
            swap(i, minIdx)

            items[minIdx].type = SortItemType.UNSORTED
            items[i].type = SortItemType.SORTED
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