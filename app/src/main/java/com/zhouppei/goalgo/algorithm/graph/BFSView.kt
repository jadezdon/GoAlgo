package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.EdgeType
import com.zhouppei.goalgo.models.VertexType
import com.zhouppei.goalgo.views.GraphView
import java.util.*

class BFSView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GraphView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        setCaption("Start: $startVertexLabel, Target: $targetVertexLabel")

        val labelsQueue: Queue<Int> = LinkedList()
        val pred = MutableList(graph.vertexCount) { -1 }
        val dist = MutableList(graph.vertexCount) { Int.MAX_VALUE }

        dist[startVertexLabel] = 0
        graph.vertices[startVertexLabel].type = VertexType.VISITED
        update()
        labelsQueue.add(startVertexLabel)

        var isTargetFound = false
        while (labelsQueue.isNotEmpty() && !isTargetFound) {
            val v = labelsQueue.poll() ?: break

            val vNeighbours = graph.getVertexNeighbours(v)
            for (u in vNeighbours) {
                highlightEdge(v, u)
                if (graph.vertices[u].type != VertexType.VISITED) {
                    graph.vertices[u].type = VertexType.VISITED
                    graph.adjMatrix[v][u]?.type = EdgeType.DONE
                    if (graph.isUndirected) graph.adjMatrix[u][v]?.type = EdgeType.DONE
                    update()

                    dist[u] = dist[v] + 1
                    pred[u] = v

                    labelsQueue.add(u)
                    if (graph.vertices[u].label == targetVertexLabel) {
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
            while (pred[current] != -1) {
                path.add(pred[current])
                current = pred[current]
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
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}