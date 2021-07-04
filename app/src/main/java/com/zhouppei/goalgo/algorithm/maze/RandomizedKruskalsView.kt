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
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}