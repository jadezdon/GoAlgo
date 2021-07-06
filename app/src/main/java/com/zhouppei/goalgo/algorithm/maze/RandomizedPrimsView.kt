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
        return "1  Start with a grid full of walls.<br>" +
                "2  Pick a cell, mark it as part of the maze. Add the walls of the cell to the wall list.<br>" +
                "3  While there are walls in the list:<br>" +
                "4      Pick a random wall from the list. If only one of the cells that the wall divides is visited, then:<br>" +
                "5          Make the wall a passage and mark the unvisited cell as part of the maze.<br>" +
                "6          Add the neighboring walls of the cell to the wall list.<br>" +
                "7      Remove the wall from the list."
    }

    override fun description(): String {
        return "This algorithm is a randomized version of Prim's algorithm."
    }
}