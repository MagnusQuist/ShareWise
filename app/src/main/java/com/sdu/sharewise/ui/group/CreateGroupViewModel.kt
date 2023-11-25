package com.sdu.sharewise.ui.group

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
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    // private val groupRepository: GroupRepository
) : ViewModel() {
    private val _createGroupFlow = MutableStateFlow<Resource<Group>?>(null)
    val createGroupFlow: StateFlow<Resource<Group>?> = _createGroupFlow

    fun createGroup(name: String, desc: String) = viewModelScope.launch {
        _createGroupFlow.value = Resource.Loading
        // val result = getCurrentUser?.let { groupRepository.createGroup(name, desc, color = "#2ecc71", ownerUid = it.uid, members = members) }
        // _createGroupFlow.value = result
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser
}