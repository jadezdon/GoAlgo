package com.zhouppei.goalgo.algorithm.rootfinding

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.zhouppei.goalgo.view.FunctionView
import kotlin.math.abs

class InverseInterpolationMethodView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FunctionView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var x0 = -0.6
        var x1 = -0.1
        var x2 = 0.3

        setCaption("Initial values: x0 = $x0, x1 = $x1, x2 = $x2")
        update()

        var x3 = 0.0
        var i = 1
        do {
            val y0 = evaluateFunction(x0)
            val y1 = evaluateFunction(x1)
            val y2 = evaluateFunction(x2)
            x3 = x0 * (y1 * y2) / ((y0 - y1) * (y0 - y2)) + x1 * (y0 * y2) / ((y1 - y0) * (y1 - y2)) + x2 * (y0 * y1) / ((y2 - y0) * (y2 - y1))
            val y3 = evaluateFunction(x3)

            setCaption(
                "${i}. iteration: \n" +
                "x0 = ${"%.3f".format(x0)}, y0 = ${"%.3f".format(y0)} \n" +
                "x1 = ${"%.3f".format(x1)}, y1 = ${"%.3f".format(y1)} \n" +
                "x2 = ${"%.3f".format(x2)}, y2 = ${"%.3f".format(y2)} \n" +
                "x3 = ${"%.3f".format(x3)}, y3 = ${"%.3f".format(y3)}"
            )
            setCurrentLine(x0, y0, x1, y1)
            setPoint(x3, y3)
            point = null
            currentLine = null

            x0 = x1
            x1 = x2
            x2 = x3
            i += 1
        } while (abs(y3) > 0.0 + 1e-13)

        setCaption("find root (x = ${"%.3f".format(x2)}) in ${i-1} iteration")
        setPoint(x2, 0.0)

        complete()
    }

    override fun sourceCode(): String {
        return ""
    }

    override fun description(): String {
        return "In numerical analysis, inverse quadratic interpolation is a root-finding algorithm, meaning that it is an algorithm for solving " +
                "equations of the form f(x) = 0. The idea is to use quadratic interpolation to approximate the inverse of f. " +
                "This algorithm is rarely used on its own, but it is important because it forms part of the popular Brent's method."
    }
}