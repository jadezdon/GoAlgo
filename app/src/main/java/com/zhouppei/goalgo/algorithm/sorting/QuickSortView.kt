package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.SortItemType
import com.zhouppei.goalgo.view.SortView

class QuickSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        val stack = mutableListOf<Pair<Int, Int>>()
        var start = 0
        var end = items.size - 1

        stack.add(Pair(start, end))

        while (stack.isNotEmpty()) {
            val curr = stack.removeLast()
            start = curr.first
            end = curr.second

            sortingInterval = Pair(start, end)

            // rearrange elements across pivot
            var partitionIdx = start
            items[end].isPivot = true
            items[partitionIdx].type = SortItemType.CURRENT
            update()

            for (j in start until end) {
                compare(j, end)

                if (items[j].value <= items[end].value) {
                    highlight(listOf(partitionIdx, j))
                    swap(partitionIdx, j)

                    partitionIdx += 1

                    highlight(listOf(partitionIdx))
                }
            }
            swap(partitionIdx, end)
            items[end].isPivot = false

            if (start < partitionIdx-1) {
                stack.add(Pair(start, partitionIdx - 1))
            }

            if (partitionIdx + 1 < end) {
                stack.add(Pair(partitionIdx + 1, end))
            }
        }

        completeAnimation()
        complete()
    }

    override fun sourceCode(): String {
        return "algorithm quicksort(A, lo, hi) is <br>" +
                "    if lo < hi then <br>" +
                "        p := partition(A, lo, hi) <br>" +
                "        quicksort(A, lo, p - 1) <br>" +
                "        quicksort(A, p + 1, hi) <br>" +
                " <br>" +
                "algorithm partition(A, lo, hi) is <br>" +
                "    pivot := A[hi] <br>" +
                "    i := lo <br>" +
                "    for j := lo to hi do <br>" +
                "        if A[j] < pivot then <br>" +
                "            swap A[i] with A[j] <br>" +
                "            i := i + 1 <br>" +
                "    swap A[i] with A[hi] <br>" +
                "    return i"
    }

    override fun description(): String {
        return "Quicksort is an in-place sorting algorithm. Developed by British computer scientist Tony Hoare in 1959 and published in 1961, " +
                "it is still a commonly used algorithm for sorting. When implemented well, it can be somewhat faster than merge sort and about " +
                "two or three times faster than heapsort."
    }
}