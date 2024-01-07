package com.sdu.sharewise.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupExpenseRepository
import com.sdu.sharewise.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val expenseRepository: GroupExpenseRepository
) : ViewModel() {
    private val _createExpenseFlow = MutableStateFlow<Resource<Expense>?>(null)
    val createExpenseFlow: StateFlow<Resource<Expense>?> = _createExpenseFlow

    fun createExpense(expenseDesc: String, groupUid: String, amount: Float, expensePayer: String) = viewModelScope.launch {
        _createExpenseFlow.value = Resource.Loading
        val expenseUid = UUID.randomUUID().toString()
        val time = System.currentTimeMillis()
        val result = authRepository.currentUser?.let { expenseRepository.createGroupExpense(expenseUid, groupUid, expenseDesc, amount, it.uid, expensePayer, false, time) }
        _createExpenseFlow.value = result
    }

    fun findEmailByUuid(uuid: String, callback: (String?, String?) -> Unit) = viewModelScope.launch {
        userRepository.getEmailByUuid(uuid) { email ->
            if (email != null) {
                callback("success", email)
            } else {
                callback("Email not found", null)
            }
        }

        return@launch
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser
}