package com.zhouppei.goalgo.views

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
import com.zhouppei.goalgo.utils.Constants
import kotlin.math.max
import kotlin.math.min

abstract class AlgorithmView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    protected var canvasWidth = 100
    protected var canvasHeight = 100
    protected var isRunning = false
    protected var captionText = ""
    protected var captionTextLayout: StaticLayout? = null

    private var onCompleteListener: OnCompleteListener? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d(LOG_TAG, "w = $w, h = $h")

        canvasWidth = w
        canvasHeight = h

        if (w <= 0 || h <= 0) {
            val size = getScreenSize(context as Activity)
            canvasWidth = max(width, size.first)
            canvasHeight = max(min(800, size.second), height)
        }

        canvasWidth = canvasWidth - paddingLeft - paddingRight
        canvasHeight = canvasHeight - paddingTop - paddingBottom
    }

    protected val captionTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 35f
    }

    private fun getScreenSize(activity: Activity): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            Pair(windowMetrics.bounds.width() - insets.left - insets.right, windowMetrics.bounds.height() - insets.top - insets.bottom)
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { drawCaption(it) }
    }

    private fun drawCaption(canvas: Canvas) {
        if (captionText.isNotBlank()) {
            if (captionTextLayout == null) {
                canvas.drawText(
                    captionText,
                    paddingLeft.toFloat(),
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

    open fun new() {
        isRunning = false
        captionText = ""
        invalidate()
    }

    open fun complete() {
        isRunning = false
        onCompleteListener?.onAlgorithmComplete()
    }

    fun setCaption(text: String) {
        captionText = text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            captionTextLayout = StaticLayout.Builder.obtain(text, 0, text.length, captionTextPaint, canvasWidth - paddingLeft - paddingRight).build()
        }
    }

    fun setOnCompleteListener(listener: OnCompleteListener) {
        onCompleteListener = listener
    }

    open suspend fun run() {
        isRunning = true
    }

    abstract fun sourceCode(): String
    abstract fun description(): String

    companion object {
        private val LOG_TAG = AlgorithmView::class.qualifiedName
    }
}

open class AlgorithmConfig {
    var animationSpeed = Constants.ANIMATION_SPEED_NORMAL
    var isCompleteAnimationEnabled = true
}

interface OnCompleteListener {
    fun onAlgorithmComplete()
}