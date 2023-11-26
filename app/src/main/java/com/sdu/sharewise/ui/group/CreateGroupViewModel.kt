package com.sdu.sharewise.ui.group

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _createGroupFlow = MutableStateFlow<Resource<Group>?>(null)
    val createGroupFlow: StateFlow<Resource<Group>?> = _createGroupFlow

    private val _groupName = MutableStateFlow("")
    private val _groupDescription = MutableStateFlow("")
    private val _groupMembers = MutableStateFlow(SnapshotStateList<String>())

    fun setGroupName(name: String) {
        _groupName.value = name
    }

    fun setGroupDescription(description: String) {
        _groupDescription.value = description
    }

    fun setGroupMembers(members: SnapshotStateList<String>) {
        _groupMembers.value = members
    }

    fun createGroup(name: String, desc: String, members: SnapshotStateList<String?>) = viewModelScope.launch {
        _createGroupFlow.value = Resource.Loading
        val groupUid = UUID.randomUUID().toString()
        val result = getCurrentUser?.let { groupRepository.createGroup(groupUid = groupUid, name = name, desc = desc, color = "#2ecc71", ownerUid = it.uid, members = members) }
        _createGroupFlow.value = result
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser
}