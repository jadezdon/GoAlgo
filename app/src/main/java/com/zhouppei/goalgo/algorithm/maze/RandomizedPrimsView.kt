package com.zhouppei.goalgo.algorithm.maze

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.CellType
import com.zhouppei.goalgo.view.GridView
import kotlin.random.Random

class RandomizedPrimsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        val wallList = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        val startLocation = Pair(Random.nextInt(grid.rows), Random.nextInt(grid.cols))
        grid.cells[startLocation.first][startLocation.second].type = CellType.VISITED
        wallList.addAll(grid.getCellWalls(startLocation))

        while (wallList.isNotEmpty()) {
            val wall = wallList.random()

            if ((grid.cells[wall.first.first][wall.first.second].type == CellType.VISITED).xor(grid.cells[wall.second.first][wall.second.second].type == CellType.VISITED)) {
                grid.removeWallBetweenCells(wall.first.first, wall.first.second, wall.second.first, wall.second.second)

                if (grid.cells[wall.first.first][wall.first.second].type != CellType.VISITED) {
                    markCellAsCurrent(wall.first)

                    grid.cells[wall.first.first][wall.first.second].type = CellType.VISITED
                    wallList.addAll(grid.getCellWalls(wall.first))
                }
                if (grid.cells[wall.second.first][wall.second.second].type != CellType.VISITED) {
                    markCellAsCurrent(wall.second)

                    grid.cells[wall.second.first][wall.second.second].type = CellType.VISITED
                    wallList.addAll(grid.getCellWalls(wall.second))
                }
                update()
            }

            wallList.remove(wall)
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