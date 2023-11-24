package com.sdu.sharewise.data.repository

import com.google.firebase.database.FirebaseDatabase
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
        return try {
            val user = User(uuid = uuid, name = name, email = email, phone = phone)
            firebaseDB.getReference("Users").child(uuid).setValue(user).await()
            Resource.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserPhone(
        uuid: String,
        phone: String
    ): Resource<User> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser() {
        TODO("Not yet implemented")
    }
}