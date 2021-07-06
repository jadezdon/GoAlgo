package com.zhouppei.goalgo.algorithm.maze

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.CellType
import com.zhouppei.goalgo.view.GridView
import java.util.*

class RandomizedDFSView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        val stack = Stack<Pair<Int, Int>>()
        stack.push(startCellLocation)
        grid.cells[startCellLocation.first][startCellLocation.second].type = CellType.VISITED

        while (stack.isNotEmpty()) {
            val current = stack.pop()
            markCellAsCurrent(current)

            val neighbours = grid.getCellUnvisitedNeighbours(current)
            if (neighbours.isNotEmpty()) {
                stack.push(current)
                val chosen = neighbours.random()
                grid.removeWallBetweenCells(current.first, current.second, chosen.first, chosen.second)
                stack.push(chosen)

                grid.cells[chosen.first][chosen.second].type = CellType.VISITED
            }
            grid.cells[current.first][current.second].type = CellType.VISITED
        }

        complete()
    }

    override fun sourceCode(): String {
        return  "1  Choose the initial cell, mark it as visited and push it to the stack<br>" +
                "2  While the stack is not empty<br>" +
                "3      Pop a cell from the stack and make it a current cell<br>" +
                "4      If the current cell has any neighbours which have not been visited<br>" +
                "5          Push the current cell to the stack<br>" +
                "6          Choose one of the unvisited neighbours<br>" +
                "7          Remove the wall between the current cell and the chosen cell<br>" +
                "8          Mark the chosen cell as visited and push it to the stack"
    }

    override fun description(): String {
        return  "This algorithm, also known as the \"recursive backtracker\" algorithm, is a randomized version of the depth-first search algorithm.<br>" +
                "Frequently implemented with a stack, this approach is one of the simplest ways to generate a maze using a computer. " +
                "Consider the space for a maze being a large grid of cells (like a large chess board), each cell starting with four walls."
    }
}