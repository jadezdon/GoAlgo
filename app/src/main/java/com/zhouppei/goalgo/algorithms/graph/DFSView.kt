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
        graph.vertices[v].state = VertexState.CURRENT
        update()

        for (i in 0 until graph.adjMatrix[v].size) {
            if (graph.hasEdge(v, i)) {
                highlightEdge(v, i)
                if (graph.vertices[i].state == VertexState.UNVISITED) {
                    graph.adjMatrix[v][i]?.state = EdgeState.DONE
                    update()
                    dfs(i)
                }
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