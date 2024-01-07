package com.sdu.sharewise.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
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

            saveFCMToken(result.user?.uid)

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

            currentUser?.let {
                userRepository.createUser(
                    it.uid,
                    name,
                    email,
                    phone = "",
                    notificationToken = "",
                )
            }

            saveFCMToken(result.user?.uid)
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

    private suspend fun saveFCMToken(userId: String?) {
        val token = FirebaseMessaging.getInstance().token.await()
        Log.d("Token Generator", "Generating Token for user: $token")

        token?.let {
            userId?.let {
                userRepository.setNotificationtoken(userId, token)
            }
        }
    }
}