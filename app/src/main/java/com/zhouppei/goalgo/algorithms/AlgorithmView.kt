package com.zhouppei.goalgo.algorithms

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
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

    abstract fun new()

    open fun complete() {
        onCompleteListener?.onComplete()
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