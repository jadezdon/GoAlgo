package com.zhouppei.goalgo.models

import android.graphics.RectF
import kotlin.math.sqrt

class Graph(var vertexCount: Int) {
    var vertices = MutableList(vertexCount) { Vertex(it) }
    var adjMatrix = MutableList(vertexCount) { MutableList<Edge?>(vertexCount) { null } }
    var isUndirected = true

    fun addEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (hasEdge(vertexLabel1, vertexLabel2)) return

        adjMatrix[vertexLabel1][vertexLabel2] = Edge(vertexLabel1, vertexLabel2)
        if (isUndirected) adjMatrix[vertexLabel2][vertexLabel1] = Edge(vertexLabel1, vertexLabel2)
        vertices[vertexLabel1].degree += 1
        vertices[vertexLabel2].degree += 1
    }

    fun removeEdge(vertexLabel1: Int, vertexLabel2: Int) {
        if (!hasEdge(vertexLabel1, vertexLabel2)) return

        adjMatrix[vertexLabel1][vertexLabel2] = null
        if (isUndirected) adjMatrix[vertexLabel2][vertexLabel1] = null
        vertices[vertexLabel1].degree -= 1
        vertices[vertexLabel2].degree -= 1
    }

    fun getVertexNeighbours(vertexLabel: Int): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until vertexCount) {
            if (i != vertexLabel && hasEdge(vertexLabel, i)) list.add(i)
        }
        return list
    }

    fun distanceBetweenVertices(vertexLabel1: Int, vertexLabel2: Int): Float {
        return sqrt(
            (vertices[vertexLabel2].coordinate.first - vertices[vertexLabel1].coordinate.first) * (vertices[vertexLabel2].coordinate.first - vertices[vertexLabel1].coordinate.first)
                    + (vertices[vertexLabel2].coordinate.second - vertices[vertexLabel1].coordinate.second) * (vertices[vertexLabel2].coordinate.second - vertices[vertexLabel1].coordinate.second)
        )
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
    var type = VertexType.UNVISITED
    var boundingRectF = RectF(0f, 0f, 0f, 0f)
    var degree = 0
}

class Edge(var vertexLabel1: Int, var vertexLabel2: Int) {
    var type = EdgeType.DEFAULT
}

enum class EdgeType {
    DEFAULT, HIGHLIGHT, DONE, PATH
}

enum class VertexType {
    VISITED, UNVISITED, CURRENT, PATH
}