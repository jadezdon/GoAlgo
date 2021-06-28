package com.zhouppei.goalgo.models

import android.graphics.RectF

class Graph(var vertexCount: Int) {
    var vertices = MutableList(vertexCount) { Vertex(it) }
    var adjMatrix = MutableList(vertexCount) { MutableList(vertexCount) { 0 } }

    fun addEdge(vertexLabel1: Int, vertexLabel2: Int) {
        adjMatrix[vertexLabel1][vertexLabel2] = 1
        adjMatrix[vertexLabel2][vertexLabel1] = 1
        vertices[vertexLabel1].degree += 1
        vertices[vertexLabel2].degree += 1
    }

    fun hasEdge(vertexLabel1: Int, vertexLabel2: Int): Boolean {
        return (adjMatrix[vertexLabel1][vertexLabel2] == 1 || adjMatrix[vertexLabel2][vertexLabel1] == 1)
    }
}

class Vertex(var label: Int) {
    var coordinate = Pair(0f, 0f)
    var state = VertexState.UNVISITED
    var boundingRectF = RectF(0f, 0f, 0f, 0f)
    var degree = 0
}

enum class VertexState {
    VISITED, UNVISITED, CURRENT
}