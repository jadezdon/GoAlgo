package com.zhouppei.goalgo.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import com.zhouppei.goalgo.algorithms.graph.DFSView
import com.zhouppei.goalgo.extensions.clone
import com.zhouppei.goalgo.models.*
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

abstract class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    protected var graph: Graph
    protected var startVertexLabel = 0
    protected var targetVertexLabel = 0
    protected var hasTarget = true
    private var config = GraphViewConfig()
    private var vertexRadius = 10f
    private val vertexPadding = 8
    private var possiblePositions = mutableListOf<Pair<Int, Int>>()
    private var topLeftCorner = Pair(0f, 0f)
    private var labelTextBounds = Rect()

    private var maxVertexCountInRow = 15
    private var maxVertexCountInCol = 15

    init {
        graph = Graph(config.vertexCount)
        targetVertexLabel = config.vertexCount - 1
    }

    private val edgeDefaultPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.edgeDefaultColor)
        strokeWidth = 2f
    }

    private val edgeHighlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.edgeHighlightedColor)
        strokeWidth = 2f
    }

    private val edgePathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.pathColor)
        strokeWidth = 3f
    }

    private val edgeDonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.vertexVisitedColor)
        strokeWidth = 3f
    }

    private val vertexHighlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.edgeHighlightedColor)
        strokeWidth = 5f
    }

    private val vertexCurrentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.vertexCurrentColor)
    }

    private val vertexVisitedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.vertexVisitedColor)
    }

    private val vertexUnvisitedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.vertexUnvisitedColor)
    }

    private val vertexPathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.pathColor)
        strokeWidth = 5f
    }

    private val startVertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.startVertexColor)
        strokeWidth = 5f
    }

    private val targetVertexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.targetVertexColor)
        strokeWidth = 5f
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    fun setGraphViewConfig(graphViewConfig: GraphViewConfig) {
        config = graphViewConfig
        vertexVisitedPaint.color = Color.parseColor(config.vertexVisitedColor)
        vertexUnvisitedPaint.color = Color.parseColor(config.vertexUnvisitedColor)
        vertexCurrentPaint.color = Color.parseColor(config.vertexCurrentColor)
        vertexPathPaint.color = Color.parseColor(config.pathColor)
        startVertexPaint.color = Color.parseColor(config.startVertexColor)
        targetVertexPaint.color = Color.parseColor(config.targetVertexColor)
        edgeDefaultPaint.color = Color.parseColor(config.edgeDefaultColor)
        edgeHighlightedPaint.color = Color.parseColor(config.edgeHighlightedColor)
        edgePathPaint.color = Color.parseColor(config.pathColor)
        if (!isRunning) invalidate()
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    suspend fun highlightEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (!graph.hasEdge(vertexLabel1, vertexLabel2)) return

        val prevState = graph.adjMatrix[vertexLabel1][vertexLabel2]?.type
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.type = EdgeType.HIGHLIGHT
        if (graph.isUndirected) graph.adjMatrix[vertexLabel2][vertexLabel1]?.type = EdgeType.HIGHLIGHT
        update()
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.type = prevState ?: EdgeType.DEFAULT
        if (graph.isUndirected) graph.adjMatrix[vertexLabel2][vertexLabel1]?.type = prevState ?: EdgeType.DEFAULT
    }

    override fun initParams() {
        topLeftCorner = Pair(paddingLeft.toFloat(), paddingTop.toFloat())

        vertexRadius = (min(canvasWidth, canvasHeight) / maxVertexCountInRow - 2 * vertexPadding) / 2f
        maxVertexCountInRow = (canvasWidth / ((vertexRadius + vertexPadding) * 2)).toInt()
        maxVertexCountInCol = (canvasHeight / ((vertexRadius + vertexPadding) * 2)).toInt()

        possiblePositions.clear()
        for (x in 0 until maxVertexCountInRow) {
            for (y in 0 until maxVertexCountInCol) {
                possiblePositions.add(Pair(x, y))
            }
        }

        labelPaint.textSize = vertexRadius
        graph = Graph(config.vertexCount)

        generateVertices()
        generateEdges()
    }

    private fun generateVertices() {
        val possiblePositionsCopy = possiblePositions.clone()

        for (i in 0 until graph.vertexCount) {
            val position = possiblePositionsCopy.random()
            possiblePositionsCopy.remove(position)
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
        for (i in 0 until graph.vertexCount) {
            for (j in i + 1 until graph.vertexCount) {
                var hasAnyIntersect = false
                for (k in 0 until config.vertexCount) {
                    val intersect = isIntersect(graph.vertices[i].coordinate, graph.vertices[j].coordinate, graph.vertices[k])
                    if (i != k && j != k && intersect) {
                        hasAnyIntersect = true
                        break
                    }
                }
                if (!hasAnyIntersect) {
                    graph.addEdge(i, j)
                }
            }
        }

        for (i in 0 until graph.vertexCount) {
            if (graph.vertices[i].degree <= 3) continue

            val connectedVertices = graph.getVertexNeighbours(i)
            for (j in 0 until connectedVertices.size) {
                if (graph.vertices[i].degree <= 3) break
                if (graph.vertices[connectedVertices[j]].degree > 3
                    && graph.distanceBetweenVertices(connectedVertices[j], i) > canvasWidth * (2f / 3f)
                ) {
                    graph.removeEdge(i, connectedVertices[j])
                }
            }
        }
    }

    private fun isIntersect(linePoint1: Pair<Float, Float>, linePoint2: Pair<Float, Float>, vertex: Vertex): Boolean {
        // check if vertex is outside of smallest rectangle which is contains the linepoints
        /*
               (0,0)   1,0    2,0    3,0
                0,1    1,1    2,1    3,1
                0,2    1,2    2,2    3,2
                0,3    1,3   (2,3)   3,3
         */
        val isRight = vertex.coordinate.first > max(linePoint1.first, linePoint2.first)
        val isAbove = vertex.coordinate.second < min(linePoint1.second, linePoint2.second)
        val isLeft = vertex.coordinate.first < min(linePoint1.first, linePoint2.first)
        val isBelow = vertex.coordinate.second > max(linePoint1.second, linePoint2.second)

        if (isRight || isAbove || isLeft || isBelow) {
            return false
        }

        if (linePoint1.first == vertex.coordinate.first && linePoint2.first == vertex.coordinate.first
            && min(linePoint1.second, linePoint2.second) < vertex.coordinate.second && vertex.coordinate.second < max(
                linePoint1.second,
                linePoint2.second
            )
        ) {
            return true
        }

        if (linePoint1.second == vertex.coordinate.second && linePoint2.second == vertex.coordinate.second
            && min(linePoint1.first, linePoint2.first) < vertex.coordinate.first && vertex.coordinate.first < max(linePoint1.first, linePoint2.first)
        ) {
            return true
        }

        var y = getPointYOfLine(linePoint1, linePoint2, vertex.boundingRectF.left)
        if (vertex.boundingRectF.top - vertexPadding <= y && y <= vertex.boundingRectF.bottom + vertexPadding) return true

        y = getPointYOfLine(linePoint1, linePoint2, vertex.boundingRectF.right)
        if (vertex.boundingRectF.top - vertexPadding <= y && y <= vertex.boundingRectF.top + vertexPadding) return true

        y = getPointYOfLine(Pair(linePoint1.second, linePoint1.first), Pair(linePoint2.second, linePoint2.first), vertex.boundingRectF.top)
        if (vertex.boundingRectF.left - vertexPadding <= y && y <= vertex.boundingRectF.right + vertexPadding) return true

        y = getPointYOfLine(Pair(linePoint1.second, linePoint1.first), Pair(linePoint2.second, linePoint2.first), vertex.boundingRectF.bottom)
        if (vertex.boundingRectF.left - vertexPadding <= y && y <= vertex.boundingRectF.right + vertexPadding) return true

        return false
    }

    private fun getPointYOfLine(linePoint1: Pair<Float, Float>, linePoint2: Pair<Float, Float>, x: Float): Float {
        val slope = (linePoint2.second - linePoint1.second) / (linePoint2.first - linePoint1.first)
        return slope * (x - linePoint1.first) + linePoint1.second
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawEdges(it)
            drawVertices(it)
        }
    }

    private fun drawEdges(canvas: Canvas) {
        for (i in 0 until graph.adjMatrix.size) {
            for (j in i + 1 until graph.adjMatrix[i].size) {
                if (graph.hasEdge(i, j)) {
                    when (graph.adjMatrix[i][j]?.type) {
                        EdgeType.DEFAULT -> edgeDefaultPaint
                        EdgeType.HIGHLIGHT -> {
                            canvas.drawRoundRect(graph.vertices[i].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, vertexHighlightedPaint)
                            canvas.drawRoundRect(graph.vertices[j].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, vertexHighlightedPaint)
                            edgeHighlightedPaint
                        }
                        EdgeType.DONE -> edgeDonePaint
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

        for (i in 0 until graph.adjMatrix.size) {
            for (j in i + 1 until graph.adjMatrix[i].size) {
                if (graph.hasEdge(i, j) && graph.adjMatrix[i][j]?.type == EdgeType.PATH) {
                    canvas.drawLine(
                        graph.vertices[i].coordinate.first,
                        graph.vertices[i].coordinate.second,
                        graph.vertices[j].coordinate.first,
                        graph.vertices[j].coordinate.second,
                        edgePathPaint
                    )
                }
            }
        }
    }

    private fun drawVertices(canvas: Canvas) {
        for (i in 0 until graph.vertices.size) {
            if (graph.vertices[i].coordinate.first == 0f && graph.vertices[i].coordinate.first == 0f) continue

            when (graph.vertices[i].type) {
                VertexType.CURRENT -> vertexCurrentPaint
                VertexType.VISITED -> vertexVisitedPaint
                VertexType.UNVISITED -> vertexUnvisitedPaint
                VertexType.PATH -> vertexPathPaint
            }.let { paint ->
                canvas.drawRoundRect(graph.vertices[i].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, paint)
            }

            if (config.isLabelVisible) {
                labelPaint.getTextBounds(graph.vertices[i].label.toString(), 0, graph.vertices[i].label.toString().length, labelTextBounds)
                canvas.drawText(
                    graph.vertices[i].label.toString(),
                    graph.vertices[i].boundingRectF.centerX(),
                    graph.vertices[i].boundingRectF.centerY() + labelTextBounds.height() / 2,
                    labelPaint
                )
            }
        }

        canvas.drawRoundRect(graph.vertices[startVertexLabel].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, startVertexPaint)
        if (hasTarget) {
            canvas.drawRoundRect(graph.vertices[targetVertexLabel].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, targetVertexPaint)
        }
    }

    override fun complete() {
        invalidate()
        super.complete()
    }

    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
        const val DEFAULT_VERTEX_CURRENT_COLOR = "#FEDD00"
        const val DEFAULT_VERTEX_UNVISITED_COLOR = "#dbeaeb"
        const val DEFAULT_VERTEX_VISITED_COLOR = "#b4e8b3"
        const val DEFAULT_START_VERTEX_COLOR = "#fe4600"
        const val DEFAULT_TARGET_VERTEX_COLOR = "#fe46ef"
        const val EDGE_DEFAULT_COLOR = "#e8f0ec"
        const val EDGE_HIGHLIGHTED_COLOR = "#ff033e"
        const val DEFAULT_PATH_COLOR = "#fe4600"
        const val DEFAULT_VERTEX_COUNT = 50
        const val ROUND_RECT_RADIUS = 10f
    }
}

class GraphViewConfig : AlgorithmConfig() {
    var vertexCount = GraphView.DEFAULT_VERTEX_COUNT
    var startVertexColor = GraphView.DEFAULT_START_VERTEX_COLOR
    var targetVertexColor = GraphView.DEFAULT_TARGET_VERTEX_COLOR

    var vertexCurrentColor = GraphView.DEFAULT_VERTEX_CURRENT_COLOR
    var vertexVisitedColor = GraphView.DEFAULT_VERTEX_VISITED_COLOR
    var vertexUnvisitedColor = GraphView.DEFAULT_VERTEX_UNVISITED_COLOR

    var edgeDefaultColor = GraphView.EDGE_DEFAULT_COLOR
    var pathColor = GraphView.DEFAULT_PATH_COLOR
    var edgeHighlightedColor = GraphView.EDGE_HIGHLIGHTED_COLOR

    var isLabelVisible = true
}