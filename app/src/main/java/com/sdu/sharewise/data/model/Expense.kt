package com.sdu.sharewise.data.model

data class Expense (
    val expenseUid: String,
    val groupUid: String,
    val amount: Float,
    val expenseCreator: String,
    val expensePayer: String,
    var paid: Boolean
) {
    constructor() : this("", "", 0.0f, "", "", false)
}