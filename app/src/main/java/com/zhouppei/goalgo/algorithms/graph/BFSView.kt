package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.EdgeState
import com.zhouppei.goalgo.models.VertexState
import com.zhouppei.goalgo.views.GraphView
import java.util.*

class BFSView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GraphView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        val labelsQueue: Queue<Int> = LinkedList()
        graph.vertices[startVertexLabel].state = VertexState.VISITED
        update()
        labelsQueue.add(startVertexLabel)

        while (labelsQueue.isNotEmpty()) {
            val v = labelsQueue.poll() ?: break

            if (v == targetVertexLabel) {
                break
            }

            val vNeighbours = graph.getVertexNeighbours(v)
            for (i in 0 until vNeighbours.size) {
                highlightEdge(v, vNeighbours[i])
                if (graph.vertices[vNeighbours[i]].state != VertexState.VISITED) {
                    labelsQueue.add(vNeighbours[i])
                    graph.vertices[vNeighbours[i]].state = VertexState.VISITED
                    graph.adjMatrix[v][vNeighbours[i]]?.state = EdgeState.DONE
                    if (graph.isUndirected) graph.adjMatrix[vNeighbours[i]][v]?.state = EdgeState.DONE
                    update()
                }
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