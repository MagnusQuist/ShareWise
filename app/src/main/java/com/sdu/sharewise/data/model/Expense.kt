package com.sdu.sharewise.data.model

data class Expense (
    val expenseUid: String,
    val groupUid: String,
    val user: User,
    val expense: Float,
)