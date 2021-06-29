package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.EdgeState
import com.zhouppei.goalgo.models.VertexState
import com.zhouppei.goalgo.views.GraphView

class DFSView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GraphView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        dfs(startVertexLabel)

        complete()
    }

    private suspend fun dfs(v: Int) {
        graph.vertices[v].state = VertexState.CURRENT
        update()

        val vNeighbours = graph.getVertexNeighbours(v)
        for (i in 0 until vNeighbours.size) {
            highlightEdge(v, vNeighbours[i])
            if (graph.vertices[vNeighbours[i]].state == VertexState.UNVISITED) {
                graph.adjMatrix[v][vNeighbours[i]]?.state = EdgeState.DONE
                graph.adjMatrix[vNeighbours[i]][v]?.state = EdgeState.DONE
                update()
                dfs(vNeighbours[i])
            }
        }

        graph.vertices[v].state = VertexState.VISITED
        update()
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}