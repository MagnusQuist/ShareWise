package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.User
import com.sdu.sharewise.data.utils.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseDB: FirebaseDatabase
) : UserRepository {
    override val database: FirebaseDatabase?
        get() = firebaseDB

    override suspend fun createUser(
        uuid: String,
        name: String,
        email: String,
        phone: String
    ): Resource<User> {
        val trace = FirebasePerformance.getInstance().newTrace("createUser_trace")
        trace.start()
        return try {
            val user = User(uuid = uuid, name = name, email = email, phone = phone)
            firebaseDB.getReference("Users").child(uuid).setValue(user).await()
            trace.stop()
            Resource.Success(user)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserName(uuid: String, name: String): Resource<String> {
        val trace = FirebasePerformance.getInstance().newTrace("updateUserName_trace")
        trace.start()
        return try {
            firebaseDB.getReference("Users").child(uuid).child("name").setValue(name).await()
            trace.stop()
            Resource.Success(name)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserEmail(
        uuid: String,
        email: String
    ): Resource<String> {
        val trace = FirebasePerformance.getInstance().newTrace("updateUserEmail_trace")
        trace.start()
        return try {
            firebaseDB.getReference("Users").child(uuid).child("email").setValue(email).await()
            trace.stop()
            Resource.Success(email)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserPhone(
        uuid: String,
        phone: String
    ): Resource<String> {
        val trace = FirebasePerformance.getInstance().newTrace("updateUserPhone_trace")
        trace.start()
        return try {
            firebaseDB.getReference("Users").child(uuid).child("phone").setValue(phone).await()
            trace.stop()
            Resource.Success(phone)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun deleteUser() {
        TODO("Not yet implemented")
    }
}