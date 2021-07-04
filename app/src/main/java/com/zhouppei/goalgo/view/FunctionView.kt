package com.zhouppei.goalgo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.zhouppei.goalgo.extension.clone
import com.zhouppei.goalgo.model.*
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

abstract class FunctionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    private var config = FunctionViewConfig()
    private var functionPath = Path()
    private var unitSize = 10f
    // -1 ... 0 ... 1
    private var xUnitCount = 20
    // -2 ... 0 ... 2
    private var yUnitCount = 40

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor(config.axisColor)
    }

    fun setConfig(functionViewConfig: FunctionViewConfig) {
        config = functionViewConfig

        if (!isRunning) invalidate()
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    // 8x^{5}-7x^{4}+x^{3}+6x^{2}-4x-1
    fun evaluateFunction(x: Float): Float {
        return 8f * x.pow(5) - 7f * x.pow(4) + x.pow(3) + 6f * x.pow(2) - 4f * x - 1
    }

    override fun initParams() {
        unitSize = min(canvasWidth / xUnitCount.toFloat(), canvasHeight / yUnitCount.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawXAxis(it)
            drawYAxis(it)
            drawFunction(it)
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

        val startX = (canvasWidth - xUnitCount * unitSize) / 2f
        for (i in 0 until xUnitCount) {
            canvas.drawLine(
                paddingLeft + startX + i * unitSize,
                (paddingTop + paddingBottom + canvasHeight) / 2f - 10,
                paddingLeft + startX + i * unitSize,
                (paddingTop + paddingBottom + canvasHeight) / 2f + 10,
                axisPaint
            )
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

        val startY = (canvasHeight - yUnitCount * unitSize) / 2f
        for (i in 0 until yUnitCount) {
            canvas.drawLine(
                (paddingLeft + paddingRight + canvasWidth) / 2f - 10,
                paddingTop + startY + i * unitSize,
                (paddingLeft + paddingRight + canvasWidth) / 2f + 10,
                paddingTop + startY + i * unitSize,
                axisPaint
            )
        }
    }

    private fun drawFunction(canvas: Canvas) {

    }

    override fun complete() {
        invalidate()
        super.complete()
    }

    companion object {
        private val LOG_TAG = FunctionView::class.qualifiedName
        const val DEFAULT_AXIS_COLOR = "#000000"
    }
}

class FunctionViewConfig : AlgorithmConfig() {
    var axisColor = FunctionView.DEFAULT_AXIS_COLOR
}