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
        return "/* a[0] to a[aLength-1] is the array to sort */ <br>" +
                "int i,j; <br>" +
                "int aLength; // initialise to a's length <br>" +
                " <br>" +
                "/* advance the position through the entire array */ <br>" +
                "/*   (could do i < aLength-1 because single element is also min element) */ <br>" +
                "for (i = 0; i < aLength-1; i++) <br>" +
                "{ <br>" +
                "    /* find the min element in the unsorted a[i .. aLength-1] */ <br>" +
                " <br>" +
                "    /* assume the min is the first element */ <br>" +
                "    int jMin = i; <br>" +
                "    /* test against elements after i to find the smallest */ <br>" +
                "    for (j = i+1; j < aLength; j++) <br>" +
                "    { <br>" +
                "        /* if this element is less, then it is the new minimum */ <br>" +
                "        if (a[j] < a[jMin]) <br>" +
                "        { <br>" +
                "            /* found new minimum; remember its index */ <br>" +
                "            jMin = j; <br>" +
                "        } <br>" +
                "    } <br>" +
                " <br>" +
                "    if (jMin != i)  <br>" +
                "    { <br>" +
                "        swap(a[i], a[jMin]); <br>" +
                "    } <br>" +
                "}"
    }

    override fun description(): String {
        return "selection sort is an in-place comparison sorting algorithm. It has an O(n^2) time complexity, which makes it inefficient " +
                "on large lists, and generally performs worse than the similar insertion sort. Selection sort is noted for its simplicity " +
                "and has performance advantages over more complicated algorithms in certain situations, particularly where auxiliary memory is limited."
    }
}