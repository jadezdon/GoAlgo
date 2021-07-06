package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.SortItemType
import com.zhouppei.goalgo.view.SortView

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
            items[items.size - i - 1].type = SortItemType.SORTED
            if (items.size - i - 2 >= 0) items[items.size - i - 2].type = SortItemType.UNSORTED
            update()
        }

        complete()
    }

    override fun sourceCode(): String {
        return "procedure bubbleSort(A : list of sortable items)<br>" +
                "    n := length(A)<br>" +
                "    repeat<br>" +
                "        swapped := false<br>" +
                "        for i := 1 to n-1 inclusive do<br>" +
                "            /* if this pair is out of order */<br>" +
                "            if A[i-1] > A[i] then<br>" +
                "                /* swap them and remember something changed */<br>" +
                "                swap(A[i-1], A[i])<br>" +
                "                swapped := true<br>" +
                "            end if<br>" +
                "        end for<br>" +
                "    until not swapped<br>" +
                "end procedure"
    }

    override fun description(): String {
        return "Bubble sort, sometimes referred to as sinking sort, is a simple sorting algorithm that repeatedly steps through the list, " +
                "compares adjacent elements and swaps them if they are in the wrong order. The pass through the list is repeated until " +
                "the list is sorted. The algorithm, which is a comparison sort, is named for the way smaller or larger elements \"bubble\" " +
                "to the top of the list."
    }
}