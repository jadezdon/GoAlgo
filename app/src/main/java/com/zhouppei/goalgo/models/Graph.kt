package com.zhouppei.goalgo.models

import android.graphics.RectF

class Graph(var vertexCount: Int) {
    var vertices = MutableList(vertexCount) { Vertex(it) }
    var adjMatrix = MutableList(vertexCount) { MutableList<Edge?>(vertexCount) { null } }
    var isUndirected = true

    fun addEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (hasEdge(vertexLabel1, vertexLabel2)) return

        if (isUndirected) {
            adjMatrix[vertexLabel1][vertexLabel2] = Edge(vertexLabel1, vertexLabel2)
            adjMatrix[vertexLabel2][vertexLabel1] = Edge(vertexLabel1, vertexLabel2)
            vertices[vertexLabel1].degree += 1
            vertices[vertexLabel2].degree += 1
        } else {
            adjMatrix[vertexLabel1][vertexLabel2] = Edge(vertexLabel1, vertexLabel2)
            vertices[vertexLabel1].degree += 1
            vertices[vertexLabel2].degree += 1
        }
    }

    fun removeEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (!hasEdge(vertexLabel1, vertexLabel2)) return

        if (isUndirected) {
            adjMatrix[vertexLabel1][vertexLabel2] = null
            adjMatrix[vertexLabel2][vertexLabel1] = null
            vertices[vertexLabel1].degree -= 1
            vertices[vertexLabel2].degree -= 1
        } else {
            adjMatrix[vertexLabel1][vertexLabel2] = null
            vertices[vertexLabel1].degree -= 1
            vertices[vertexLabel2].degree -= 1
        }
    }

    fun getVertexNeighbours(vertexLabel: Int): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until vertexCount) {
            if (i != vertexLabel && hasEdge(vertexLabel, i)) list.add(i)
        }
        return list
    }

    fun hasEdge(vertexLabel1: Int, vertexLabel2: Int): Boolean {
        return if (isUndirected) {
            (adjMatrix[vertexLabel1][vertexLabel2] != null || adjMatrix[vertexLabel2][vertexLabel1] != null)
        } else {
            adjMatrix[vertexLabel1][vertexLabel2] != null
        }
    }
}

class Vertex(var label: Int) {
    var coordinate = Pair(0f, 0f)
    var state = VertexState.UNVISITED
    var boundingRectF = RectF(0f, 0f, 0f, 0f)
    var degree = 0
}

class Edge(var vertexLabel1: Int, var vertexLabel2: Int) {
    var state = EdgeState.DEFAULT
}

enum class EdgeState {
    DEFAULT, HIGHLIGHT, DONE
}

enum class VertexState {
    VISITED, UNVISITED, CURRENT
}