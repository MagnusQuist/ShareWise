package com.sdu.sharewise.data.repository

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    override suspend fun updateUserName(uuid: String, name: String): Resource<String> {
        return try {
            firebaseDB.getReference("Users").child(uuid).child("name").setValue(name).await()

            Resource.Success(name)
        } catch (e: Exception) {
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
        return Resource.Failure(Exception("Not working. Come back next year"))
    }

    suspend fun updateUserEmailInFirestore(userId: String, newEmail: String): Resource<Unit> {
        Log.d("CHANGE EMAIL IN FIRESTORE","PLS")
        return try {
            // Update the Firestore document with the new email
            firebaseDB.getReference("Users").child(userId).child("email").setValue(newEmail).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Handle Firestore update error and log the details
            Log.e("FirestoreUpdate", "Failed to update Firestore document: ${e.message}", e)
            Resource.Failure(e)
        }
    }

    override suspend fun updateUserPhone(
        uuid: String,
        phone: String
    ): Resource<String> {
        return try {
            firebaseDB.getReference("Users").child(uuid).child("phone").setValue(phone).await()
            Resource.Success(phone)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun deleteUser() {
        TODO("Not yet implemented")
    }
}
