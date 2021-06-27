package com.zhouppei.goalgo.models

class Vertex(
    var label: Int
) {
    var positionX = 0
    var positionY = 0
    var state = VertexState.UNVISITED
}

enum class VertexState {
    VISITED, UNVISITED, CURRENT
}