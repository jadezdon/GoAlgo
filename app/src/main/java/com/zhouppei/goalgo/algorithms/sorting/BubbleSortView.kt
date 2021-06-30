package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.ItemType
import com.zhouppei.goalgo.views.SortView

class BubbleSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        for (i in 0 until items.size - 1) {
            for (j in 0 until items.size - i - 1) {
                compare(j, j+1)

                if (items[j].value > items[j+1].value) {
                    swap(j, j+1)
                }
            }
            items[items.size - i - 1].type = ItemType.SORTED
            if (items.size - i - 2 >= 0) items[items.size - i - 2].type = ItemType.UNSORTED
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