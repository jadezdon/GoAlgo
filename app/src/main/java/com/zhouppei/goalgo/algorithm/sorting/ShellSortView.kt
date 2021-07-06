package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.SortItemType
import com.zhouppei.goalgo.view.SortView

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

                setCaption("gap = $gap, key = $temp")
                items[i].type = SortItemType.CURRENT
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

                items[j].type = SortItemType.CURRENT
                items[j].isPivot = true
                update()
                items[j].type = SortItemType.UNSORTED
                items[j].isPivot = false
            }

            gap /= 2
        }

        completeAnimation()
        complete()
    }

    override fun sourceCode(): String {
        return  "# Sort an array a[0...n-1]. <br>" +
                "gaps = [n / 2, n / 4, ... 2, 1] <br>" +
                " <br>" +
                "# Start with the largest gap and work down to a gap of 1 <br>" +
                "foreach (gap in gaps) <br>" +
                "{ <br>" +
                "    # Do a gapped insertion sort for this gap size. <br>" +
                "    # The first gap elements a[0..gap-1] are already in gapped order <br>" +
                "    # keep adding one more element until the entire array is gap sorted <br>" +
                "    for (i = gap; i < n; i += 1) <br>" +
                "    { <br>" +
                "        # add a[i] to the elements that have been gap sorted <br>" +
                "        # save a[i] in temp and make a hole at position i <br>" +
                "        temp = a[i] <br>" +
                "        # shift earlier gap-sorted elements up until the correct location for a[i] is found <br>" +
                "        for (j = i; j >= gap and a[j - gap] > temp; j -= gap) <br>" +
                "        { <br>" +
                "            a[j] = a[j - gap] <br>" +
                "        } <br>" +
                "        # put temp (the original a[i]) in its correct location <br>" +
                "        a[j] = temp <br>" +
                "    } <br>" +
                "}"
    }

    override fun description(): String {
        return  "Shellsort, also known as Shell sort or Shell's method, is an in-place comparison sort. It can be seen as either a generalization " +
                "of sorting by exchange (bubble sort) or sorting by insertion (insertion sort). The method starts by sorting pairs of elements " +
                "far apart from each other, then progressively reducing the gap between elements to be compared."
    }
}