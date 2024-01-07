package com.sdu.sharewise.ui.group

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.ConfirmDialog
import com.sdu.sharewise.ui.components.ProfileItemComp
import com.sdu.sharewise.ui.components.SettingsClickableComp
import com.sdu.sharewise.ui.components.SettingsGroup

@Composable
fun SettingsGroupView(
    viewModel: SettingsGroupViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val group by viewModel.group.observeAsState(Group())
    val expenses by viewModel.expenses.observeAsState(emptyList())

    val groupUid = navController.currentBackStackEntry?.arguments?.getString("groupUid").orEmpty()

    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current.applicationContext

    LaunchedEffect(groupUid) {
        viewModel.fetchGroup(groupUid)
        viewModel.fetchExpenses(groupUid)
    }

    Surface(
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(22.dp),
                        onClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        }) {
                        Icon(
                            modifier = Modifier
                                .size(22.dp),
                            imageVector = Icons.Default.ArrowBackIos,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Go Back"
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Group Settings",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            SettingsGroup(
                name = "",
            ) {
                SettingsClickableComp(
                    name = "Name",
                    value = group?.name ?: "",
                    color = MaterialTheme.colorScheme.primary
                ) {

                }
                SettingsClickableComp(
                    name = "Members",
                    value = (group?.members?.size?.plus(1)).toString(),
                    color = MaterialTheme.colorScheme.primary
                ) {

                }
                SettingsClickableComp(
                    name = "Expenses",
                    value = expenses.size.toString(),
                    color = MaterialTheme.colorScheme.primary
                ) {

                }
            }

            SettingsGroup(
                name = "",
            ) {
                ProfileItemComp(
                    name = "Created By",
                    value = group?.ownerUid?: "",
                    color = MaterialTheme.colorScheme.primary
                )
                SettingsClickableComp(
                    name = "Delete Group",
                    value = "",
                    color = MaterialTheme.colorScheme.error
                ) {
                    if (!viewModel.hasUnpaidExpenses()) {
                        showDialog = true
                    } else {
                        Toast.makeText(context, "Group has unpaid expenses!", Toast.LENGTH_SHORT).show()
                    }
                }

                if (showDialog) {
                    ConfirmDialog(
                        title = "Are you sure?",
                        body = "You are about to delete a group. This action cannot be undone!",
                        confirmText = "Yes, delete",
                        cancelText = "Cancel",
                        onConfirm = {
                            showDialog = false
                            viewModel.deleteGroup()
                            navController.navigate(Routes.Home.route) {
                                // Pop up to the home destination and clear the back stack
                                popUpTo(Routes.Home.route) {
                                    saveState = false
                                }
                                // Avoid multiple copies of the home destination when navigating up
                                launchSingleTop = true
                            }
                        },
                        onCancel = {
                            showDialog = false
                        }
                    )
                }
            }
        }
    }
}
