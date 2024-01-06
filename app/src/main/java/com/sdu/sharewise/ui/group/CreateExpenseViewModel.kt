package com.sdu.sharewise.ui.group

import androidx.lifecycle.ViewModel
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CreateExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _createExpenseFlow = MutableStateFlow<Resource<Expense>?>(null)
    val createExpenseFlow: StateFlow<Resource<Expense>?> = _createExpenseFlow

    fun createExpense(groupUid: String, amount: Float, expensePayer: String) {

    }
}