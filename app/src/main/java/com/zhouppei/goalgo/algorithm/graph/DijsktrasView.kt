package com.zhouppei.goalgo.algorithm.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.EdgeType
import com.zhouppei.goalgo.model.VertexType
import com.zhouppei.goalgo.view.GraphView

class DijsktrasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GraphView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        setCaption("Start: $startVertexLabel, Target: $targetVertexLabel")

        val labelsSet: MutableSet<Int> = mutableSetOf()
        val prev = MutableList(graph.vertexCount) { -1 }
        val dist = MutableList(graph.vertexCount) { Float.MAX_VALUE }

        labelsSet.addAll((0 until graph.vertexCount).toList())
        dist[startVertexLabel] = 0f
        graph.vertices[startVertexLabel].type = VertexType.VISITED
        update()

        var isTargetFound = false
        while (labelsSet.isNotEmpty() && !isTargetFound) {
            var v = labelsSet.first()
            var minDist = dist[v]
            for (u in labelsSet) {
                if (dist[u] < minDist) {
                    minDist = dist[u]
                    v = u
                }
            }
            labelsSet.remove(v)

            val vNeighbours = graph.getVertexNeighbours(v)
            for (u in vNeighbours) {
                if (!labelsSet.contains(u)) continue

                highlightEdge(v, u)
                val alternitive = dist[v] + graph.distanceBetweenVertices(u, v)
                if (alternitive < dist[u]) {
                    graph.vertices[u].type = VertexType.VISITED
                    graph.adjMatrix[v][u]?.type = EdgeType.DONE
                    if (graph.isUndirected) graph.adjMatrix[u][v]?.type = EdgeType.DONE
                    update()

                    dist[u] = alternitive
                    prev[u] = v

                    if (u == targetVertexLabel) {
                        isTargetFound = true
                        break
                    }
                }
            }
        }

        if (isTargetFound) {
            val path = mutableListOf<Int>()
            var current = targetVertexLabel
            path.add(current)
            while (prev[current] != -1) {
                path.add(prev[current])
                current = prev[current]
            }

            setCaption("$captionText, Path: ")
            for (i in path.size - 1 downTo 1) {
                graph.adjMatrix[path[i]][path[i-1]]?.type = EdgeType.PATH
                if (graph.isUndirected) graph.adjMatrix[path[i-1]][path[i]]?.type = EdgeType.PATH
                graph.vertices[path[i]].type = VertexType.PATH
                setCaption("$captionText ${path[i]} ")
                update()
            }
            graph.vertices[path[0]].type = VertexType.PATH
            setCaption("$captionText ${path[0]} ")
        } else {
            setCaption("Given start vertex ($startVertexLabel) and target vertex ($targetVertexLabel) are not connected")
        }

        complete()
    }

    override fun sourceCode(): String {
        return "<p>" +
                " 1  function Dijkstra(Graph, source):\n" +
                " 2\n" +
                " 3      create vertex set Q\n" +
                " 4\n" +
                " 5      for each vertex v in Graph:            \n" +
                " 6          dist[v] ← INFINITY                 \n" +
                " 7          prev[v] ← UNDEFINED                \n" +
                " 8          add v to Q                     \n" +
                " 9      dist[source] ← 0                       \n" +
                "10     \n" +
                "11      while Q is not empty:\n" +
                "12          u ← vertex in Q with min dist[u]   \n" +
                "13                                             \n" +
                "14          remove u from Q\n" +
                "15         \n" +
                "16          for each neighbor v of u:           // only v that are still in Q\n" +
                "17              alt ← dist[u] + length(u, v)\n" +
                "18              if alt < dist[v]:              \n" +
                "19                  dist[v] ← alt\n" +
                "20                  prev[v] ← u\n" +
                "21\n" +
                "22      return dist[], prev[]" +
                "</p>"
    }

    override fun description(): String {
        return "<p>" +
                "Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph, which may represent, " +
                "for example, road networks. It was conceived by computer scientist Edsger W. Dijkstra in 1956 and published three years later." +
                "</p>"
    }
}