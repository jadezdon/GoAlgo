package com.zhouppei.goalgo.extensions

fun <T> List<T>.clone(): MutableList<T> {
    val original = this
    return mutableListOf<T>().apply { addAll(original) }
}