package com.sdu.sharewise.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    private val _ownGroups = MutableLiveData<List<Group>>()
    val ownGroups: LiveData<List<Group>> get() = _ownGroups

    private val _othersGroups = MutableLiveData<List<Group>>()
    val othersGroups: LiveData<List<Group>> get() = _othersGroups

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchGroups() {
        firebaseDB.getReference("Groups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ownGroupsList = mutableListOf<Group>()
                val othersGroupsList = mutableListOf<Group>()

                for (childSnapshot in snapshot.children) {
                    val group = childSnapshot.getValue(Group::class.java)

                    if (group != null) {
                        if (group.ownerUid == authRepository.currentUser?.uid) {
                            ownGroupsList.add(group)
                        } else if (group.members.contains(authRepository.currentUser?.email)) {
                            othersGroupsList.add(group)
                        }
                    }
                }

                _ownGroups.value = ownGroupsList
                _othersGroups.value = othersGroupsList
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Fail to get groups."
            }
        })
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser
}