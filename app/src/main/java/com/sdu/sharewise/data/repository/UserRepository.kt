package com.sdu.sharewise.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.User

interface UserRepository {
    val database: FirebaseDatabase?
    suspend fun createUser(uuid: String, name: String, email: String, phone: String, notificationToken: String): Resource<User>
    suspend fun updateUserName(uuid: String, name: String, authRepository: AuthRepository): Resource<String>
    suspend fun updateUserEmail(
        uuid: String,
        newEmail: String,
        password: String,
        authRepository: AuthRepository
    ): Resource<String>
    suspend fun updateUserPhone(uuid: String, phone: String): Resource<String>

    suspend fun deleteUser()
    suspend fun getUuidByEmail(email: String, callback: (String?) -> Unit)
    suspend fun setNotificationtoken(uuid: String, token: String)
    suspend fun getTokenFromUuid(uuid: String, callback: (String?) -> Unit)
}