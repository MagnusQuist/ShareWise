package com.sdu.sharewise.data

import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.model.UserModel

interface UserRepository {
    val database: FirebaseDatabase?
    suspend fun createUser(uuid: String, name: String, email: String, phone: String): Resource<UserModel>
    suspend fun updateUserPhone(uuid: String, phone: String): Resource<UserModel>
    suspend fun deleteUser()
}