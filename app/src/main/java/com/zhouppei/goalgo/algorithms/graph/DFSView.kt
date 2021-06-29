package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.EdgeState
import com.zhouppei.goalgo.models.VertexState

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
        graph.vertices[v].state = VertexState.VISITED
        update()

        val vNeighbours = graph.getVertexNeighbours(v)
        for (i in 0 until vNeighbours.size) {
            highlightUndirectedEdge(v, vNeighbours[i])
            if (graph.vertices[vNeighbours[i]].state != VertexState.VISITED) {
                graph.adjMatrix[v][vNeighbours[i]]?.state = EdgeState.DONE
                graph.adjMatrix[vNeighbours[i]][v]?.state = EdgeState.DONE
                update()
                dfs(vNeighbours[i])
            }
        }
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}