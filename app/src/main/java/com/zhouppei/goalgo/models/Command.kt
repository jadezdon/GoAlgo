package com.zhouppei.goalgo.models

class Command(
    val type: CommandType,
    val args: List<Any>
) {
}

interface CommandType {

}

enum class SortCommandType : CommandType {
    SWAP, COMPARE, COMPLETE
}