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
        return  " 1  function Dijkstra(Graph, source): <br>" +
                " 2 <br>" +
                " 3      create vertex set Q <br>" +
                " 4 <br>" +
                " 5      for each vertex v in Graph:             <br>" +
                " 6          dist[v] ← INFINITY                  <br>" +
                " 7          prev[v] ← UNDEFINED                 <br>" +
                " 8          add v to Q                      <br>" +
                " 9      dist[source] ← 0                        <br>" +
                "10      <br>" +
                "11      while Q is not empty: <br>" +
                "12          u ← vertex in Q with min dist[u]    <br>" +
                "13                                              <br>" +
                "14          remove u from Q <br>" +
                "15          <br>" +
                "16          for each neighbor v of u:           // only v that are still in Q <br>" +
                "17              alt ← dist[u] + length(u, v) <br>" +
                "18              if alt < dist[v]:               <br>" +
                "19                  dist[v] ← alt <br>" +
                "20                  prev[v] ← u <br>" +
                "21 <br>" +
                "22      return dist[], prev[]" 
    }

    override fun description(): String {
        return  "Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph, which may represent, " +
                "for example, road networks. It was conceived by computer scientist Edsger W. Dijkstra in 1956 and published three years later." 
    }
}