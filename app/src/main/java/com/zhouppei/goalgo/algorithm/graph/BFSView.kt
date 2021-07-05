package com.zhouppei.goalgo.algorithm.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.model.EdgeType
import com.zhouppei.goalgo.model.VertexType
import com.zhouppei.goalgo.view.GraphView
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
                graph.adjMatrix[path[i]][path[i - 1]]?.type = EdgeType.PATH
                if (graph.isUndirected) graph.adjMatrix[path[i - 1]][path[i]]?.type = EdgeType.PATH
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
                " 1  procedure BFS(G, root) is\n" +
                " 2      let Q be a queue\n" +
                " 3      label root as explored\n" +
                " 4      Q.enqueue(root)\n" +
                " 5      while Q is not empty do\n" +
                " 6          v := Q.dequeue()\n" +
                " 7          if v is the goal then\n" +
                " 8              return v\n" +
                " 9          for all edges from v to w in G.adjacentEdges(v) do\n" +
                "10              if w is not labeled as explored then\n" +
                "11                  label w as explored\n" +
                "12                  Q.enqueue(w)" +
                "</p>"
    }

    override fun description(): String {
        return "<p>" +
                "Input: A graph G and a starting vertex root of G\n" +
                "Output: Goal state. The parent links trace the shortest path back to root\n" +
                "Breadth-first search (BFS) is an algorithm for searching a tree data structure for a node that satisfies a given property. " +
                "It starts at the tree root and explores all nodes at the present depth prior to moving on to the nodes at the next depth level. " +
                "Extra memory, usually a queue, is needed to keep track of the child nodes that were encountered but not yet explored." +
                "</p>"
    }
}