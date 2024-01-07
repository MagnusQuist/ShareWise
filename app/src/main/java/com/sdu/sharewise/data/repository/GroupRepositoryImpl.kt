package com.sdu.sharewise.data.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.utils.await
import com.sdu.sharewise.services.NotificationSenderService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor (
    private val firebaseDB: FirebaseDatabase,
    private val userRepository: UserRepository
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
        val trace = FirebasePerformance.getInstance().newTrace("createGroup_trace")
        trace.start() //Group creation tracing

        return try {
            val group = Group(groupUid = groupUid, name = name, desc = desc, color = color, ownerUid = ownerUid, members = members)
            firebaseDB.getReference("Groups").child(groupUid).setValue(group).await()

            val tokens: ArrayList<String> = ArrayList<String>()
            for (uuid in members) {
                if (uuid != null) {
                    userRepository.getTokenFromUuid(uuid) {token ->
                        if (token != null) {
                            tokens.add(token)
                        }
                    }
                }
            }

            NotificationSenderService.sendNotification(
                tokens,
                "Added to a New Group",
                "You have been added to: $name"
            )
            trace.stop()
            Resource.Success(group)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun addUser(groupUid: String, uuid: String): Resource<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUser(groupUid: String, uuid: String): Resource<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGroup(groupUid: String) {
        try {
            firebaseDB.reference.child("Groups").child(groupUid).removeValue().await()
        } catch (e: Exception) {
            Log.d("Group Repository", e.toString())
        }
    }
}