package com.sdu.sharewise.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        TODO("addExpense and createGroupExpense are the same thing lol")
    }

    override suspend fun removeExpense(expenseUid: String, groupUid: String, paid: Boolean) {
        val trace = FirebasePerformance.getInstance().newTrace("createRemoveExpense_trace")
        trace.start()

        try {
            val expensesRef = FirebaseDatabase.getInstance().getReference("groups/$groupUid/expenses")

            // Constructing the query to find the expense with expenseUid and groupUid
            val query = expensesRef
                .orderByChild("expenseUid")
                .equalTo(expenseUid)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (expenseSnapshot in dataSnapshot.children) {
                        val expense = expenseSnapshot.getValue(GroupExpense::class.java)
                        // Checking if the expense exists and the 'paid' condition is true
                        if (expense != null && expense.paid == paid) {
                            // Removing the expense where condition matches
                            expenseSnapshot.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        } finally {
            trace.stop()
        }
    }

    override suspend fun payExpense(expenseId: Int, expensePayer: String, amount: Float, paid: Boolean) {
        val trace = FirebasePerformance.getInstance().newTrace("payExpense_trace")
        trace.start()

        try {
            val expensesRef = FirebaseDatabase.getInstance().getReference("expenses")
            val query = expensesRef.orderByChild("expenseId").equalTo(expenseId.toDouble())

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (expenseSnapshot in dataSnapshot.children) {
                        val expense = expenseSnapshot.getValue(GroupExpense::class.java)

                        // Check if the expense exists and the payer matches
                        if (expense != null && expense.expensePayer == expensePayer) {
                            // Check if the amount matches and the expense is not already paid
                            if (expense.amount == amount && !expense.paid) {
                                expense.paid = true
                                expenseSnapshot.ref.setValue(expense) // Update 'paid' field to true
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        } finally {
            trace.stop()
        }
    }
    override suspend fun totalExpenses(groupUid: String, amount: Float) {
        val trace = FirebasePerformance.getInstance().newTrace("totalExpenses_trace")
        trace.start()

        try {
            val expensesRef = FirebaseDatabase.getInstance().getReference("groups/$groupUid/expenses")
            expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var totalOwed = 0f

                    for (expenseSnapshot in dataSnapshot.children) {
                        val expense = expenseSnapshot.getValue(GroupExpense::class.java)
                        // Check if the expense exists and the amount is owed (not paid)
                        if (expense != null && !expense.paid) {
                            totalOwed += expense.amount
                        }
                    }
                    // Here, 'totalOwed' holds the sum of amounts owed for expenses
                    // You can use 'totalOwed' in your application logic as needed
                    // For example, you can pass it to a function or display it in your app UI
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        } finally {
            trace.stop()
        }
    }
}