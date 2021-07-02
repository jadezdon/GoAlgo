package com.zhouppei.goalgo.algorithms.maze

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.CellType
import com.zhouppei.goalgo.views.GridView
import kotlin.random.Random

class RecursiveDivisionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        for (r in 0 until grid.rows) {
            for (c in 0 until grid.cols) {
                if (c+1 < grid.cols) {
                    grid.removeWallBetweenCells(r, c, r, c+1)
                }
                if (r+1 < grid.rows) {
                    grid.removeWallBetweenCells(r, c, r+1, c)
                }
            }
        }
        update()

        division(Pair(0, 0), Pair(grid.rows - 1, grid.cols - 1))

        complete()
    }

    private suspend fun division(topLeft: Pair<Int, Int>, bottomRight: Pair<Int, Int>) {
        if (topLeft.first == bottomRight.first && topLeft.second == bottomRight.second) return
        if (topLeft.first > bottomRight.first || topLeft.second > bottomRight.second) return

        if (bottomRight.second - topLeft.second > bottomRight.first - topLeft.first) {
            val row = Random.nextInt(bottomRight.first - topLeft.first + 1)
            val col = Random.nextInt(bottomRight.second - topLeft.second)

            for (c in topLeft.second until bottomRight.second + 1) {
                if (c == col) continue
                grid.cells[topLeft.first + row][c].hasBottomWall = true
                grid.cells[topLeft.first + row+1][c].hasTopWall = true
            }

            division(topLeft, Pair(topLeft.first + row, bottomRight.second))
            division(Pair(topLeft.first + row + 1, topLeft.second), bottomRight)

        } else {
            val row = Random.nextInt(bottomRight.first - topLeft.first)
            val col = Random.nextInt(bottomRight.second - topLeft.second + 1)

            for (r in topLeft.first until bottomRight.first + 1) {
                if (r == row) continue
                grid.cells[r][topLeft.second + col].hasRightWall = true
                grid.cells[r][topLeft.second + col+1].hasLeftWall = true
            }

            division(topLeft, Pair(bottomRight.first, topLeft.second + col))
            division(Pair(topLeft.first, topLeft.second + col + 1), bottomRight)
        }
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}