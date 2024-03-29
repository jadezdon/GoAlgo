package com.zhouppei.goalgo.algorithm.maze

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.view.GridView
import kotlin.random.Random

class RecursiveDivisionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override fun initParams() {
        super.initParams()
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
    }

    override suspend fun run() {
        super.run()

        division(Pair(0, 0), Pair(grid.rows - 1, grid.cols - 1))

        complete()
    }

    private suspend fun division(topLeft: Pair<Int, Int>, bottomRight: Pair<Int, Int>) {
        if (topLeft.first == bottomRight.first && topLeft.second == bottomRight.second) return
        if (topLeft.first > bottomRight.first || topLeft.second > bottomRight.second) return

        if (bottomRight.second - topLeft.second < bottomRight.first - topLeft.first) {
            val row = Random.nextInt(bottomRight.first - topLeft.first)
            val col = Random.nextInt(bottomRight.second - topLeft.second + 1)

            for (c in topLeft.second until bottomRight.second + 1) {
                if (c == topLeft.second + col) continue
                grid.cells[topLeft.first + row][c].hasBottomWall = true
                grid.cells[topLeft.first + row+1][c].hasTopWall = true
            }

            update()

            division(topLeft, Pair(topLeft.first + row, bottomRight.second))
            division(Pair(topLeft.first + row + 1, topLeft.second), bottomRight)

        } else {
            val row = Random.nextInt(bottomRight.first - topLeft.first + 1)
            val col = Random.nextInt(bottomRight.second - topLeft.second)

            for (r in topLeft.first until bottomRight.first + 1) {
                if (r == topLeft.first + row) continue
                grid.cells[r][topLeft.second + col].hasRightWall = true
                grid.cells[r][topLeft.second + col+1].hasLeftWall = true
            }

            update()

            division(topLeft, Pair(bottomRight.first, topLeft.second + col))
            division(Pair(topLeft.first, topLeft.second + col + 1), bottomRight)
        }
    }

    override fun sourceCode(): String {
        return ""
    }

    override fun description(): String {
        return "Mazes can be created with recursive division, an algorithm which works as follows: " +
                "Begin with the maze's space with no walls. Call this a chamber. " +
                "Divide the chamber with a randomly positioned wall (or multiple walls) where each wall contains a randomly positioned passage opening within it. " +
                "Then recursively repeat the process on the subchambers until all chambers are minimum sized. " +
                "This method results in mazes with long straight walls crossing their space, making it easier to see which areas to avoid."
    }
}