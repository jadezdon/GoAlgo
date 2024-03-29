package com.zhouppei.goalgo.model

class AlgorithmGroup(
    var name: String,
    var iconResourceId: Int,
    var algorithmNameList: MutableList<String>
) {
}

enum class GraphSearchAlgorithm(val str: String) {
    BFS("Breadth-first search (BFS)"),
    DFS("Depth-first search (DFS)"),
    Dijkstras("Dijkstra's");

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

enum class MazeGenerationAlgorithm(val str: String) {
    RandomizedDFS("Randomized DFS"),
    RandomizedKruskals("Randomized Kruskal's"),
    RandomizedPrims("Randomized Prim's"),
    RecursiveDivision("Recursive division");

    companion object {
        fun valuesToNameList(): MutableList<String> {
            val list = mutableListOf<String>()
            values().forEach { list.add(it.str) }
            return list
        }
    }
}

enum class RootFindingAlgorithm(val str: String) {
    SecantMethod("Secant method"),
    SteffensensMethod("Steffensen's method"),
    InverseInterpolation("Inverse quadratic interpolation");

    companion object {
        fun valuesToNameList(): MutableList<String> {
            val list = mutableListOf<String>()
            values().forEach { list.add(it.str) }
            return list
        }
    }
}