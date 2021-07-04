package com.zhouppei.goalgo.extension

fun <T> List<T>.clone(): MutableList<T> {
    val original = this
    return mutableListOf<T>().apply { addAll(original) }
}