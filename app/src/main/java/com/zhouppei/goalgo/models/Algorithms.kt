package com.zhouppei.goalgo.models

class AlgorithmGroup(
    var name: String,
    var iconResourceId: Int,
    var algorithmNameList: MutableList<String>
) {

}

enum class GraphSearchAlgorithm(val str: String) {
    BFS("Breadth-first search (BFS)"),
    DFS("Depth-first search (DFS)"),
    ASTAR("A *");

    companion object {
        fun valuesToNameList(): MutableList<String> {
            val list = mutableListOf<String>()
            values().forEach { list.add(it.str) }
            return list
        }
    }
}

enum class SortingAlgorithm(val str: String) {
    QuickSort("Quick sort"),
    BubbleSort("Bubble sort"),
    InsertionSort("Insertion sort"),
    SelectionSort("Selection sort"),
    MergeSort("Merge sort"),
    ShellSort("Shell sort"),
    CocktailShakerSort("Cocktail shaker sort");

    companion object {
        fun valuesToNameList(): MutableList<String> {
            val list = mutableListOf<String>()
            values().forEach { list.add(it.str) }
            return list
        }
    }
}