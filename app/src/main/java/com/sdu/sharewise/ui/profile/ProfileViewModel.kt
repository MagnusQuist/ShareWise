package com.sdu.sharewise.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.UserRepository
import com.sdu.sharewise.data.repository.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {



    private val _isSwitchOn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSwitchOn = _isSwitchOn.asStateFlow()

    private val _textPreference: MutableStateFlow<String> = MutableStateFlow("")
    val textPreference = _textPreference.asStateFlow()

    private val _intPreference: MutableStateFlow<Int> = MutableStateFlow(0)
    val intPreference = _intPreference.asStateFlow()

    fun toggleSwitch() {
        _isSwitchOn.value = _isSwitchOn.value.not()
        // Store the switch state
    }

    fun saveText(finalText: String) {
        _textPreference.value = finalText
        // Store text
    }

    fun checkTextInput(text: String) = text.isNotEmpty()

    fun logout() {
        repository.logout()
    }

    val getCurrentUser: FirebaseUser?
        get() = repository.currentUser

    fun setUsername(uuid: String, name: String) = viewModelScope.launch {
        userRepository.updateUserName(uuid, name)
    }

    fun setEmail(uuid: String, email: String) = viewModelScope.launch{
        userRepository.updateUserEmail(uuid, email)
    }

    fun setPhone(uuid: String,phone: String) = viewModelScope.launch {
        userRepository.updateUserPhone(uuid, phone)
    }

    companion object {
        const val TAG = "ProfileViewModel"
    }
}