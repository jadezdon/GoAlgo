package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.SortItemType
import com.zhouppei.goalgo.views.SortView

class InsertionSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var key = 0
        var j = 0
        for (i in 1 until items.size) {
            key = items[i].value

            sortingInterval = Pair(0, i)

            setCaption("key = $key")
            items[i].type = SortItemType.CURRENT
            items[i].isPivot = true
            update()
            items[i].type = SortItemType.UNSORTED


            j = i - 1

            compare(i, j)
            items[i].isPivot = false

            while (j >= 0 && items[j].value > key) {
                items[j + 1].isPivot = false
                items[j].isPivot = true
                swap(j, j+1)

                j -= 1

                if (j >= 0) {
                    compare(j, j + 1)
                }
            }

            items[j + 1].value = key

            items[j + 1].type = SortItemType.CURRENT
            items[j + 1].isPivot = true
            update()
            items[j + 1].type = SortItemType.UNSORTED
            items[j + 1].isPivot = false
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