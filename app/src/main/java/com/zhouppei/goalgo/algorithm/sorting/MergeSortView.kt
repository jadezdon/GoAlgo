package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.SortItemType
import com.zhouppei.goalgo.view.SortView
import kotlin.math.min

class MergeSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        // divide the list into blocks of size m
        // m = [1, 2, 4, 8, 16 …]
        var m = 1
        val n = items.size
        while (m <= n - 1) {
            // m = 1, start = [0, 2, 4, 6, 8 …]
            // m = 2, start = [0, 4, 8, 12 …]
            // m = 4, start = [0, 8, 16 …]
            for (start in 0 until n-1 step 2*m) {
                val mid = min(start + m - 1, n-1)
                val end = min(start + 2*m - 1, n-1)

                sortingInterval = Pair(start, end)

                // merge [start ... mid] and [mid+1 ... end] lists
                val tempList = mutableListOf<Int>()
                var left = start
                var right = mid + 1

                while (left <= mid && right <= end) {
                    compare(left, right)
                    items[left].type = SortItemType.CURRENT
                    items[right].type = SortItemType.CURRENT

                    if (items[left].value < items[right].value) {
                        tempList.add(items[left].value)

                        setCaption(listToString(tempList))
                        items[left].isPivot = true
                        update()
                        items[left].isPivot = false
                        items[left].type = SortItemType.UNSORTED

                        left += 1
                    } else {
                        tempList.add(items[right].value)

                        setCaption(listToString(tempList))
                        items[right].isPivot = true
                        update()
                        items[right].isPivot = false
                        items[right].type = SortItemType.UNSORTED

                        right += 1
                    }
                }

                if (left > mid) {
                    items[left-1].type = SortItemType.UNSORTED
                    update()
                }

                if (right > end) {
                    items[right-1].type = SortItemType.UNSORTED
                    update()
                }

                while (left <= mid) {
                    tempList.add(items[left].value)

                    setCaption(listToString(tempList))
                    items[left].type = SortItemType.CURRENT
                    items[left].isPivot = true
                    update()
                    items[left].type = SortItemType.UNSORTED
                    items[left].isPivot = false

                    left += 1
                }

                while (right <= end) {
                    tempList.add(items[right].value)

                    setCaption(listToString(tempList))
                    items[right].type = SortItemType.CURRENT
                    items[right].isPivot = true
                    update()
                    items[right].type = SortItemType.UNSORTED
                    items[right].isPivot = false

                    right += 1
                }

                var tempIdx = 0
                for (idx in start until end+1) {
                    setCaption(listToString(tempList, tempIdx))
                    items[idx].type = SortItemType.CURRENT
                    update()

                    items[idx].value = tempList[tempIdx]

                    setCaption(listToString(tempList, tempIdx))
                    update()
                    items[idx].type = SortItemType.UNSORTED

                    tempIdx += 1
                }
            }

            setCaption("")
            update()

            m *= 2
        }

        completeAnimation()
        complete()
    }

    private fun listToString(list: MutableList<Int>, selectedPosition: Int = -1): String {
        var s = "Temp list = "
        list.forEachIndexed { index, item ->
            s += if (index == selectedPosition) "[$item] " else " $item  "
        }
        return s
    }

    override fun sourceCode(): String {
        return "function merge_sort(list m) is <br>" +
                "    // Base case. A list of zero or one elements is sorted, by definition. <br>" +
                "    if length of m ≤ 1 then <br>" +
                "        return m <br>" +
                " <br>" +
                "    // Recursive case. First, divide the list into equal-sized sublists <br>" +
                "    // consisting of the first half and second half of the list. <br>" +
                "    // This assumes lists start at index 0. <br>" +
                "    var left := empty list <br>" +
                "    var right := empty list <br>" +
                "    for each x with index i in m do <br>" +
                "        if i < (length of m)/2 then <br>" +
                "            add x to left <br>" +
                "        else <br>" +
                "            add x to right <br>" +
                " <br>" +
                "    // Recursively sort both sublists. <br>" +
                "    left := merge_sort(left) <br>" +
                "    right := merge_sort(right) <br>" +
                " <br>" +
                "    // Then merge the now-sorted sublists. <br>" +
                "    return merge(left, right)" +
                " <br>" +
                "function merge(left, right) is <br>" +
                "    var result := empty list <br>" +
                " <br>" +
                "    while left is not empty and right is not empty do <br>" +
                "        if first(left) ≤ first(right) then <br>" +
                "            append first(left) to result <br>" +
                "            left := rest(left) <br>" +
                "        else <br>" +
                "            append first(right) to result <br>" +
                "            right := rest(right) <br>" +
                " <br>" +
                "    // Either left or right may have elements left; consume them. <br>" +
                "    // (Only one of the following loops will actually be entered.) <br>" +
                "    while left is not empty do <br>" +
                "        append first(left) to result <br>" +
                "        left := rest(left) <br>" +
                "    while right is not empty do <br>" +
                "        append first(right) to result <br>" +
                "        right := rest(right) <br>" +
                "    return result"
    }

    override fun description(): String {
        return "Merge sort (also commonly spelled as mergesort) is an efficient, general-purpose, and comparison-based sorting algorithm. " +
                "Most implementations produce a stable sort, which means that the order of equal elements is the same in the input and output. " +
                "Merge sort is a divide and conquer algorithm that was invented by John von Neumann in 1945. " +
                "A detailed description and analysis of bottom-up merge sort appeared in a report by Goldstine and von Neumann as early as 1948."
    }
}