package com.zhouppei.goalgo.models

import android.graphics.RectF

class Grid(var rows: Int, var cols: Int) {
    var cells = MutableList(rows) { r ->
        MutableList(cols) { c ->
            Cell(r, c)
        }
    }

    fun getCellUnvisitedNeighbours(location: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        val row = location.first
        val col = location.second

        if (0 <= row - 1 && cells[row - 1][col].type == CellType.UNVISITED) {
            list.add(Pair(row - 1, col))
        }

        if (row + 1 < rows && cells[row + 1][col].type == CellType.UNVISITED) {
            list.add(Pair(row + 1, col))
        }

        if (0 <= col - 1 && cells[row][col - 1].type == CellType.UNVISITED) {
            list.add(Pair(row, col - 1))
        }

        if (col + 1 < cols && cells[row][col + 1].type == CellType.UNVISITED) {
            list.add(Pair(row, col + 1))
        }

        return list
    }

    fun removeWallBetweenCells(cell1Row: Int, cell1Col: Int, cell2Row: Int, cell2Col: Int) {
        if (cell1Row == cell2Row && cell1Col == cell2Col - 1) {
            cells[cell1Row][cell1Col].hasRightWall = false
            cells[cell2Row][cell2Col].hasLeftWall = false
        }

        if (cell1Row == cell2Row - 1 && cell1Col == cell2Col) {
            cells[cell1Row][cell1Col].hasBottomWall = false
            cells[cell2Row][cell2Col].hasTopWall = false
        }

        if (cell1Row == cell2Row + 1 && cell1Col == cell2Col) {
            cells[cell1Row][cell1Col].hasTopWall = false
            cells[cell2Row][cell2Col].hasBottomWall = false
        }

        if (cell1Row == cell2Row && cell1Col == cell2Col + 1) {
            cells[cell1Row][cell1Col].hasLeftWall = false
            cells[cell2Row][cell2Col].hasRightWall = false
        }
    }

    fun getWallList(): MutableList<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val list = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (r + 1 < rows && cells[r][c].hasBottomWall) {
                    list.add(Pair(Pair(r, c), Pair(r+1, c)))
                }

                if (c + 1 < cols && cells[r][c].hasRightWall) {
                    list.add(Pair(Pair(r, c), Pair(r, c+1)))
                }
            }
        }

        return list
    }

    fun getCellWalls(location: Pair<Int, Int>): MutableList<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val list = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        val row = location.first
        val col = location.second

        if (0 <= row - 1 && cells[row][col].hasTopWall) {
            list.add(Pair(location, Pair(row - 1, col)))
        }

        if (row + 1 < rows && cells[row][col].hasBottomWall) {
            list.add(Pair(location, Pair(row + 1, col)))
        }

        if (0 <= col - 1 && cells[row][col].hasLeftWall) {
            list.add(Pair(location, Pair(row, col - 1)))
        }

        if (col + 1 < cols && cells[row][col].hasRightWall) {
            list.add(Pair(location, Pair(row, col + 1)))
        }

        return list
    }

    fun getCellSetList(): MutableList<Set<Pair<Int, Int>>> {
        val cellSetList = mutableListOf<Set<Pair<Int, Int>>>()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val set = mutableSetOf<Pair<Int, Int>>()
                set.add(Pair(r, c))
                cellSetList.add(set)
            }
        }

        return cellSetList
    }
}

class Cell(
    var row: Int,
    var col: Int
) {
    var hasTopWall = true
    var hasLeftWall = true
    var hasRightWall = true
    var hasBottomWall = true
    var type = CellType.UNVISITED
    var coordinate = Pair(0f, 0f)
    var boundingRectF = RectF(0f, 0f, 0f, 0f)
}

enum class CellType {
    UNVISITED, CURRENT, VISITED
}