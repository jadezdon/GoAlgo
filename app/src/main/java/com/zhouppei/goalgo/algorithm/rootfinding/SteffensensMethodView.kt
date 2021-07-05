package com.zhouppei.goalgo.algorithm.rootfinding

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.zhouppei.goalgo.view.FunctionView
import kotlin.math.abs

class SteffensensMethodView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FunctionView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var x0 = 0.3
        setCaption("Initial value: x0 = $x0")
        update()

        var x1 = 0.5
        var i = 1
        do {
            val y0 = evaluateFunction(x0)
            x1 = x0 - y0 / (evaluateFunction(x0 + y0) / y0 - 1.0)
            val y1 = evaluateFunction(x1)

            setCaption(
                "${i}. iteration: \n" +
                        "x0 = ${"%.3f".format(x0)}, y0 = ${"%.3f".format(y0)} \n" +
                        "x1 = ${"%.3f".format(x1)}, y1 = ${"%.3f".format(y1)}"
            )
            setCurrentLine(x0, y0, x1, y1)
            currentLine = null

            x0 = x1
            i += 1
        } while (abs(y1) > 0.0 + 1e-13)

        setCaption("find root (x = ${"%.3f".format(x1)}) in ${i-1} iteration")
        setPoint(x1, 0.0)

        complete()
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}