package com.zhouppei.goalgo.algorithm.rootfinding

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.zhouppei.goalgo.view.FunctionView

class SecantMethodView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FunctionView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var x0 = -0.9
        var x1 = 0.5

        var x2 = 0.0
        for (i in 0 until 7) {
            val y0 = evaluateFunction(x0)
            val y1 = evaluateFunction(x1)
            val derivative = (x1 - x0) / (y1 - y0)
            x2 = x1 - y1 * derivative

            setCaption(
                "${i + 1}. iteration: \n" +
                "x0 = ${"%.3f".format(x0)}, y0 = ${"%.3f".format(y0)} \n" +
                "x1 = ${"%.3f".format(x1)}, y1 = ${"%.3f".format(y1)} \n" +
                "x2 = ${"%.3f".format(x2)}"
            )
            setCurrentLine(x0, y0, x1, y1)
            setHelperLine(x2, 0.0, x2, evaluateFunction(x2))
            setPoint(x2, 0.0)
            point = null
            currentLine = null

            x0 = x1
            x1 = x2
        }

        setCaption("root = $x2")
        setPoint(x2, 0.0)

        complete()
    }

    override fun sourceCode(): String {
        TODO("Not yet implemented")
    }

    override fun description(): String {
        TODO("Not yet implemented")
    }
}