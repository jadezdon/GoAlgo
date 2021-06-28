package com.zhouppei.goalgo.ui

interface GraphConfigListener {
    fun onChangeSpeed(speedInMiliSec: Long)
    fun onChangeLabelsVisibility(isVisible: Boolean)
    fun onChangeCurrentStateColor(colorString: String)
    fun onChangeUnvisitedStateColor(colorString: String)
    fun onChangeVisitedStateColor(colorString: String)
    fun onChangeStartVertexColor(colorString: String)
    fun onChangeTargetVertexColor(colorString: String)
    fun onChangeEdgeDefaultColor(colorString: String)
    fun onChangeEdgeHighlightedColor(colorString: String)
    fun toggleCompleteAnimation()
}