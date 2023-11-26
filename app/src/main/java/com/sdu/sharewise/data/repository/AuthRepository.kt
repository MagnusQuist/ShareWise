package com.sdu.sharewise.data.repository

import com.google.firebase.auth.FirebaseUser
import com.sdu.sharewise.data.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun register(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logout()
}