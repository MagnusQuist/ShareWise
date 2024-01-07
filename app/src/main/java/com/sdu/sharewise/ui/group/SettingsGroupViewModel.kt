package com.sdu.sharewise.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupRepository
import com.sdu.sharewise.data.repository.UserRepository
import javax.inject.Inject

class SettingsGroupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val firebaseDB: FirebaseDatabase,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _group = MutableLiveData<Group?>()
    val group: MutableLiveData<Group?> get() = _group

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses

    fun hasUnpaidExpenses(): Boolean {
        val unpaidExpenses = _expenses.value?.filter { !it.paid } ?: emptyList()
        return unpaidExpenses.isNotEmpty()
    }

    fun deleteGroup() {
        if (!hasUnpaidExpenses()) {
            group.value?.groupUid?.let { groupRepository.deleteGroup(it) }
        }
    }
}