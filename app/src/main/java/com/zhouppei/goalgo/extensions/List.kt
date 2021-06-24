package com.zhouppei.goalgo.extensions

fun <T> List<T>.clone(): List<T> {
    return mutableListOf<T>().apply { addAll(this) }
}