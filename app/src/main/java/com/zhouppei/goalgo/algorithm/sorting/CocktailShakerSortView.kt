package com.zhouppei.goalgo.algorithm.sorting

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.view.SortView

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
        return "procedure cocktailShakerSort(A : list of sortable items) is <br>" +
                "    do <br>" +
                "        swapped := false <br>" +
                "        for each i in 0 to length(A) − 2 do: <br>" +
                "            if A[i] > A[i + 1] then // test whether the two elements are in the wrong order <br>" +
                "                swap(A[i], A[i + 1]) // let the two elements change places <br>" +
                "                swapped := true <br>" +
                "            end if <br>" +
                "        end for <br>" +
                "        if not swapped then <br>" +
                "            // we can exit the outer loop here if no swaps occurred. <br>" +
                "            break do-while loop <br>" +
                "        end if <br>" +
                "        swapped := false <br>" +
                "        for each i in length(A) − 2 to 0 do: <br>" +
                "            if A[i] > A[i + 1] then <br>" +
                "                swap(A[i], A[i + 1]) <br>" +
                "                swapped := true <br>" +
                "            end if <br>" +
                "        end for <br>" +
                "    while swapped // if no elements have been swapped, then the list is sorted <br>" +
                "end procedure"
    }

    override fun description(): String {
        return "Cocktail shaker sort, also known as bidirectional bubble sort, cocktail sort, shaker sort (which can also refer to a variant of selection sort), " +
                "ripple sort, shuffle sort, or shuttle sort, is an extension of bubble sort. The algorithm extends bubble sort by " +
                "operating in two directions. While it improves on bubble sort by more quickly moving items to the beginning of the list, " +
                "it provides only marginal performance improvements."
    }
}