package com.sdu.sharewise.controller

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.sdu.sharewise.Utils.FirebaseManager

class LoginController {
    private val firebaseManager = FirebaseManager()

    fun registerUser(email: String, password: String) {
        firebaseManager.registerUser(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Registration successful, you might want to handle this case
                    // e.g., navigate to the main activity or show a success message
                    println("REGISTER SUCCESS")
                } else {
                    // Registration failed, handle the error
                    // You can access the error message with task.exception?.message
                    println("DET VIRKER ÆTTE")
                }
            })
    }

    fun loginUser(email: String, password: String) {
        firebaseManager.loginUser(email, password)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Login successful, you might want to handle this case
                    // e.g., navigate to the main activity or show a success message
                    println("LOGIN SUCCESS")
                } else {
                    // Login failed, handle the error
                    // You can access the error message with task.exception?.message
                    println("DET VIRKER ÆTTE")
                }
            })
    }
}