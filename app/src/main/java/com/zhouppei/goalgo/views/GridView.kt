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
    private var cellSize = 10f

    private var maxCellCountInRow = 15
    private var maxCellCountInCol = 15

    protected var startCellLocation = Pair(0, 0)

    init {
        grid = Grid(config.cellCountInRow, config.cellCountInCol)
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(config.wallColor)
        strokeWidth = 3f
    }

    private val unvisitedCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unvisitedCellColor)
    }

    private val visitedCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.visitedCellColor)
    }

    private val currentCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.currentCellColor)
    }

    fun setGridViewConfig(gridViewConfig: GridViewConfig) {
        config = gridViewConfig
        wallPaint.color = Color.parseColor(config.wallColor)
        unvisitedCellPaint.color = Color.parseColor(config.unvisitedCellColor)
        visitedCellPaint.color = Color.parseColor(config.visitedCellColor)
        currentCellPaint.color = Color.parseColor(config.currentCellColor)
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
        cellSize = min(canvasWidth, canvasHeight) / DEFAULT_CELL_COUNT_IN_ROW.toFloat()
        maxCellCountInRow = (canvasHeight / cellSize).toInt()
        maxCellCountInCol = (canvasWidth / cellSize).toInt()

        grid = Grid(maxCellCountInRow, maxCellCountInCol)

        for (r in 0 until grid.rows) {
            for (c in 0 until grid.cols) {
                grid.cells[r][c].coordinate = Pair(paddingLeft + (2 * c + 1) * (cellSize / 2), paddingTop + (2 * r + 1) * (cellSize / 2))
                grid.cells[r][c].boundingRectF = RectF(
                    grid.cells[r][c].coordinate.first - cellSize / 2,
                    grid.cells[r][c].coordinate.second - cellSize / 2,
                    grid.cells[r][c].coordinate.first + cellSize / 2,
                    grid.cells[r][c].coordinate.second + cellSize / 2
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
        for (r in 0 until grid.rows) {
            for (c in 0 until grid.cols) {
                when (grid.cells[r][c].type) {
                    CellType.UNVISITED -> unvisitedCellPaint
                    CellType.CURRENT -> currentCellPaint
                    CellType.VISITED -> visitedCellPaint
                    else -> unvisitedCellPaint
                }.let { paint ->
                    canvas.drawRect(grid.cells[r][c].boundingRectF, paint)
                }

                // left wall
                if (grid.cells[r][c].hasLeftWall) {
                    canvas.drawLine(
                        grid.cells[r][c].boundingRectF.left,
                        grid.cells[r][c].boundingRectF.top,
                        grid.cells[r][c].boundingRectF.left,
                        grid.cells[r][c].boundingRectF.bottom,
                        wallPaint
                    )
                }

                // bottom wall
                if (grid.cells[r][c].hasBottomWall) {
                    canvas.drawLine(
                        grid.cells[r][c].boundingRectF.left,
                        grid.cells[r][c].boundingRectF.bottom,
                        grid.cells[r][c].boundingRectF.right,
                        grid.cells[r][c].boundingRectF.bottom,
                        wallPaint
                    )
                }

                // right wall
                if (grid.cells[r][c].hasRightWall) {
                    canvas.drawLine(
                        grid.cells[r][c].boundingRectF.right,
                        grid.cells[r][c].boundingRectF.top,
                        grid.cells[r][c].boundingRectF.right,
                        grid.cells[r][c].boundingRectF.bottom,
                        wallPaint
                    )
                }

                // top wall
                if (grid.cells[r][c].hasTopWall) {
                    canvas.drawLine(
                        grid.cells[r][c].boundingRectF.left,
                        grid.cells[r][c].boundingRectF.top,
                        grid.cells[r][c].boundingRectF.right,
                        grid.cells[r][c].boundingRectF.top,
                        wallPaint
                    )
                }
            }
        }
    }

    suspend fun markCellAsCurrent(location: Pair<Int, Int>) {
        val prevType = grid.cells[location.first][location.second].type
        grid.cells[location.first][location.second].type = CellType.CURRENT
        update()
        delay(config.animationSpeed)
        grid.cells[location.first][location.second].type = prevType
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
        private val LOG_TAG = GridView::class.qualifiedName
        const val DEFAULT_UNVISITED_CELL_COLOR = "#ffffff"
        const val DEFAULT_VISITED_CELL_COLOR = "#D59C58"
        const val DEFAULT_CURRENT_CELL_COLOR = "#BEEB84"
        const val DEFAULT_WALL_COLOR = "#36345A"
        const val DEFAULT_CELL_COUNT_IN_ROW = 20
        const val DEFAULT_CELL_COUNT_IN_COL = 20
    }
}

class GridViewConfig : AlgorithmConfig() {
    var cellCountInRow = GridView.DEFAULT_CELL_COUNT_IN_ROW
    var cellCountInCol = GridView.DEFAULT_CELL_COUNT_IN_COL
    var wallColor = GridView.DEFAULT_WALL_COLOR
    var unvisitedCellColor = GridView.DEFAULT_UNVISITED_CELL_COLOR
    var visitedCellColor = GridView.DEFAULT_VISITED_CELL_COLOR
    var currentCellColor = GridView.DEFAULT_CURRENT_CELL_COLOR
}