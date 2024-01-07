package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group

interface GroupRepository {
    val database: FirebaseDatabase?
    suspend fun createGroup(
        groupUid: String,
        name: String,
        desc: String,
        color: String,
        ownerUid: String,
        members: MutableList<String?> = mutableListOf(),
    ): Resource<Group>
    suspend fun addUser(
        groupUid: String,
        uuid: String,
    ): Resource<Group>
    suspend fun removeUser(
        groupUid: String,
        uuid: String,
    ): Resource<Group>
    suspend fun deleteGroup(
        groupUid: String
    )
}