package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.GroupExpense
import com.sdu.sharewise.data.utils.await
import javax.inject.Inject

class GroupExpenseRepositoryImpl @Inject constructor(
    private val firebaseDB: FirebaseDatabase
) : GroupExpenseRepository {
    override val database: FirebaseDatabase?
        get() = firebaseDB

    override suspend fun createGroupExpense(
        expenseUid: String,
        groupUid: String,
        amount: Float,
        expenseCreator: String,
        expensePayer: String,
        paid: Boolean
    ): Resource<GroupExpense> {
        val trace = FirebasePerformance.getInstance().newTrace("createGroupExpense_trace")
        trace.start()

        return try {
            val expense = GroupExpense(expenseUid = expenseUid, groupUid = groupUid, amount = amount, expenseCreator = expenseCreator, expensePayer = expensePayer, paid =paid)
            firebaseDB.getReference("GroupExpenses").child(expenseUid).setValue(expense).await()
            trace.stop()
            Resource.Success(expense)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun addExpense(groupUid: String, amount: Float, expenseCreator: String, expensePayer: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeExpense(expenseUid: String, groupUid: String, paid: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun payExpense(expenseId: Int, expensePayer: String, amount: Float, paid: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun totalExpenses(groupUid: String, amount: Float) {
        TODO("Not yet implemented")
    }
}