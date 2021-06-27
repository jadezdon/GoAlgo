package com.zhouppei.goalgo.algorithms.graph

import android.content.Context
import android.util.AttributeSet
import com.zhouppei.goalgo.algorithms.AlgorithmView

abstract class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AlgorithmView(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun complete() {


        super.complete()
    }

    override fun new() {

    }



    companion object {
        private val LOG_TAG = GraphView::class.qualifiedName
    }
}

class GraphViewConfig {
}