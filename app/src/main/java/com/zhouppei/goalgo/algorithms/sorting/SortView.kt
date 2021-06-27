package com.zhouppei.goalgo.algorithms.sorting

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmView
import com.zhouppei.goalgo.models.Item
import com.zhouppei.goalgo.models.ItemState
import com.zhouppei.goalgo.utils.Constants
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

    protected var captionText = ""
    private var captionTextLayout: StaticLayout? = null

    init {
        items = generateItems(config.itemsSize)
        sortingInterval = Pair(0, config.itemsSize-1)
    }

    private val currentItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.currentStateColorString)
    }

    private val unsortedItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unsortedStateColorString)
    }

    private val unsortedItemNotInIntervalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.unsortedStateColorString)
        alpha = 128
    }

    private val sortedItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor(config.sortedStateColorString)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 25f
        textAlign = Paint.Align.CENTER
    }

    private val captionTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 35f
    }

    private val pivotItemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor(config.pivotColorString)
        strokeWidth = 5f
    }

    private fun generateItems(size: Int): MutableList<Item> = MutableList(size) { Item(Random.nextInt(1, ITEM_MAX_VALUE)) }

    override fun new() {
        items = generateItems(config.itemsSize)
        setItemsCoordinates()
        captionText = ""
        sortingInterval = Pair(0, config.itemsSize)
        invalidate()
    }

    fun setCaption(text: String) {
        captionText = text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            captionTextLayout = StaticLayout.Builder.obtain(text, 0, text.length, captionTextPaint, canvasWidth - paddingLeft - paddingRight).build()
        }
    }

    fun setSortViewConfiguration(config: SortViewConfig) {
        this.config = config
        setCurrentStateColor(this.config.currentStateColorString)
        setUnsortedStateColor(this.config.unsortedStateColorString)
        setSortedStateColor(this.config.sortedStateColorString)
        setPivotColor(this.config.pivotColorString)
    }

    fun setSortingSpeed(speedInMiliSec: Long) {
        config.sortingSpeed = speedInMiliSec
    }

    fun setIsShowValues(isShow: Boolean) {
        config.isShowItemValues = isShow
        if (!isRunning) invalidate()
    }

    fun setIsShowIndexes(isShow: Boolean) {
        config.isShowItemIndexes = isShow
        if (!isRunning) invalidate()
    }

    fun toggleCompleteAnimation() {
        config.isCompleteAnimationEnabled = !config.isCompleteAnimationEnabled
    }

    fun setCurrentStateColor(colorString: String) {
        currentItemPaint.color = Color.parseColor(colorString)
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
        delay(config.sortingSpeed)
    }

    suspend fun highlight(indexList: List<Int>) {
        indexList.forEach {
            items[it].state = ItemState.CURRENT
        }
        invalidate()
        delay(config.sortingSpeed)
        indexList.forEach {
            items[it].state = ItemState.UNSORTED
        }
    }

    suspend fun compare(leftPosition: Int, rightPosition: Int) {
        items[leftPosition].state = ItemState.CURRENT
        items[rightPosition].state = ItemState.CURRENT
        invalidate()

        delay(config.sortingSpeed)

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

        delay(config.sortingSpeed)

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
        isRunning = false
        items.forEach { it.state = ItemState.SORTED }
        invalidate()

        super.complete()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        setItemsCoordinates()
    }

    private fun setItemsCoordinates() {
        sortItemWidth = (((canvasWidth - paddingLeft - paddingRight) / items.size) - 2 * sortItemPadding)
        textPaint.textSize = min(25f, sortItemWidth * (2f / 3f))
        maxSortItemHeight = canvasHeight - 2 * sortItemPadding - paddingTop - paddingBottom - captionTextPaint.textSize.toInt() - textPaint.textSize.toInt()

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
            items.forEachIndexed { index, item ->
                item.coordinates.top = item.coordinates.bottom - (maxSortItemHeight * (item.value.toFloat() / 100))

                if ((sortingInterval.first <= index && index <= sortingInterval.second) || !isRunning) {
                    when (item.state) {
                        ItemState.CURRENT -> it.drawRoundRect(item.coordinates, 10f, 10f, currentItemPaint)
                        ItemState.UNSORTED -> it.drawRoundRect(item.coordinates, 10f, 10f, unsortedItemPaint)
                        ItemState.SORTED -> it.drawRoundRect(item.coordinates, 10f, 10f, sortedItemPaint)
                    }
                } else {
                    it.drawRoundRect(item.coordinates, 10f, 10f, unsortedItemNotInIntervalPaint)
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
            }

            if (isRunning && captionText.isNotBlank()) {
                if (captionTextLayout == null) {
                    it.drawText(
                        captionText,
                        paddingStart + 20f,
                        captionTextPaint.textSize,
                        captionTextPaint
                    )
                } else {
                    it.save()
                    it.translate(paddingLeft.toFloat(), 0f)
                    captionTextLayout!!.draw(it)
                    it.restore()
                }
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

class SortViewConfig {
    var currentStateColorString = SortView.DEFAULT_CURRENT_STATE_COLOR
    var unsortedStateColorString = SortView.DEFAULT_UNSORTED_STATE_COLOR
    var sortedStateColorString = SortView.DEFAULT_SORTED_STATE_COLOR
    var pivotColorString = SortView.DEFAULT_PIVOT_COLOR
    var sortingSpeed = Constants.SORTING_SPEED_NORMAL
    var isShowItemValues = true
    var isShowItemIndexes = false
    var itemsSize = 20

    var isCompleteAnimationEnabled = true
}