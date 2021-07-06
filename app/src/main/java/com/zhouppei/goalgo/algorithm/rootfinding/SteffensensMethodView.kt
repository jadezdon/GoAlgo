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
        return "def g(f, x: float, fx: float):<br>" +
                "    \"\"\"First-order divided difference function.<br>" +
                "<br>" +
                "    Arguments:<br>" +
                "        f(callable): Function input to g<br>" +
                "        x(float): Point at which to evaluate g<br>" +
                "        fx(float): Function f evaluated at x <br>" +
                "    \"\"\"<br>" +
                "    return lambda x: f(x + fx) / fx - 1<br>" +
                "<br>" +
                "def steff(f, x: float):<br>" +
                "    \"\"\"Steffenson algorithm for finding roots.<br>" +
                "<br>" +
                "    This recursive generator yields the x_n+1 value first then, when the generator iterates,<br>" +
                "    it yields x_n + 2 from the next level of recursion.<br>" +
                "<br>" +
                "    Arguments:<br>" +
                "        f(callable): Function whose root we are searching for<br>" +
                "        x(float): Starting value upon first call, each level n that the function recurses x is x_n<br>" +
                "    \"\"\"<br>" +
                "    fx = f(x)<br>" +
                "    gx = g(f,x, fx)(x)<br>" +
                "    if gx != 0:<br>" +
                "        yield x - fx / gx  # First give x_n + 1<br>" +
                "        yield from steff(f, x - fx / gx)  # Then give new iterator"
    }

    override fun description(): String {
        return "In numerical analysis, Steffensen's method is a root-finding technique named after Johan Frederik Steffensen which is similar " +
                "to Newton's method. Steffensen's method also achieves quadratic convergence, but without using derivatives as Newton's method does."
    }
}