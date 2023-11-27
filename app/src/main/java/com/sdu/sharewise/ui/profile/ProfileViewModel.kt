package com.sdu.sharewise.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.User
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val firebaseDB: FirebaseDatabase
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    private val _updateEmailFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val updateEmailFlow: StateFlow<Resource<FirebaseUser>?> = _updateEmailFlow

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

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
        authRepository.logout()
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser

    fun setUsername(uuid: String, name: String) = viewModelScope.launch {
        userRepository.updateUserName(uuid = uuid, name = name, authRepository = authRepository)
    }

    fun setEmail(uuid: String, newEmail: String, password: String) = viewModelScope.launch{
        userRepository.updateUserEmail(uuid = uuid, newEmail = newEmail, password = password, authRepository = authRepository)
    }

    fun setPhone(uuid: String, phone: String) = viewModelScope.launch {
        userRepository.updateUserPhone(uuid = uuid, phone = phone)
    }

    fun fetchUser(uuid: String) {
        firebaseDB.getReference("Users").child(uuid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userSnap = snapshot.getValue(User::class.java)

                if (userSnap != null) {
                    if (userSnap.uuid == authRepository.currentUser?.uid) {
                        _user.value = userSnap
                    } else {
                        _errorMessage.value = "User not logged in"
                    }
                } else {
                    _errorMessage.value = "User not found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Fail to get user."
            }
        })
    }

    companion object {
        const val TAG = "ProfileViewModel"
    }
}