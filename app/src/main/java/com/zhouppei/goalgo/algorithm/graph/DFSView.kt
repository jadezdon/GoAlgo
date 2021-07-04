package com.zhouppei.goalgo.algorithm.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.EdgeType
import com.zhouppei.goalgo.model.VertexType
import com.zhouppei.goalgo.view.GraphView

class DFSView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GraphView(context, attrs, defStyleAttr) {

    override fun initParams() {
        super.initParams()
        hasTarget = false
    }

    override suspend fun run() {
        super.run()

        dfs(startVertexLabel)

        complete()
    }

    private suspend fun dfs(v: Int) {
        graph.vertices[v].type = VertexType.CURRENT
        update()

        val vNeighbours = graph.getVertexNeighbours(v)
        for (u in vNeighbours) {
            highlightEdge(v, u)
            if (graph.vertices[u].type == VertexType.UNVISITED) {
                graph.adjMatrix[v][u]?.type = EdgeType.DONE
                graph.adjMatrix[u][v]?.type = EdgeType.DONE
                update()
                dfs(u)
            }
        }

        graph.vertices[v].type = VertexType.VISITED
        update()
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}