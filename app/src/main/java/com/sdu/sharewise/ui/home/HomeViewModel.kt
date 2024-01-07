package com.sdu.sharewise.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.utils.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseDB: FirebaseDatabase
) : ViewModel() {

    private val _groups = MutableLiveData<List<Group>>()
    val groups: LiveData<List<Group>> get() = _groups

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchGroups() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = firebaseDB.getReference("Groups").get().await()

                val groupsList = snapshot.children
                    .mapNotNull { it.getValue(Group::class.java) }
                    .filter { group ->
                        group.ownerUid == getCurrentUser?.uid || group.members.contains(getCurrentUser?.uid)
                    }

                _groups.postValue(groupsList)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to get groups.")
            }
        }
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser
}