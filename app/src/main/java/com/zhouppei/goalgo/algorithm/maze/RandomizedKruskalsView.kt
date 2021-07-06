package com.zhouppei.goalgo.algorithm.maze

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.CellType
import com.zhouppei.goalgo.view.GridView

class RandomizedKruskalsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        val walls = grid.getWallList()
        walls.shuffle()
        val cellSetList = grid.getCellSetList()

        for (w in walls) {
            var iidxContains = 0
            var jidxContains = 0
            for (i in 0 until cellSetList.size) {
                if (cellSetList[i].contains(w.first)) {
                    iidxContains = i
                }
                if (cellSetList[i].contains(w.second)) {
                    jidxContains = i
                }
            }
            if (iidxContains != jidxContains) {
                grid.removeWallBetweenCells(w.first.first, w.first.second, w.second.first, w.second.second)
                cellSetList[iidxContains] = cellSetList[iidxContains].union(cellSetList[jidxContains])
                cellSetList.removeAt(jidxContains)
                grid.cells[w.first.first][w.first.second].type = CellType.VISITED
                grid.cells[w.second.first][w.second.second].type = CellType.VISITED
                update()
            }
        }

        complete()
    }

    override fun sourceCode(): String {
        return  "1  Create a list of all walls, and create a set for each cell, each containing just that one cell.<br>" +
                "2  For each wall, in some random order:<br>" +
                "3      If the cells divided by this wall belong to distinct sets:<br>" +
                "4          Remove the current wall.<br>" +
                "5          Join the sets of the formerly divided cells."
    }

    override fun description(): String {
        return "This algorithm is a randomized version of Kruskal's algorithm."
    }
}