package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.models.EdgeState
import com.zhouppei.goalgo.models.VertexState
import java.util.*

class BFSView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GraphView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        val labelsQueue: Queue<Int> = LinkedList<Int>()
        graph.vertices[startVertexLabel].state = VertexState.CURRENT
        update()
        labelsQueue.add(startVertexLabel)

        while (labelsQueue.isNotEmpty()) {
            val v = labelsQueue.poll() ?: break
            graph.vertices[v].state = VertexState.VISITED
            update()

            if (v == targetVertexLabel) {
                break
            }
            for (i in 0 until graph.adjMatrix[v].size) {
                highlightEdge(v, i)
                if (graph.hasEdge(v, i) && graph.vertices[i].state == VertexState.UNVISITED) {
                    labelsQueue.add(i)
                    graph.vertices[i].state = VertexState.CURRENT
                    graph.adjMatrix[v][i]?.state = EdgeState.DONE
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