package com.sdu.sharewise.ui.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.data.model.User
import com.sdu.sharewise.data.repository.AuthRepository
import com.sdu.sharewise.data.repository.GroupRepository
import com.sdu.sharewise.data.repository.UserRepository
import com.sdu.sharewise.data.utils.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val firebaseDB: FirebaseDatabase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _group = MutableLiveData<Group?>()
    val group: MutableLiveData<Group?> get() = _group

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _groupCreator = MutableLiveData<User>()
    val groupCreator: MutableLiveData<User> = _groupCreator

    fun fetchGroup(groupUid: String) {
        firebaseDB.getReference("Groups").child(groupUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)

                if (group != null) {
                    if (group.ownerUid == authRepository.currentUser?.uid ||
                        group.members.contains(authRepository.currentUser?.uid)) {
                        _group.value = group
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

    fun hasUnpaidExpenses(): Boolean {
        val unpaidExpenses = _expenses.value?.filter { !it.paid } ?: emptyList()
        return unpaidExpenses.isNotEmpty()
    }

    fun deleteGroup() = viewModelScope.launch {
        if (!hasUnpaidExpenses()) {
            group.value?.groupUid?.let { groupRepository.deleteGroup(it) }
        }
    }
}