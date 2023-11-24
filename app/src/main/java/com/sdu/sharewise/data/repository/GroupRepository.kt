package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.model.User

interface GroupRepository {
    val database: FirebaseDatabase?
    suspend fun createGroup(
        groupUid: String,
        name: String,
        desc: String,
        color: String,
        owner: User,
        users: MutableList<User> = mutableListOf(),
    ): Resource<Group>
    suspend fun addUser(
        groupUid: String,
        userToAdd: User,
    ): Resource<Group>
    suspend fun removeUser(
        groupUid: String,
        uuid: String,
    ): Resource<Group>
    suspend fun addExpense(
        expenseUid: String,
        groupUid: String,
        user: User,
        expense: Float,
    )
    suspend fun payExpense()
    suspend fun deleteGroup()
}