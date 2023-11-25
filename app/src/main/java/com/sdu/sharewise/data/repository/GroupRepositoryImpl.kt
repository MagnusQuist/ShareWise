package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.utils.await
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor (
    private val firebaseDB: FirebaseDatabase
) : GroupRepository {
    override val database: FirebaseDatabase?
        get() = firebaseDB

    override suspend fun createGroup(
        groupUid: String,
        name: String,
        desc: String,
        color: String,
        ownerUid: String,
        members: MutableList<String?>
    ): Resource<Group> {
        return try {
            val memberUuids = getUuidByEmail(members)
            val group = Group(groupUid = groupUid, name = name, desc = desc, color = color, ownerUid = ownerUid, members = memberUuids)
            firebaseDB.getReference("Groups").child(groupUid).setValue(group).await()
            Resource.Success(group)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    private fun getUuidByEmail(members: MutableList<String?>): MutableList<String?> {
        TODO("Check if user exist and get uuid")
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