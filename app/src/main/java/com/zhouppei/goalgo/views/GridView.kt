package com.zhouppei.goalgo.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.zhouppei.goalgo.models.*
import kotlinx.coroutines.delay
import kotlin.math.min

abstract class GridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    protected var grid: Grid
    private var config = GridViewConfig()
    private var itemSize = 10f

    private var maxItemCountInRow = 15
    private var maxItemCountInCol = 15


    init {
        grid = Grid(config.itemCountInRow, config.itemCountInCol)
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.wallColor)
        strokeWidth = 2f
    }

    private val itemDefaultPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.itemDefaultColor)
    }

    fun setGridViewConfig(gridViewConfig: GridViewConfig) {
        config = gridViewConfig
        wallPaint.color = Color.parseColor(config.wallColor)
        itemDefaultPaint.color = Color.parseColor(config.itemDefaultColor)
        if (!isRunning) invalidate()
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeParams()
    }

    private fun initializeParams() {
        itemSize = min(canvasWidth, canvasHeight) / maxItemCountInRow.toFloat()
        maxItemCountInRow = (canvasHeight / itemSize).toInt()
        maxItemCountInCol = (canvasWidth / itemSize).toInt()

        grid = Grid(maxItemCountInRow, maxItemCountInCol)

        for (r in 0 until grid.row) {
            for (c in 0 until grid.col) {
                grid.items[r][c].coordinate = Pair(paddingLeft + itemSize / 2 + (2 * c + 1) * itemSize, paddingTop + itemSize / 2 + (2 * r + 1))
                grid.items[r][c].boundingRectF = RectF(
                    grid.items[r][c].coordinate.first - itemSize / 2,
                    grid.items[r][c].coordinate.second - itemSize / 2,
                    grid.items[r][c].coordinate.first + itemSize / 2,
                    grid.items[r][c].coordinate.second + itemSize / 2
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawGrid(it)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        for (r in 0 until grid.row) {
            for (c in 0 until grid.col) {
                canvas.drawRect(grid.items[r][c].boundingRectF, itemDefaultPaint)
            }
        }
    }

    override fun new() {
        initializeParams()
        super.new()
    }

    override fun complete() {
        invalidate()
        super.complete()
    }

    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
        const val DEFAULT_ITEM_COLOR = "#FEDD00"
        const val DEFAULT_UNVISITED_STATE_COLOR = "#C1CDCD"
        const val DEFAULT_VISITED_STATE_COLOR = "#4ae057"
        const val DEFAULT_START_VERTEX_COLOR = "#fe4600"
        const val DEFAULT_WALL_COLOR = "#fe46ef"
        const val DEFAULT_ITEM_COUNT_IN_ROW = 20
        const val DEFAULT_ITEM_COUNT_IN_COL = 20

        const val ROUND_RECT_RADIUS = 10f
    }
}

class GridViewConfig : AlgorithmConfig() {
    var itemCountInRow = GridView.DEFAULT_ITEM_COUNT_IN_ROW
    var itemCountInCol = GridView.DEFAULT_ITEM_COUNT_IN_COL
    var wallColor = GridView.DEFAULT_WALL_COLOR
    var itemDefaultColor = GridView.DEFAULT_ITEM_COLOR
}