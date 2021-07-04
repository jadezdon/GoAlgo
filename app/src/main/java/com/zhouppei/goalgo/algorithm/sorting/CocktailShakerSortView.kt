package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.views.SortView

class CocktailShakerSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var beginIdx = 0
        var endIdx = items.size - 1

        while (beginIdx < endIdx) {
            var newBeginIdx = endIdx
            var newEndIdx = beginIdx
            for (i in beginIdx until endIdx) {
                compare(i, i+1)
                if (items[i].value > items[i + 1].value) {
                    swap(i, i+1)
                    newEndIdx = i
                }
            }

            endIdx = newEndIdx

            for (i in endIdx downTo beginIdx) {
                compare(i, i+1)
                if (items[i].value > items[i + 1].value) {
                    swap(i, i+1)
                    newBeginIdx = i
                }
            }

            beginIdx = newBeginIdx
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