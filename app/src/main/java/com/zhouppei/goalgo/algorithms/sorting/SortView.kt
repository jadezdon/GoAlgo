package com.zhouppei.goalgo.algorithms.sorting

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
import com.zhouppei.goalgo.models.Item
import com.zhouppei.goalgo.models.ItemState
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


abstract class SortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    protected var items: MutableList<Item>
    private var sortItemWidth = 25
    private var maxSortItemHeight = 100
    private var sortItemPadding = 8
    private var canvasWidth = 100
    private var canvasHeight = 100
    var speedInMiliSeconds = 500L

    var isShowValueText = true
    var isShowIndexText = false

    private var onCompleteListener: OnCompleteListener? = null

    init {
        items = generateItems(DEFAULT_ITEM_SIZE)
    }

    private val currentStatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FEDD00")
    }

    private val unsortedStatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#C1CDCD")
    }

    private val sortedStatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#1E90FF")
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    private fun generateItems(size: Int): MutableList<Item> = MutableList(size) { Item(Random.nextInt(1, 100)) }

    fun new(size: Int = DEFAULT_ITEM_SIZE) {
        items = generateItems(size)
        setItemsCoordinates()
        invalidate()
    }

    fun update() {
        invalidate()
    }

    fun compare(leftPosition: Int, rightPosition: Int) {
        items.forEachIndexed { index, item ->
            if (item.state == ItemState.CURRENT) item.state = ItemState.UNSORTED
            if (index == leftPosition || index == rightPosition) item.state = ItemState.CURRENT
        }
        invalidate()
    }

    fun swap(leftPosition: Int, rightPosition: Int) {
        val tempValue = items[leftPosition].value
        items[leftPosition].value = items[rightPosition].value
        items[rightPosition].value = tempValue
        val tempTop = items[leftPosition].coordinates.top
        items[leftPosition].coordinates.top = items[rightPosition].coordinates.top
        items[rightPosition].coordinates.top = tempTop

        invalidate()
    }

    fun complete() {
        items.forEach { it.state = ItemState.SORTED }
        invalidate()
        onCompleteListener?.onComplete()
    }

    fun setOnCompleteListener(listener: OnCompleteListener) {
        onCompleteListener = listener
    }

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

        setItemsCoordinates()
    }

    private fun setItemsCoordinates() {
        sortItemWidth = (((canvasWidth - paddingLeft - paddingRight) / items.size) - 2 * sortItemPadding)
        maxSortItemHeight = canvasHeight - 2 * sortItemPadding - paddingTop - paddingBottom

        items.forEachIndexed { index, item ->
            item.coordinates.left = paddingLeft + ((2 * index + 1) * sortItemPadding + index * sortItemWidth).toFloat()
            item.coordinates.right = item.coordinates.left + sortItemWidth
            item.coordinates.bottom = maxSortItemHeight + sortItemPadding.toFloat() + paddingTop
            item.coordinates.top = item.coordinates.bottom - (maxSortItemHeight * (item.value.toFloat() / 100))
        }
        textPaint.textSize = sortItemWidth * (2f / 3f)
        isShowValueText = (textPaint.textSize > 15)
        isShowIndexText = isShowIndexText && (textPaint.textSize > 15)
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
        canvas?.let {
            items.forEachIndexed { index, item ->
                when (item.state) {
                    ItemState.CURRENT -> it.drawRoundRect(item.coordinates, 10f, 10f, currentStatePaint)
                    ItemState.UNSORTED -> it.drawRoundRect(item.coordinates, 10f, 10f, unsortedStatePaint)
                    ItemState.SORTED -> it.drawRoundRect(item.coordinates, 10f, 10f, sortedStatePaint)
                }
                if (isShowValueText) {
                    it.drawText(
                        item.value.toString(),
                        item.coordinates.centerX(),
                        item.coordinates.top - sortItemPadding,
                        textPaint
                    )
                }

                if (isShowIndexText) {
                    it.drawText(
                        "[$index]",
                        item.coordinates.centerX(),
                        item.coordinates.bottom + textPaint.textSize + sortItemPadding,
                        textPaint
                    )
                }
            }
        }
    }

    abstract suspend fun sort()

    companion object {
        val LOG_TAG = SortView::class.qualifiedName
        const val DEFAULT_ITEM_SIZE = 20
    }
}

interface OnCompleteListener {
    fun onComplete()
}