package com.sdu.sharewise.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.utils.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser


    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        val trace = FirebasePerformance.getInstance().newTrace("login_trace")
        trace.start() //tracing login
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            trace.stop()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        val trace = FirebasePerformance.getInstance().newTrace("register_trace")
        trace.start() //tracing user registering
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            currentUser?.let { userRepository.createUser(it.uid, name, email, phone = "") }
            trace.stop()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}