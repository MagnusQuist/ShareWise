package com.sdu.sharewise.ui.group

import android.util.Log
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
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupExpenseRepository
import com.sdu.sharewise.data.repository.UserRepository
import com.sdu.sharewise.data.utils.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedGroupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val groupExpenseRepository: GroupExpenseRepository,
    private val firebaseDB: FirebaseDatabase
) : ViewModel() {
    private val _selectedGroup = MutableLiveData<Group?>()
    val selectedGroup: MutableLiveData<Group?> get() = _selectedGroup

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    private val _payExpenseFlow = MutableStateFlow<Resource<String>?>(null)
    val payExpenseFlow: StateFlow<Resource<String>?> = _payExpenseFlow

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchGroup(groupUid: String) {
        firebaseDB.getReference("Groups").child(groupUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)

                if (group != null) {
                    if (group.ownerUid == authRepository.currentUser?.uid ||
                        group.members.contains(authRepository.currentUser?.uid)) {
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

    fun fetchExpenses(groupUid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = firebaseDB.getReference("GroupExpenses")
                    .orderByChild("groupUid")
                    .equalTo(groupUid)
                    .get()
                    .await()

                val expensesList = snapshot.children
                    .mapNotNull { it.getValue(Expense::class.java) }
                    .filter { expense ->
                        expense.groupUid == groupUid
                    }

                _expenses.postValue(expensesList)
            } catch (e: Exception) {
                Log.d("FetchExpenses", "Error: ${e.message}")
                _errorMessage.postValue("Failed to get groups.")
            }
        }
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

    fun payExpense(expenseUid: String, expensePayer: String, amount: Float, paid: Boolean) = viewModelScope.launch {
        _payExpenseFlow.value = Resource.Loading
        groupExpenseRepository.payExpense(expenseUid, expensePayer, amount, paid)
        _payExpenseFlow.value = Resource.Success("Expense Paid")
    }

    val getCurrentUser: FirebaseUser?
        get() = authRepository.currentUser
}