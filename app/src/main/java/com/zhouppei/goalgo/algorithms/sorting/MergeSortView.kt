package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.extensions.clone
import com.zhouppei.goalgo.models.ItemState
import kotlinx.coroutines.delay
import kotlin.math.min

class MergeSortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SortView(context, attrs, defStyleAttr) {

    override suspend fun sort() {
        super.sort()

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
                    items[left].state = ItemState.CURRENT
                    items[right].state = ItemState.CURRENT

                    if (items[left].value < items[right].value) {
                        tempList.add(items[left].value)

                        setCaption(listToString(tempList))
                        items[left].isPivot = true
                        update()
                        items[left].isPivot = false
                        items[left].state = ItemState.UNSORTED

                        left += 1
                    } else {
                        tempList.add(items[right].value)

                        setCaption(listToString(tempList))
                        items[right].isPivot = true
                        update()
                        items[right].isPivot = false
                        items[right].state = ItemState.UNSORTED

                        right += 1
                    }
                }

                if (left > mid) {
                    items[left-1].state = ItemState.UNSORTED
                    update()
                }

                if (right > end) {
                    items[right-1].state = ItemState.UNSORTED
                    update()
                }

                while (left <= mid) {
                    tempList.add(items[left].value)

                    setCaption(listToString(tempList))
                    items[left].state = ItemState.CURRENT
                    items[left].isPivot = true
                    update()
                    items[left].state = ItemState.UNSORTED
                    items[left].isPivot = false

                    left += 1
                }

                while (right <= end) {
                    tempList.add(items[right].value)

                    setCaption(listToString(tempList))
                    items[right].state = ItemState.CURRENT
                    items[right].isPivot = true
                    update()
                    items[right].state = ItemState.UNSORTED
                    items[right].isPivot = false

                    right += 1
                }

                var tempIdx = 0
                for (idx in start until end+1) {
                    setCaption(listToString(tempList, tempIdx))
                    items[idx].state = ItemState.CURRENT
                    update()

                    items[idx].value = tempList[tempIdx]

                    setCaption(listToString(tempList, tempIdx))
                    update()
                    items[idx].state = ItemState.UNSORTED

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
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}