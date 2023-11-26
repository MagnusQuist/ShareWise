package com.sdu.sharewise.ui.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseDB: FirebaseDatabase
) : ViewModel() {

    private val _ownGroupFlow = MutableStateFlow<Resource<Group>?>(null)
    val ownGroupFlow: StateFlow<Resource<Group>?> = _ownGroupFlow

    private val _othersGroupFlow = MutableStateFlow<Resource<Group>?>(null)
    val othersGroupFlow: StateFlow<Resource<Group>?> = _othersGroupFlow

    val getDatabase: FirebaseDatabase
        get() = firebaseDB

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser

}