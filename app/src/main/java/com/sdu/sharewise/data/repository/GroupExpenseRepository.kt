package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Expense


interface GroupExpenseRepository {
    val database: FirebaseDatabase?

    suspend fun createGroupExpense(
        expenseUid: String,
        groupUid: String,
        expenseDesc: String,
        amount: Float,
        expenseCreator: String,
        expensePayer: String,
        paid: Boolean,
        time: Long,
    ): Resource<Expense>

    suspend fun removeExpense(
        expenseUid: String,
        groupUid: String,
        paid: Boolean
    )

    suspend fun payExpense(
        expenseId: Int,
        expensePayer: String,
        amount: Float,
        paid: Boolean
    )

    suspend fun totalExpenses(
        groupUid: String,
        amount: Float,
    )

    suspend fun userOwed(
        expenseCreator: String,
        amount: Float,
        groupUid: String
    )



}