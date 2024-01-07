package com.sdu.sharewise.data.repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.perf.FirebasePerformance
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.User
import com.sdu.sharewise.data.utils.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val firebaseDB: FirebaseDatabase
) : UserRepository {
    override val database: FirebaseDatabase?
        get() = firebaseDB

    override suspend fun createUser(
        uuid: String,
        name: String,
        email: String,
        phone: String,
        notificationToken: String,
    ): Resource<User> {
        val trace = FirebasePerformance.getInstance().newTrace("createUser_trace")
        trace.start()
        return try {
            val user = User(uuid = uuid, name = name, email = email, phone = phone, notificationToken = notificationToken)
            firebaseDB.getReference("Users").child(uuid).setValue(user).await()
            trace.stop()
            Resource.Success(user)
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserName(uuid: String, name: String, authRepository: AuthRepository): Resource<String> {
        val trace = FirebasePerformance.getInstance().newTrace("updateUserName_trace")
        trace.start()

        return try {
            authRepository.currentUser?.updateProfile(userProfileChangeRequest {
                displayName = name
            })
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
        newEmail: String,
        password: String,
        authRepository: AuthRepository
    ): Resource<String> {
        val trace = FirebasePerformance.getInstance().newTrace("updateUserEmail_trace")
        trace.start()

        val user = authRepository.currentUser
        val credential = user?.email?.let { EmailAuthProvider.getCredential(it, password) }
            ?: return Resource.Failure(Exception("Bad credentials"))

        user.reauthenticate(credential)
            .addOnCompleteListener {
                user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener { updateEmailTask ->
                    if (updateEmailTask.isSuccessful) {
                        firebaseDB.getReference("Users").child(uuid).child("email").setValue(newEmail)
                        authRepository.logout()
                        return@addOnCompleteListener
                    } else {
                        val exception = updateEmailTask.exception
                        Log.d("CHANGE EMAIL", exception.toString())
                    }
                }
            }
            .addOnFailureListener {
                return@addOnFailureListener
            }
        return Resource.Failure(Exception("Unexpected Failure"))
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

    override suspend fun getUuidByEmail(
        email: String,
        callback: (String?) -> Unit
    ) {
        val query = firebaseDB.getReference("Users").orderByChild("email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if there is any matching user
                if (dataSnapshot.exists()) {
                    // Iterate through the matching users (although there should be only one)
                    for (userSnapshot in dataSnapshot.children) {
                        // Get the UUID from the user data
                        val uuid = userSnapshot.child("uuid").getValue(String::class.java)
                        callback(uuid)
                        return
                    }
                }

                // If no matching user found, return null
                callback(null)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if needed
                // For simplicity, we're just printing the error message here
                println("Firebase Database Error: ${databaseError.message}")
                callback(null)
            }
        })
    }

    override suspend fun deleteUser() {
        TODO("Not yet implemented")
    }

    override suspend fun setNotificationtoken(uuid: String, token: String) {
        val trace = FirebasePerformance.getInstance().newTrace("setNotificationToken_trace")
        trace.start()
        try {
            firebaseDB.getReference("Users").child(uuid).child("notificationToken")
                .setValue(token)
            trace.stop()
        } catch (e: Exception) {
            trace.stop()
            e.printStackTrace()
        }
    }

    override suspend fun getTokenFromUuid(uuid: String): String? = suspendCoroutine { continuation ->
        val query = firebaseDB.getReference("Users").orderByChild("uuid").equalTo(uuid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val token = userSnapshot.child("notificationToken").getValue(String::class.java)
                        continuation.resume(token)
                        return
                    }
                }

                continuation.resume(null)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase database error: ${error.message}")
                continuation.resume(null)
            }
        })
    }
}
