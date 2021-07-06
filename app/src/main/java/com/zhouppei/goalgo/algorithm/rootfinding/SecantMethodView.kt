package com.zhouppei.goalgo.algorithm.rootfinding

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.zhouppei.goalgo.view.FunctionView
import kotlin.math.abs

class SecantMethodView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FunctionView(context, attrs, defStyleAttr) {

    override suspend fun run() {
        super.run()

        var x0 = -0.6
        var x1 = 0.4

        setCaption("Initial values: x0 = $x0, x1 = $x1")
        update()

        var x2 = 0.0
        var i = 1
        do {
            val y0 = evaluateFunction(x0)
            val y1 = evaluateFunction(x1)
            val derivative = (x1 - x0) / (y1 - y0)
            x2 = x1 - y1 * derivative
            val y2 = evaluateFunction(x2)

            setCaption(
                "${i}. iteration: \n" +
                "x0 = ${"%.3f".format(x0)}, y0 = ${"%.3f".format(y0)} \n" +
                "x1 = ${"%.3f".format(x1)}, y1 = ${"%.3f".format(y1)} \n" +
                "x2 = ${"%.3f".format(x2)}, y2 = ${"%.3f".format(y2)}"
            )
            setCurrentLine(x0, y0, x1, y1)
            setHelperLine(x2, 0.0, x2, y2)
            setPoint(x2, 0.0)
            point = null
            currentLine = null

            x0 = x1
            x1 = x2
            i += 1
        } while (abs(y2) > 0.0 + 1e-13)

        setCaption("find root (x = ${"%.3f".format(x2)}) in ${i-1} iteration")
        setPoint(x2, 0.0)

        complete()
    }

    override fun sourceCode(): String {
        return "def secant_method(f, x0, x1, iterations):<br>" +
                "    \"\"\"Return the root calculated using the secant method.\"\"\"<br>" +
                "    for i in range(iterations):<br>" +
                "        x2 = x1 - f(x1) * (x1 - x0) / float(f(x1) - f(x0))<br>" +
                "        x0, x1 = x1, x2<br>" +
                "    return x2"
    }

    override fun description(): String {
        return "In numerical analysis, the secant method is a root-finding algorithm that uses a succession of roots of secant lines to " +
                "better approximate a root of a function f. The secant method can be thought of as a finite-difference approximation of Newton's method."
    }
}