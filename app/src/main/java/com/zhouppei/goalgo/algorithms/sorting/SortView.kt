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
import com.zhouppei.goalgo.utils.Constants
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
    protected var config = SortViewConfiguration()

    protected var captionText = ""

    private var isSorting = false

    private var onCompleteListener: OnCompleteListener? = null

    init {
        items = generateItems(config.itemsSize)
    }

    private val currentItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.currentStateColorString)
    }

    private val unsortedItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unsortedStateColorString)
    }

    private val sortedItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.sortedStateColorString)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    private val captionTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 35f
    }

    private val pivotItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.pivotColorString)
        strokeWidth = 5f
    }

    private fun generateItems(size: Int): MutableList<Item> = MutableList(size) { Item(Random.nextInt(1, 100)) }

    fun new(size: Int = config.itemsSize) {
        items = generateItems(size)
        setItemsCoordinates()
        captionText = ""
        invalidate()
    }

    fun update() {
        invalidate()
    }

    fun setSortViewConfiguration(configuration: SortViewConfiguration) {
        config = configuration
        setCurrentStateColor(config.currentStateColorString)
        setUnsortedStateColor(config.unsortedStateColorString)
        setSortedStateColor(config.sortedStateColorString)
        setPivotColor(config.pivotColorString)
    }

    fun setSortingSpeed(speedInMiliSec: Long) {
        config.sortingSpeed = speedInMiliSec
    }

    fun setIsShowValues(isShow: Boolean) {
        config.isShowItemValues = isShow
        if (!isSorting) invalidate()
    }

    fun setIsShowIndexes(isShow: Boolean) {
        config.isShowItemIndexes = isShow
        if (!isSorting) invalidate()
    }

    fun setCurrentStateColor(colorString: String) {
        currentItemPaint.color = Color.parseColor(colorString)
    }

    fun setUnsortedStateColor(colorString: String) {
        unsortedItemPaint.color = Color.parseColor(colorString)
        if (!isSorting) invalidate()
    }

    fun setSortedStateColor(colorString: String) {
        sortedItemPaint.color = Color.parseColor(colorString)
        if (!isSorting) invalidate()
    }

    fun setPivotColor(colorString: String) {
        pivotItemPaint.color = Color.parseColor(colorString)
        if (!isSorting) invalidate()
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

        invalidate()
    }

    fun complete() {
        items.forEach { it.state = ItemState.SORTED }
        invalidate()
        isSorting = false
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
        maxSortItemHeight = canvasHeight - 2 * sortItemPadding - paddingTop - paddingBottom - captionTextPaint.textSize.toInt()

        items.forEachIndexed { index, item ->
            item.coordinates.left = paddingLeft + ((2 * index + 1) * sortItemPadding + index * sortItemWidth).toFloat()
            item.coordinates.right = item.coordinates.left + sortItemWidth
            item.coordinates.bottom = maxSortItemHeight + sortItemPadding.toFloat() + paddingTop
            item.coordinates.top = item.coordinates.bottom - (maxSortItemHeight * (item.value.toFloat() / 100))
        }
        textPaint.textSize = min(25f, sortItemWidth * (2f / 3f))
        config.isShowItemValues = (textPaint.textSize > 15)
        config.isShowItemIndexes = config.isShowItemIndexes && (textPaint.textSize > 15)
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
                item.coordinates.top = item.coordinates.bottom - (maxSortItemHeight * (item.value.toFloat() / 100))

                when (item.state) {
                    ItemState.CURRENT -> it.drawRoundRect(item.coordinates, 10f, 10f, currentItemPaint)
                    ItemState.UNSORTED -> it.drawRoundRect(item.coordinates, 10f, 10f, unsortedItemPaint)
                    ItemState.SORTED -> it.drawRoundRect(item.coordinates, 10f, 10f, sortedItemPaint)
                }

                if (item.isPivot) {
                    it.drawRoundRect(item.coordinates, 10f, 10f, pivotItemPaint)
                }

                if (config.isShowItemValues) {
                    it.drawText(
                        item.value.toString(),
                        item.coordinates.centerX(),
                        item.coordinates.top - sortItemPadding,
                        textPaint
                    )
                }

                if (config.isShowItemIndexes) {
                    it.drawText(
                        "[$index]",
                        item.coordinates.centerX(),
                        item.coordinates.bottom + textPaint.textSize + sortItemPadding,
                        textPaint
                    )
                }

                if (isSorting && captionText.isNotBlank()) {
                    it.drawText(
                        captionText,
                        paddingStart + 20f,
                        paddingTop + captionTextPaint.textSize,
                        captionTextPaint
                    )
                }
            }
        }
    }

    open suspend fun sort() {
        isSorting = true
    }

    companion object {
        val LOG_TAG = SortView::class.qualifiedName
        const val DEFAULT_CURRENT_STATE_COLOR = "#FEDD00"
        const val DEFAULT_UNSORTED_STATE_COLOR = "#C1CDCD"
        const val DEFAULT_SORTED_STATE_COLOR = "#2e8b57"
        const val DEFAULT_PIVOT_COLOR = "#ff4040"
    }
}

interface OnCompleteListener {
    fun onComplete()
}

class SortViewConfiguration {
    var currentStateColorString = SortView.DEFAULT_CURRENT_STATE_COLOR
    var unsortedStateColorString = SortView.DEFAULT_UNSORTED_STATE_COLOR
    var sortedStateColorString = SortView.DEFAULT_SORTED_STATE_COLOR
    var pivotColorString = SortView.DEFAULT_PIVOT_COLOR
    var sortingSpeed = Constants.SORTING_SPEED_NORMAL
    var isShowItemValues = true
    var isShowItemIndexes = false
    var itemsSize = 20
}