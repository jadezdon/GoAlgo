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
        return "<p>" +
                "1 procedure DFS(G, v) is\n" +
                "2     label v as discovered\n" +
                "3     for all directed edges from v to w that are in G.adjacentEdges(v) do\n" +
                "4         if vertex w is not labeled as discovered then\n" +
                "5             recursively call DFS(G, w)" +
                "</p>"
    }

    override fun description(): String {
        return "<p>" +
                "Input: A graph G and a vertex v of G\n" +
                "Output: All vertices reachable from v labeled as discovered\n" +
                "Depth-first search (DFS) is an algorithm for traversing or searching tree or graph data structures. " +
                "The algorithm starts at the root node (selecting some arbitrary node as the root node in the case of a graph) and explores as far as " +
                "possible along each branch before backtracking." +
                "</p>"
    }
}