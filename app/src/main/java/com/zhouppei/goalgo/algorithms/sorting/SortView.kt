package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmConfig
import com.zhouppei.goalgo.algorithms.AlgorithmView
import com.zhouppei.goalgo.models.Item
import com.zhouppei.goalgo.models.ItemState
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.random.Random


abstract class SortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    protected var items: MutableList<Item>
    private var sortItemWidth = 25
    private var maxSortItemHeight = 100
    private var sortItemPadding = 8
    private var config = SortViewConfig()

    protected var sortingInterval: Pair<Int, Int>

    init {
        items = generateItems(config.itemsSize)
        sortingInterval = Pair(0, config.itemsSize - 1)
    }

    private val currentItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.currentStateColor)
    }

    private val unsortedItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unsortedStateColor)
    }

    private val unsortedItemNotInIntervalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unsortedStateColor)
        alpha = 128
    }

    private val sortedItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.sortedStateColor)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }


    private val pivotItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.pivotColor)
        strokeWidth = 5f
    }

    private fun generateItems(size: Int): MutableList<Item> = MutableList(size) { Item(Random.nextInt(1, ITEM_MAX_VALUE)) }

    override fun new() {
        items = generateItems(config.itemsSize)
        initializeParams()
        sortingInterval = Pair(0, config.itemsSize)
        super.new()
    }

    fun setSortViewConfiguration(sortViewConfig: SortViewConfig) {
        config = sortViewConfig
        setCurrentStateColor(config.currentStateColor)
        setUnsortedStateColor(config.unsortedStateColor)
        setSortedStateColor(config.sortedStateColor)
        setPivotColor(config.pivotColor)
    }

    fun setAnimationSpeed(speedInMiliSec: Long) {
        config.animationSpeed = speedInMiliSec
    }

    fun setValuesVisibility(isVisible: Boolean) {
        config.isItemValuesVisible = isVisible
        if (!isRunning) invalidate()
    }

    fun setIndexesVisibility(isVisible: Boolean) {
        config.isItemIndexesVisible = isVisible
        if (!isRunning) invalidate()
    }

    fun toggleCompleteAnimation() {
        config.isCompleteAnimationEnabled = !config.isCompleteAnimationEnabled
    }

    fun setCurrentStateColor(colorString: String) {
        currentItemPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setUnsortedStateColor(colorString: String) {
        unsortedItemPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setSortedStateColor(colorString: String) {
        sortedItemPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    fun setPivotColor(colorString: String) {
        pivotItemPaint.color = Color.parseColor(colorString)
        if (!isRunning) invalidate()
    }

    suspend fun update() {
        invalidate()
        delay(config.animationSpeed)
    }

    suspend fun highlight(indexList: List<Int>) {
        indexList.forEach {
            items[it].state = ItemState.CURRENT
        }
        invalidate()
        delay(config.animationSpeed)
        indexList.forEach {
            items[it].state = ItemState.UNSORTED
        }
    }

    suspend fun compare(leftPosition: Int, rightPosition: Int) {
        items[leftPosition].state = ItemState.CURRENT
        items[rightPosition].state = ItemState.CURRENT
        invalidate()

        delay(config.animationSpeed)

        items[leftPosition].state = ItemState.UNSORTED
        items[rightPosition].state = ItemState.UNSORTED
    }

    suspend fun swap(leftPosition: Int, rightPosition: Int) {
        val tempValue = items[leftPosition].value
        items[leftPosition].value = items[rightPosition].value
        items[rightPosition].value = tempValue

        items[leftPosition].state = ItemState.CURRENT
        items[rightPosition].state = ItemState.CURRENT

        invalidate()

        delay(config.animationSpeed)

        items[leftPosition].state = ItemState.UNSORTED
        items[rightPosition].state = ItemState.UNSORTED
    }

    suspend fun completeAnimation() {
        if (config.isCompleteAnimationEnabled) {
            sortingInterval = Pair(0, items.size - 1)

            for (i in 0 until items.size) {
                items[i].state = ItemState.SORTED
                invalidate()
                delay(30L)
            }
        }
    }

    override fun complete() {
        items.forEach { it.state = ItemState.SORTED }
        invalidate()

        super.complete()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeParams()
    }

    private fun initializeParams() {
        sortItemWidth = (((canvasWidth - paddingLeft - paddingRight) / items.size) - 2 * sortItemPadding)
        textPaint.textSize = min(25f, sortItemWidth * (2f / 3f))
        maxSortItemHeight = (canvasHeight - 2 * sortItemPadding - paddingTop - paddingBottom - captionTextPaint.textSize - textPaint.textSize).toInt()

        items.forEachIndexed { index, item ->
            item.coordinates.left = paddingLeft + ((2 * index + 1) * sortItemPadding + index * sortItemWidth).toFloat()
            item.coordinates.right = item.coordinates.left + sortItemWidth
            item.coordinates.bottom = maxSortItemHeight + sortItemPadding.toFloat() + paddingTop
            item.coordinates.top = item.coordinates.bottom - (maxSortItemHeight * (item.value.toFloat() / ITEM_MAX_VALUE))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawItems(it)
            drawCaption(it)
        }
    }

    private fun drawItems(canvas: Canvas) {
        items.forEachIndexed { index, item ->
            item.coordinates.top = item.coordinates.bottom - (maxSortItemHeight * (item.value.toFloat() / 100))

            if ((sortingInterval.first <= index && index <= sortingInterval.second) || !isRunning) {
                when (item.state) {
                    ItemState.CURRENT -> canvas.drawRoundRect(item.coordinates, 10f, 10f, currentItemPaint)
                    ItemState.UNSORTED -> canvas.drawRoundRect(item.coordinates, 10f, 10f, unsortedItemPaint)
                    ItemState.SORTED -> canvas.drawRoundRect(item.coordinates, 10f, 10f, sortedItemPaint)
                }
            } else {
                canvas.drawRoundRect(item.coordinates, 10f, 10f, unsortedItemNotInIntervalPaint)
            }

            if (item.isPivot) {
                canvas.drawRoundRect(item.coordinates, 10f, 10f, pivotItemPaint)
            }

            if (config.isItemValuesVisible) {
                canvas.drawText(
                    item.value.toString(),
                    item.coordinates.centerX(),
                    item.coordinates.top - sortItemPadding,
                    textPaint
                )
            }

            if (config.isItemIndexesVisible) {
                canvas.drawText(
                    "[$index]",
                    item.coordinates.centerX(),
                    item.coordinates.bottom + textPaint.textSize + sortItemPadding,
                    textPaint
                )
            }
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

    companion object {
        private val LOG_TAG = SortView::class.qualifiedName
        const val DEFAULT_CURRENT_STATE_COLOR = "#FEDD00"
        const val DEFAULT_UNSORTED_STATE_COLOR = "#C1CDCD"
        const val DEFAULT_SORTED_STATE_COLOR = "#2e8b57"
        const val DEFAULT_PIVOT_COLOR = "#ff4040"
        const val ITEM_MAX_VALUE = 100
    }
}

class SortViewConfig : AlgorithmConfig() {
    var itemsSize = 20

    var currentStateColor = SortView.DEFAULT_CURRENT_STATE_COLOR
    var unsortedStateColor = SortView.DEFAULT_UNSORTED_STATE_COLOR
    var sortedStateColor = SortView.DEFAULT_SORTED_STATE_COLOR
    var pivotColor = SortView.DEFAULT_PIVOT_COLOR

    var isItemValuesVisible = true
    var isItemIndexesVisible = false
}