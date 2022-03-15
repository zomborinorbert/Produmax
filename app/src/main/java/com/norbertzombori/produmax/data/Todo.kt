package com.norbertzombori.produmax.data


data class Todo(var description: String = "", var done: Boolean = false, var members: List<String> = listOf())