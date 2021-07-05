package com.zhouppei.goalgo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow


abstract class FunctionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    private var config = FunctionViewConfig()
    private var functionPath = Path()
    private var origoLocation = Pair(0f, 0f)
    private var unitSize = 10f

    // -1 ... 0 ... 1
    private var xUnitCount = 20

    // -2 ... 0 ... 2
    private var yUnitCount = 40

    private var startX = 0f
    private var startY = 0f

    private var xAxisRange = Pair(-1.0, 1.0)
    private var yAxisRange = Pair(-0.5, 0.5)

    protected var currentLine: Pair<Pair<Float, Float>, Pair<Float, Float>>? = null
    protected var helperLine: Path? = null
    protected var point: Pair<Float, Float>? = null

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor(config.axisColor)
    }

    private val functionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.parseColor(config.functionColor)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.parseColor(config.lineColor)
    }

    private val dashedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
        color = Color.parseColor(config.lineColor)
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 4f
        color = Color.parseColor(config.lineColor)
    }

    private val xAxisLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 20f
        textAlign = Paint.Align.CENTER
    }

    private val yAxisLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 20f
        textAlign = Paint.Align.RIGHT
    }

    fun setConfig(functionViewConfig: FunctionViewConfig) {
        config = functionViewConfig
        axisPaint.color = Color.parseColor(config.axisColor)
        functionPaint.color = Color.parseColor(config.functionColor)
        linePaint.color = Color.parseColor(config.lineColor)
        pointPaint.color = Color.parseColor(config.lineColor)
        dashedLinePaint.color = Color.parseColor(config.lineColor)
        if (!isRunning) invalidate()
    }

    fun setHelperLine(x0: Double, y0: Double, x1: Double, y1: Double) {
        helperLine = Path().apply {
            moveTo(xValueToCoordinate(x0), yValueToCoordinate(y0))
            quadTo(xValueToCoordinate(x0), yValueToCoordinate(y0), xValueToCoordinate(x1), yValueToCoordinate(y1))
        }
    }

    suspend fun setCurrentLine(x0: Double, y0: Double, x1: Double, y1: Double) {
        currentLine = Pair(Pair(xValueToCoordinate(x0), yValueToCoordinate(y0)), Pair(xValueToCoordinate(x1), yValueToCoordinate(y1)))
        update()
    }

    suspend fun setPoint(x0: Double, y0: Double) {
        point = Pair(xValueToCoordinate(x0), yValueToCoordinate(y0))
        update()
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    fun evaluateFunction(x: Double): Double {
        // 8x^{5}-33x^{4}+23x^{3}+5x^{2}-4x
        // return 8.0 * x.pow(5) - 33.0 * x.pow(4) + 23 * x.pow(3) + 5.0 * x.pow(2) - 4.0 * x - 1

        // x^5 - 1.7x^4 - 1.51x^3 + 1.637x^2 + 0.393x - 0.054
        // return x.pow(5) - 1.7 * x.pow(4) - 1.51 * x.pow(3) + 1.637 * x.pow(2) + 0.393 * x - 0.054

        // 2x^{3}+x^{2}-x-0.2
        return 2 * x.pow(3) + x.pow(2) - x - 0.2

        // x^5 - 1.7x^4 - 1.51x^3 + 1.637x^2 + 0.393x - 0.054
//        return x.pow(5) - 1.7 * x.pow(4) - 1.51 * x.pow(3) + 1.637 * x.pow(2) + 0.393 * x - 0.054
    }

    private fun xValueToCoordinate(x: Double): Float {
        return (origoLocation.first + unitSize * x * 10).toFloat()
    }

    private fun yValueToCoordinate(y: Double): Float {
        return (origoLocation.second - unitSize * y * 10).toFloat()
    }

    override fun initParams() {
        currentLine = null
        point = null
        xUnitCount = ((xAxisRange.second - xAxisRange.first) * 10).toInt()
        yUnitCount = ((yAxisRange.second - yAxisRange.first) * 10).toInt()

        unitSize = min(canvasWidth / xUnitCount.toFloat(), canvasHeight / yUnitCount.toFloat())
        startX = paddingLeft + (canvasWidth - xUnitCount * unitSize) / 2f
        startY = paddingTop + (canvasHeight - yUnitCount * unitSize) / 2f

        origoLocation = Pair(
            startX + (xAxisRange.second - xAxisRange.first).toInt() * 5 * unitSize,
            startY + (yAxisRange.second - yAxisRange.first).toInt() * 5 * unitSize
        )

        var x = xAxisRange.first
        val step = 0.01
        functionPath.moveTo(xValueToCoordinate(x), yValueToCoordinate(evaluateFunction(x)))
        while (x <= xAxisRange.second) {
            val y1 = evaluateFunction(x)
            val y2 = evaluateFunction(x + step)
            functionPath.quadTo(
                xValueToCoordinate(x),
                yValueToCoordinate(y1),
                xValueToCoordinate(x + step),
                yValueToCoordinate(y2)
            )

            x += step
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawXAxis(it)
            drawYAxis(it)
            drawFunction(it)
            drawLine(it)
        }
    }

    private fun drawXAxis(canvas: Canvas) {
        canvas.drawLine(
            paddingLeft.toFloat(),
            (paddingTop + paddingBottom + canvasHeight) / 2f,
            paddingLeft + canvasWidth.toFloat(),
            (paddingTop + paddingBottom + canvasHeight) / 2f,
            axisPaint
        )

        for (i in 0 until xUnitCount + 1) {
            canvas.drawLine(
                startX + i * unitSize,
                (paddingTop + paddingBottom + canvasHeight) / 2f - 5,
                startX + i * unitSize,
                (paddingTop + paddingBottom + canvasHeight) / 2f + 5,
                axisPaint
            )


            if (config.isXAxisLabelVisible && "%.1f".format(xAxisRange.first + i * 0.1) != "0.0") {
                canvas.drawText(
                    "%.1f".format(xAxisRange.first + i * 0.1),
                    0,
                    "%.1f".format(xAxisRange.first + i * 0.1).length,
                    startX + i * unitSize,
                    (paddingTop + paddingBottom + canvasHeight) / 2f + 10 + xAxisLabelPaint.textSize,
                    xAxisLabelPaint
                )
            }
        }
    }

    private fun drawYAxis(canvas: Canvas) {
        canvas.drawLine(
            (paddingLeft + paddingRight + canvasWidth) / 2f,
            paddingTop.toFloat(),
            (paddingLeft + paddingRight + canvasWidth) / 2f,
            paddingTop + canvasHeight.toFloat(),
            axisPaint
        )

        for (i in 0 until yUnitCount + 1) {
            canvas.drawLine(
                (paddingLeft + paddingRight + canvasWidth) / 2f - 5,
                startY + i * unitSize,
                (paddingLeft + paddingRight + canvasWidth) / 2f + 5,
                startY + i * unitSize,
                axisPaint
            )

            if (config.isYAxisLabelVisible && "%.1f".format(yAxisRange.first + i * 0.1) != "0.0") {
                canvas.drawText(
                    "%.1f".format(yAxisRange.first + i * 0.1),
                    0,
                    "%.1f".format(yAxisRange.first + i * 0.1).length,
                    (paddingLeft + paddingRight + canvasWidth) / 2f - 10,
                    startY + i * unitSize + xAxisLabelPaint.textSize / 2f,
                    yAxisLabelPaint
                )
            }
        }
    }

    private fun drawLine(canvas: Canvas) {
        currentLine?.let {
            canvas.drawLine(it.first.first, it.first.second, it.second.first, it.second.second, linePaint)
            canvas.drawCircle(it.first.first, it.first.second, 10f, pointPaint)
            canvas.drawCircle(it.second.first, it.second.second, 10f, pointPaint)
        }
        helperLine?.let {
            canvas.drawPath(it, dashedLinePaint)
        }
        point?.let {
            canvas.drawCircle(it.first, it.second, 10f, pointPaint)
        }
    }

    private fun drawFunction(canvas: Canvas) {
        canvas.drawPath(functionPath, functionPaint)
        canvas.drawText("f(x) = 2x^3 + x^2 - x - 0.2", paddingLeft.toFloat(), paddingTop + canvasHeight.toFloat(), captionTextPaint)
    }

    override fun complete() {
        invalidate()
        super.complete()
    }

    companion object {
        private val LOG_TAG = FunctionView::class.qualifiedName
        const val DEFAULT_AXIS_COLOR = "#000000"
        const val DEFAULT_FUNCTION_COLOR = "#36345A"
        const val DEFAULT_LINE_COLOR = "#0707F0"
    }
}

class FunctionViewConfig : AlgorithmConfig() {
    var axisColor = FunctionView.DEFAULT_AXIS_COLOR
    var functionColor = FunctionView.DEFAULT_FUNCTION_COLOR
    var lineColor = FunctionView.DEFAULT_LINE_COLOR
    var isXAxisLabelVisible = true
    var isYAxisLabelVisible = true
}