package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.utils.await
import com.sdu.sharewise.services.NotificationSenderService
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

            userRepository.getTokenFromUuid(ownerUid) { token ->
                if (token != null) {
                    NotificationSenderService.sendNotification(
                        listOf(token),
                        "Added to group",
                        "You have been added to a new group called: $name"
                    )
                }
            }
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

    override suspend fun deleteGroup() {
        TODO("Not yet implemented")
    }
}