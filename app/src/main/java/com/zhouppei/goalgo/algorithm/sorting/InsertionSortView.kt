package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.SortItemType
import com.zhouppei.goalgo.view.SortView

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
        return "i ← 1 <br>" +
                "while i < length(A) <br>" +
                "    x ← A[i] <br>" +
                "    j ← i - 1 <br>" +
                "    while j >= 0 and A[j] > x <br>" +
                "        A[j+1] ← A[j] <br>" +
                "        j ← j - 1 <br>" +
                "    end while <br>" +
                "    A[j+1] ← x <br>" +
                "    i ← i + 1 <br>" +
                "end while"
    }

    override fun description(): String {
        return "Insertion sort is a simple sorting algorithm that builds the final sorted array (or list) one item at a time. " +
                "It is much less efficient on large lists than more advanced algorithms such as quicksort, heapsort, or merge sort. " +
                "However, insertion sort provides several advantages: efficient for (quite) small data sets, much like other quadratic sorting algorithms," +
                "stable, in-place."
    }
}