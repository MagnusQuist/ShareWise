package com.sdu.sharewise.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.utils.await
import com.sdu.sharewise.services.NotificationSenderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

class GroupExpenseRepositoryImpl @Inject constructor(
    private val firebaseDB: FirebaseDatabase,
    private val userRepository: UserRepository,
) : GroupExpenseRepository {
    override val database: FirebaseDatabase?
        get() = firebaseDB

    override suspend fun createGroupExpense(
        expenseUid: String,
        groupUid: String,
        expenseDesc: String,
        amount: Float,
        expenseCreator: String,
        expensePayer: String,
        paid: Boolean,
        time: Long
    ): Resource<Expense> {
        val trace = FirebasePerformance.getInstance().newTrace("createGroupExpense_trace")
        trace.start()

        return try {
            val expense = Expense(uid = expenseUid, groupUid = groupUid, amount = amount, expenseDesc = expenseDesc, expenseCreator = expenseCreator, expensePayer = expensePayer, paid = paid, time = time)
            firebaseDB.getReference("GroupExpenses").child(expenseUid).setValue(expense).await()
            trace.stop()
            val tokens: ArrayList<String> = ArrayList()
            userRepository.getTokenFromUuid(expensePayer) {token ->
                if (token != null) { tokens.add(token) }
            }

            NotificationSenderService.sendNotification(
                tokens,
                "New expense",
                "You owe $amount DKK"
            )
            Resource.Success(expense)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
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
                        val expense = expenseSnapshot.getValue(Expense::class.java)
                        // Checking if the expense exists and the 'paid' condition is true
                        if (expense != null && expense.paid == paid) {
                            // Removing the expense where condition matches
                            expenseSnapshot.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
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

    override suspend fun payExpense(expenseId: String, expensePayer: String, amount: Float, paid: Boolean) {
        val trace = FirebasePerformance.getInstance().newTrace("payExpense_trace")
        trace.start()

        try {
            val expensesRef = FirebaseDatabase.getInstance().getReference("GroupExpenses")
            val query = expensesRef.orderByChild("uid").equalTo(expenseId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (expenseSnapshot in dataSnapshot.children) {
                        val expense = expenseSnapshot.getValue(Expense::class.java)

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
                    TODO("Not yet implemented")
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
                        val expense = expenseSnapshot.getValue(Expense::class.java)
                        // Check if the expense exists and the amount is owed (not paid)
                        if (expense != null && !expense.paid) {
                            totalOwed += expense.amount
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    TODO("Not yet implemented")
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

    override suspend fun userOwed(expenseCreator: String, amount: Float, groupUid: String) {
        return withContext(Dispatchers.IO) {
            val trace = FirebasePerformance.getInstance().newTrace("userOwed_trace")
            trace.start()

            val deferred = CompletableDeferred<Float>()

            try {
                val expensesRef = FirebaseDatabase.getInstance().getReference("groups/$groupUid/expenses")
                expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var totalOwedToUser = 0f

                        for (expenseSnapshot in dataSnapshot.children) {
                            val expense = expenseSnapshot.getValue(Expense::class.java)
                            // Check if the expense exists, the creator matches, and the expense is not paid
                            if (expense != null && expense.expenseCreator == expenseCreator && !expense.paid) {
                                totalOwedToUser += expense.amount
                            }
                        }

                        deferred.complete(totalOwedToUser)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        deferred.completeExceptionally(databaseError.toException())
                    }
                })
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            } finally {
                trace.stop()
            }

            deferred.await()
        }
    }
}