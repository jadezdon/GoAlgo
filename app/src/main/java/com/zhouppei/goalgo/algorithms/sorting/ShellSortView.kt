package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.ItemState

class ShellSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var gap = items.size / 2
        while (gap > 0) {
            for (i in gap until items.size) {
                val temp = items[i].value

                captionText = "gap = $gap, key = $temp"
                items[i].state = ItemState.CURRENT
                items[i].isPivot = true
                update()

                var j = i

                compare(i, j - gap)
                items[i].isPivot = false

                while (j >= gap && items[j - gap].value > temp) {
                    items[j].isPivot = false
                    items[j - gap].isPivot = true
                    swap(j-gap, j)

                    j -= gap

                    if (j >= gap) {
                        compare(j - gap, j)
                    }
                }

                items[j].value = temp

                items[j].state = ItemState.CURRENT
                items[j].isPivot = true
                update()
                items[j].state = ItemState.UNSORTED
                items[j].isPivot = false
            }

            gap /= 2
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