package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.model.User

class GroupRepositoryImpl : GroupRepository {
    override val database: FirebaseDatabase?
        get() = TODO("Not yet implemented")

    override suspend fun createGroup(
        groupUid: String,
        name: String,
        desc: String,
        color: String,
        ownerUid: String,
        members: MutableList<String?>
    ): Resource<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun addUser(groupUid: String, uuid: String): Resource<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUser(groupUid: String, uuid: String): Resource<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun addExpense(
        expenseUid: String,
        groupUid: String,
        uuid: String,
        expense: Float
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun payExpense() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroup() {
        TODO("Not yet implemented")
    }
}