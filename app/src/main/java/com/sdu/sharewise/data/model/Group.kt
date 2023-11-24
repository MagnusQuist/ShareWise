package com.sdu.sharewise.data.model

data class Group(
    val groupUid: String,
    val name: String,
    val desc: String,
    val color: String,
    val owner: User,
    val users: MutableList<User> = mutableListOf(),
    val expenses: MutableList<Expense> = mutableListOf(),
)