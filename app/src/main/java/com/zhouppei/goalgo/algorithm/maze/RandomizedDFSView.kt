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
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}