package com.zhouppei.goalgo.algorithms.sort

import android.content.Context
import android.util.AttributeSet

class BubbleSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override fun sort() {
        for (i in 0 until items.size-1) {
            for (j in i+1 until items.size) {
                addCompareCommand(i, j)
                if (items[i].value > items[j].value) {
                    addSwapCommand(i, j)
                }
            }
        }
        addCompleteCommand()
    }
}