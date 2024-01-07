package com.sdu.sharewise.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
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
}