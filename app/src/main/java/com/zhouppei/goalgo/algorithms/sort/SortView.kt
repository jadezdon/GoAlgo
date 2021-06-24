package com.zhouppei.goalgo.algorithms.sort

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.zhouppei.goalgo.models.*
import java.util.*
import kotlin.concurrent.timerTask

abstract class SortView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val PADDING = 10
    }

    protected val items = mutableListOf<Item>()
    private val commands: Queue<Command> = LinkedList()

    fun setItemValues(values: MutableList<Item>) {
        items.clear()
        items.addAll(values)
    }

    fun addCompareCommand(leftPosition: Int, rightPosition: Int) {
        commands.add(Command(SortCommandType.COMPARE, listOf(leftPosition, rightPosition)))
    }

    private fun compare(command: Command) {
        val leftPosition = command.args[0] as Int
        val rightPosition = command.args[1] as Int

        items.forEachIndexed { _, item ->
            if (item.state == ItemState.CURRENT) item.state = ItemState.UNSORTED
        }
        items[leftPosition].state = ItemState.CURRENT
        items[rightPosition].state = ItemState.CURRENT

        Timer().schedule(timerTask {
            animateCommand()
        }, 1000)
    }

    fun addSwapCommand(leftPosition: Int, rightPosition: Int) {
        commands.add(Command(SortCommandType.SWAP, listOf(leftPosition, rightPosition)))
    }

    private fun swap(command: Command) {
        val leftPosition = command.args[0] as Int
        val rightPosition = command.args[1] as Int


        Timer().schedule(timerTask {
            animateCommand()
        }, 1000)
    }

    private fun complete() {

    }

    fun addCompleteCommand() {
        commands.add(Command(SortCommandType.COMPLETE, listOf()))
        animateCommand()
    }

    private fun animateCommand() {
        if (commands.isEmpty()) return

        val command = commands.remove()
        when (command.type) {
            SortCommandType.SWAP -> swap(command)
            SortCommandType.COMPARE -> compare(command)
            SortCommandType.COMPLETE -> complete()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        items.forEachIndexed { index, item ->

        }
    }

    abstract fun sort()
}