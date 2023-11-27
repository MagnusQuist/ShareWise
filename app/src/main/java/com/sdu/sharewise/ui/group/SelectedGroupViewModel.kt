package com.sdu.sharewise.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectedGroupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseDB: FirebaseDatabase
) : ViewModel() {
    private val _selectedGroup = MutableLiveData<Group?>()
    val selectedGroup: MutableLiveData<Group?> get() = _selectedGroup

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchGroup(groupUid: String) {
        firebaseDB.getReference("Groups").child(groupUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)

                if (group != null) {
                    if (group.ownerUid == authRepository.currentUser?.uid ||
                        group.members.contains(authRepository.currentUser?.email)) {
                        _selectedGroup.value = group
                    } else {
                        _errorMessage.value = "You don't have access to this group."
                    }
                } else {
                    _errorMessage.value = "Group not found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Fail to get group."
            }
        })
    }
}