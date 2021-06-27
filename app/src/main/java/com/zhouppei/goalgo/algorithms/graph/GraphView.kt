package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmView
import com.zhouppei.goalgo.models.Vertex
import com.zhouppei.goalgo.utils.Constants

abstract class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    protected var vertexes: MutableList<Vertex>
    protected var graph: MutableList<MutableList<Int>>
    private var config = GraphViewConfig()
    private var vertexRadius = 10

    init {
        vertexes = MutableList(config.vertexCount) { Vertex(it) }
        graph = MutableList(config.vertexCount) { MutableList(config.vertexCount) { 0 } }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generateGraph()
    }

    private fun generateGraph() {

    }

    override fun complete() {


        super.complete()
    }

    override fun new() {

    }


    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
    }
}

class GraphViewConfig {
    var sortingSpeed = Constants.SORTING_SPEED_NORMAL
    var vertexCount = 20
}