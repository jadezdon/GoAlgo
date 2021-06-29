package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmConfig
import com.zhouppei.goalgo.algorithms.AlgorithmView
import com.zhouppei.goalgo.extensions.clone
import com.zhouppei.goalgo.models.*
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

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
        targetVertexLabel = config.vertexCount - 1
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
        setCurrentStateColor(config.currentStateColor)
        setUnvisitedStateColor(config.unvisitedStateColor)
        setVisitedStateColor(config.visitedStateColor)
        setStartVertexColor(config.startVertexColor)
        setTargetVertexColor(config.targetVertexColor)
        setEdgeDefaultColor(config.edgeDefaultColor)
        setEdgeHighlightedColor(config.edgeHighlightedColor)
    }

    fun setIsTargetExist(isExist: Boolean) {
        hasTarget = isExist
        if (!isRunning) invalidate()
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    suspend fun highlightUndirectedEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (!graph.hasEdge(vertexLabel1, vertexLabel2)) return

        val prevState = graph.adjMatrix[vertexLabel1][vertexLabel2]?.state
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.state = EdgeState.HIGHLIGHT
        graph.adjMatrix[vertexLabel2][vertexLabel1]?.state = EdgeState.HIGHLIGHT
        update()
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.state = prevState ?: EdgeState.DEFAULT
        graph.adjMatrix[vertexLabel2][vertexLabel1]?.state = prevState ?: EdgeState.DEFAULT
    }

    suspend fun highlightDirectedEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (!graph.hasEdge(vertexLabel1, vertexLabel2)) return

        val prevState = graph.adjMatrix[vertexLabel1][vertexLabel2]?.state
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.state = EdgeState.HIGHLIGHT
        update()
        graph.adjMatrix[vertexLabel1][vertexLabel2]?.state = prevState ?: EdgeState.DEFAULT
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
        generateUndirectedEdges()
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

    private fun generateUndirectedEdges() {
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
            val possibleRemovableEdge = mutableListOf<Pair<Int, Int>>()
            for (j in 0 until connectedVertices.size) {
                if (graph.vertices[connectedVertices[j]].degree > 3) possibleRemovableEdge.add(Pair(i, connectedVertices[j]))
            }

            if (possibleRemovableEdge.size == 0) continue
            if (possibleRemovableEdge.size == 1) {
                graph.removeEdge(possibleRemovableEdge[0].first, possibleRemovableEdge[0].second)
                continue
            }
            var removeCount = Random.nextInt(1, possibleRemovableEdge.size)
            while (removeCount > 0 && graph.vertices[i].degree > 3) {
                val v = possibleRemovableEdge.random()
                possibleRemovableEdge.remove(v)
                graph.removeEdge(v.first, v.second)
                removeCount -= 1
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
            && min(linePoint1.second, linePoint2.second) < vertex.coordinate.second && vertex.coordinate.second < max(linePoint1.second, linePoint2.second)
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
            drawUndirectedEdges(it)
            drawVertices(it)
            drawCaption(it)
        }
    }

    private fun drawUndirectedEdges(canvas: Canvas) {
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
        for (i in 0 until graph.vertices.size) {
            if (graph.vertices[i].coordinate.first == 0f && graph.vertices[i].coordinate.first == 0f) continue

            when (graph.vertices[i].state) {
                VertexState.CURRENT -> canvas.drawRoundRect(graph.vertices[i].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, currentVertexPaint)
                VertexState.VISITED -> canvas.drawRoundRect(graph.vertices[i].boundingRectF, ROUND_RECT_RADIUS, ROUND_RECT_RADIUS, visitedVertexPaint)
                VertexState.UNVISITED -> canvas.drawRoundRect(
                    graph.vertices[i].boundingRectF,
                    ROUND_RECT_RADIUS,
                    ROUND_RECT_RADIUS,
                    unvisitedVertexPaint
                )
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
        edgeDonePaint.color = Color.parseColor(colorString)
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

    fun setTargetVertexColor(colorString: String) {
        targetVertexPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    override fun complete() {
        invalidate()
        super.complete()
    }

    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
        const val DEFAULT_CURRENT_STATE_COLOR = "#FEDD00"
        const val DEFAULT_UNVISITED_STATE_COLOR = "#C1CDCD"
        const val DEFAULT_VISITED_STATE_COLOR = "#2e8b57"
        const val DEFAULT_START_VERTEX_COLOR = "#7575a9"
        const val DEFAULT_TARGET_VERTEX_COLOR = "#191970"
        const val EDGE_DEFAULT_COLOR = "#C1CDCD"
        const val EDGE_HIGHLIGHTED_COLOR = "#ff033e"
        const val MAX_VERTEX_COUNT_IN_ROW = 15

        const val ROUND_RECT_RADIUS = 10f
    }
}

class GraphViewConfig : AlgorithmConfig() {
    var vertexCount = 30
    var startVertexColor = GraphView.DEFAULT_START_VERTEX_COLOR
    var targetVertexColor = GraphView.DEFAULT_TARGET_VERTEX_COLOR

    var currentStateColor = GraphView.DEFAULT_CURRENT_STATE_COLOR
    var visitedStateColor = GraphView.DEFAULT_VISITED_STATE_COLOR
    var unvisitedStateColor = GraphView.DEFAULT_UNVISITED_STATE_COLOR

    var edgeDefaultColor = GraphView.EDGE_DEFAULT_COLOR
    var edgeHighlightedColor = GraphView.EDGE_HIGHLIGHTED_COLOR

    var isLabelVisible = true
}