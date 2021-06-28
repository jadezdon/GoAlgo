package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmConfig
import com.zhouppei.goalgo.algorithms.AlgorithmView
import com.zhouppei.goalgo.extensions.clone
import com.zhouppei.goalgo.models.*
import com.zhouppei.goalgo.utils.Constants
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

abstract class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    protected var graph: Graph
    protected var startVertixLabel = 0
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

    private val edgeDefaultPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.edgeDefaultColor)
        strokeWidth = 2f
    }

    private val edgeHighlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.edgeHighlightedColor)
        strokeWidth = 3f
    }

    private val edgeDonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.visitedStateColor)
        strokeWidth = 3f
    }

    private val vertexHighlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.edgeHighlightedColor)
        strokeWidth = 5f
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

    private val startVertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.startVertexColor)
        strokeWidth = 5f
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    suspend fun highlightEdge(vertexLabel1: Int, vertexLabel2: Int) {
        val prevState = graph.adjMatrix[vertexLabel1][vertexLabel2]?.state
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.state = EdgeState.HIGHLIGHT
        update()
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.state = if (prevState == EdgeState.DONE) EdgeState.DONE else EdgeState.DEFAULT
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
            drawEdges(it)
            drawVertices(it)
            drawCaption(it)
        }
    }

    private fun drawEdges(canvas: Canvas) {
        for (i in 0 until graph.adjMatrix.size) {
            for (j in i + 1 until graph.adjMatrix[i].size) {
                if (graph.hasEdge(i, j)) {
                    when (graph.adjMatrix[i][j]?.state) {
                        EdgeState.DEFAULT -> edgeDefaultPaint
                        EdgeState.HIGHLIGHT -> {
                            canvas.drawRoundRect(graph.vertices[i].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, vertexHighlightedPaint)
                            canvas.drawRoundRect(graph.vertices[j].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, vertexHighlightedPaint)
                            edgeHighlightedPaint
                        }
                        EdgeState.DONE -> edgeDonePaint
                        else -> edgeDefaultPaint
                    }.let { paint ->
                        canvas.drawLine(
                            graph.vertices[i].coordinate.first,
                            graph.vertices[i].coordinate.second,
                            graph.vertices[j].coordinate.first,
                            graph.vertices[j].coordinate.second,
                            paint
                        )
                    }
                }
            }
        }
    }

    private fun drawVertices(canvas: Canvas) {
        graph.vertices.forEach { vertex ->
            when (vertex.state) {
                VertexState.CURRENT -> canvas.drawRoundRect(vertex.boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, currentVertexPaint)
                VertexState.VISITED -> canvas.drawRoundRect(vertex.boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, visitedVertexPaint)
                VertexState.UNVISITED -> canvas.drawRoundRect(vertex.boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, unvisitedVertexPaint)
            }

            if (config.isLabelVisible) {
                labelPaint.getTextBounds(vertex.label.toString(), 0, vertex.label.toString().length, labelTextBounds)
                canvas.drawText(
                    vertex.label.toString(),
                    vertex.boundingRectF.centerX(),
                    vertex.boundingRectF.centerY() + labelTextBounds.height() / 2,
                    labelPaint
                )
            }
        }

        canvas.drawRoundRect(graph.vertices[startVertixLabel].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, startVertexPaint)
    }

    private fun drawCaption(canvas: Canvas) {
        if (isRunning && captionText.isNotBlank()) {
            if (captionTextLayout == null) {
                canvas.drawText(
                    captionText,
                    paddingLeft + 20f,
                    captionTextPaint.textSize,
                    captionTextPaint
                )
            } else {
                canvas.save()
                canvas.translate(paddingLeft.toFloat(), 0f)
                captionTextLayout!!.draw(canvas)
                canvas.restore()
            }
        }
    }

    override fun new() {
        initializeParams()
        super.new()
    }

    fun setAnimationSpeed(speedInMiliSec: Long) {
        config.animationSpeed = speedInMiliSec
    }

    fun setLabelsVisibility(isVisible: Boolean) {
        config.isLabelVisible = isVisible
        if (!isRunning) invalidate()
    }

    fun setCurrentStateColor(colorString: String) {
        currentVertexPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setUnvisitedStateColor(colorString: String) {
        unvisitedVertexPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setVisitedStateColor(colorString: String) {
        visitedVertexPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setStartVertexColor(colorString: String) {
        startVertexPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setEdgeDefaultColor(colorString: String) {
        edgeDefaultPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setEdgeHighlightedColor(colorString: String) {
        edgeHighlightedPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun toggleCompleteAnimation() {
        config.isCompleteAnimationEnabled = !config.isCompleteAnimationEnabled
    }

    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
        const val DEFAULT_CURRENT_STATE_COLOR = "#FEDD00"
        const val DEFAULT_UNVISITED_STATE_COLOR = "#C1CDCD"
        const val DEFAULT_VISITED_STATE_COLOR = "#2e8b57"
        const val DEFAULT_START_VERTEX_COLOR = "#191970"
        const val EDGE_DEFAULT_COLOR = "#C1CDCD"
        const val EDGE_HIGHLIGHTED_COLOR = "#ff033e"
        const val MAX_VERTEX_COUNT_IN_ROW = 15

        const val ROUND_RECT_RADIUS = 10f
    }
}

class GraphViewConfig : AlgorithmConfig() {
    var vertexCount = 30
    var startVertexColor = GraphView.DEFAULT_START_VERTEX_COLOR
    var currentStateColor = GraphView.DEFAULT_CURRENT_STATE_COLOR
    var visitedStateColor = GraphView.DEFAULT_VISITED_STATE_COLOR
    var unvisitedStateColor = GraphView.DEFAULT_UNVISITED_STATE_COLOR

    var edgeDefaultColor = GraphView.EDGE_DEFAULT_COLOR
    var edgeHighlightedColor = GraphView.EDGE_HIGHLIGHTED_COLOR

    var isLabelVisible = true
}