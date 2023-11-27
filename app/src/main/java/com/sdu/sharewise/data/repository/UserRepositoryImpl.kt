package com.sdu.sharewise.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
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

    override suspend fun updateUserName(uuid: String, name: String, authRepository: AuthRepository): Resource<String> {
        return try {
            authRepository.currentUser?.updateProfile(userProfileChangeRequest {
                displayName = name
            })
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
        return try {
            // Update Auth
            val user = authRepository.currentUser
            val credential = user?.email?.let { EmailAuthProvider.getCredential(it, password) }

            try {
                if (credential != null) {
                    user.reauthenticate(credential).await()
                }

                val result = user?.verifyBeforeUpdateEmail(newEmail)?.await()

                // Update DB
                firebaseDB.getReference("Users").child(uuid).child("email").setValue(newEmail).await()
                Resource.Success(newEmail)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Failure(e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
