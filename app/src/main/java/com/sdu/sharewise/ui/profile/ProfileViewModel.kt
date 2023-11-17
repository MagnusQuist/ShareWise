package com.sdu.sharewise.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

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

    companion object {
        const val TAG = "ProfileViewModel"
    }
}