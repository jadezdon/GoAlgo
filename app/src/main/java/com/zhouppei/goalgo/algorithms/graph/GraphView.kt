package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmView
import com.zhouppei.goalgo.extensions.clone
import com.zhouppei.goalgo.models.Graph
import com.zhouppei.goalgo.models.Vertex
import com.zhouppei.goalgo.models.VertexState
import com.zhouppei.goalgo.utils.Constants
import kotlin.math.min
import kotlin.random.Random

abstract class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    protected var graph: Graph
    private var config = GraphViewConfig()
    private var vertexRadius = 10f
    private var vertexPadding = 8
    private var possiblePositions = mutableListOf<Pair<Int, Int>>()
    private var topLeftCorner = Pair(0f, 0f)
    private var labelTextBounds = Rect()

    init {
        graph = Graph(config.vertexCount)
        for (x in 0 until MAX_VERTEX_COUNT_IN_ROW) {
            for (y in 0 until MAX_VERTEX_COUNT_IN_ROW) {
                possiblePositions.add(Pair(x, y))
            }
        }
    }

    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    private val currentVertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.currentStateColor)
    }

    private val visitedVertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.visitedStateColor)
    }

    private val unvisitedVertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unvisitedStateColor)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeParams()
    }

    private fun initializeParams() {
        val canvasSideLength = min(canvasWidth - paddingLeft - paddingRight, canvasHeight - paddingTop - paddingBottom)
        topLeftCorner = Pair((canvasWidth - canvasSideLength) / 2f, (canvasHeight - canvasSideLength) / 2f)

        vertexRadius = (canvasSideLength / MAX_VERTEX_COUNT_IN_ROW - 2 * vertexPadding) / 2f
        labelPaint.textSize = vertexRadius

        graph = Graph(config.vertexCount)

        generateVertices()
        generateEdges()
    }

    private fun generateVertices() {
        val possiblePositionsCopy = possiblePositions.clone()

        for (i in 0 until config.vertexCount) {
            val position = possiblePositionsCopy.random()
            graph.vertices[i].coordinate = Pair(
                topLeftCorner.first + (position.first * 2 + 1) * (vertexRadius + vertexPadding),
                topLeftCorner.second + (position.second * 2 + 1) * (vertexRadius + vertexPadding)
            )
            graph.vertices[i].boundingRectF = RectF(
                graph.vertices[i].coordinate.first - vertexRadius,
                graph.vertices[i].coordinate.second - vertexRadius,
                graph.vertices[i].coordinate.first + vertexRadius,
                graph.vertices[i].coordinate.second + vertexRadius
            )
        }
    }

    private fun generateEdges() {
        val verticesSortedByPosX = graph.vertices.clone().apply { sortBy { it.coordinate.first } }
        val verticesSortedByPosY = graph.vertices.clone().apply { sortBy { it.coordinate.second } }

        val possibleVertices = mutableListOf<Vertex>()
        for (i in 0 until config.vertexCount) {
            possibleVertices.clear()
            val currentX = graph.vertices[i].coordinate.first
            val nextX = verticesSortedByPosX.firstOrNull { it.coordinate.first > currentX }?.coordinate?.first ?: graph.vertices[i].coordinate.first
            val nextNextX = verticesSortedByPosX.firstOrNull { it.coordinate.first > nextX }?.coordinate?.first ?: graph.vertices[i].coordinate.first

            val currentY = graph.vertices[i].coordinate.second
            val nextY =
                verticesSortedByPosY.firstOrNull { it.coordinate.second > currentY }?.coordinate?.second ?: graph.vertices[i].coordinate.second
            val nextNextY =
                verticesSortedByPosY.firstOrNull { it.coordinate.second > nextY }?.coordinate?.second ?: graph.vertices[i].coordinate.second

            for (j in 0 until config.vertexCount) {
                if (i != j
                    && (graph.vertices[j].coordinate.first == currentX || graph.vertices[j].coordinate.first == nextX || graph.vertices[j].coordinate.first == nextNextX
                            || graph.vertices[j].coordinate.second == currentY || graph.vertices[j].coordinate.second == nextY || graph.vertices[j].coordinate.second == nextNextY)
                ) {
                    possibleVertices.add(graph.vertices[j])
                }
            }

            var degree = min(Random.nextInt(1, 4), possibleVertices.size)
            while (degree > 0 && possibleVertices.isNotEmpty()) {
                val index = Random.nextInt(0, possibleVertices.size)
                val connectedVertex = possibleVertices[index]
                possibleVertices.removeAt(index)

                if (graph.vertices[connectedVertex.label].degree < 4 && graph.vertices[i].degree < 4) {
                    graph.addEdge(connectedVertex.label, i)

                    degree -= 1
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            // draw edges
            for (i in 0 until graph.adjMatrix.size) {
                for (j in i + 1 until graph.adjMatrix[i].size) {
                    if (graph.hasEdge(i, j)) {
                        it.drawLine(
                            graph.vertices[i].boundingRectF.centerX(),
                            graph.vertices[i].boundingRectF.centerY(),
                            graph.vertices[j].boundingRectF.centerX(),
                            graph.vertices[j].boundingRectF.centerY(),
                            edgePaint
                        )
                    }
                }
            }

            // draw vertexes
            graph.vertices.forEach { vertex ->
                when (vertex.state) {
                    VertexState.CURRENT -> it.drawRoundRect(vertex.boundingRectF, 10f, 10f, currentVertexPaint)
                    VertexState.VISITED -> it.drawRoundRect(vertex.boundingRectF, 10f, 10f, visitedVertexPaint)
                    VertexState.UNVISITED -> it.drawRoundRect(vertex.boundingRectF, 10f, 10f, unvisitedVertexPaint)
                }

                if (config.isLabelVisible) {
                    labelPaint.getTextBounds(vertex.label.toString(), 0, vertex.label.toString().length, labelTextBounds)
                    it.drawText(
                        vertex.label.toString(),
                        vertex.boundingRectF.centerX(),
                        vertex.boundingRectF.centerY() + labelTextBounds.height() / 2,
                        labelPaint
                    )
                }
            }

            // draw caption
            if (isRunning && captionText.isNotBlank()) {
                if (captionTextLayout == null) {
                    it.drawText(
                        captionText,
                        paddingLeft + 20f,
                        captionTextPaint.textSize,
                        captionTextPaint
                    )
                } else {
                    it.save()
                    it.translate(paddingLeft.toFloat(), 0f)
                    captionTextLayout!!.draw(it)
                    it.restore()
                }
            }
        }
    }

    override fun complete() {


        super.complete()
    }

    override fun new() {
        initializeParams()

        super.new()
    }


    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
        const val DEFAULT_CURRENT_STATE_COLOR = "#FEDD00"
        const val DEFAULT_UNVISITED_STATE_COLOR = "#C1CDCD"
        const val DEFAULT_VISITED_STATE_COLOR = "#2e8b57"
        const val EDGE_DEFAULT_COLOR = "#ffffff"
        const val EDGE_HIGHLIGHTED_COLOR = "#FEDD00"
        const val MAX_VERTEX_COUNT_IN_ROW = 15
    }
}

class GraphViewConfig {
    var animationSpeed = Constants.ANIMATION_SPEED_NORMAL
    var currentStateColor = GraphView.DEFAULT_CURRENT_STATE_COLOR
    var visitedStateColor = GraphView.DEFAULT_VISITED_STATE_COLOR
    var unvisitedStateColor = GraphView.DEFAULT_UNVISITED_STATE_COLOR
    var vertexCount = 30

    var edgeDefaultColor = GraphView.EDGE_DEFAULT_COLOR
    var edgeHighlightedColor = GraphView.EDGE_HIGHLIGHTED_COLOR

    var isLabelVisible = true
}