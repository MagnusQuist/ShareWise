package com.sdu.sharewise.data.model

data class Expense (
    val uid: String,
    val groupUid: String,
    val expenseDesc: String,
    val amount: Float,
    val expenseCreator: String,
    val expensePayer: String,
    var paid: Boolean,
    var time: Long
) {
    constructor() : this("","","", 0.0f, "", "", false, 0)
}