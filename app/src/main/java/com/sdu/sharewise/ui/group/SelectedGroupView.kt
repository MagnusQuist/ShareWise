package com.sdu.sharewise.ui.group

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.NavigateToHomeButton

@Composable
fun SelectedGroupView (
    viewModel: SelectedGroupViewModel,
    navController: NavHostController
) {
    var activeExpensesCount by remember { mutableStateOf(0) }

    val selectedGroup by viewModel.selectedGroup.observeAsState(Group())

    val expenses by viewModel.expenses.observeAsState(emptyList())

    val payExpenseFlow = viewModel.payExpenseFlow.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    // Observe error message from ViewModel
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    // Fetch groups when the composable is first created
    val groupUid = navController.currentBackStackEntry?.arguments?.getString("groupUid").orEmpty()

    LaunchedEffect(groupUid) {
        viewModel.fetchGroup(groupUid)
        viewModel.fetchExpenses(groupUid)
    }

    val context = LocalContext.current.applicationContext

    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    NavigateToHomeButton(navController = navController)

                    selectedGroup?.let {
                        it.name?.let { it1 ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = it1,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (viewModel.getCurrentUser?.uid == selectedGroup?.ownerUid) {
                        IconButton(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.TopEnd),
                            onClick = {
                                navController.navigate(
                                    "selectedGroup/"+selectedGroup?.groupUid+"/settings", // Use the route with the parameter
                                    builder = {
                                        launchSingleTop = true

                                        popUpTo(Routes.Home.route) {
                                            saveState = true
                                        }
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        with(navController.currentBackStackEntry?.arguments) {
                                            this?.getString("groupUid")?.let { groupUid ->
                                                putString("groupUid", groupUid) // Provide the groupId here
                                            }
                                        }
                                    }
                                )
                            }) {
                            Icon(
                                modifier = Modifier
                                    .size(22.dp),
                                imageVector = Icons.Default.Settings,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Group Settings"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("group", selectedGroup)
                    navController.navigate(Routes.CreateExpense.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
            ) {
                Text(
                    text = "Add Expense",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.scrim
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (expenses.isEmpty()) {
                    item {
                        Text(
                            text = "No expenses yet \uD83D\uDE40",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.surfaceTint,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        )
                    }
                } else {
                    itemsIndexed (expenses) { index, _ ->
                        if (!expenses[index].paid) {
                            activeExpensesCount += 1

                            var email by remember { mutableStateOf<String?>(null) }

                            // Get email from uuid
                            LaunchedEffect(expenses[index].expenseCreator) {
                                viewModel.findEmailByUuid(expenses[index].expenseCreator) { tag, message ->
                                    if (tag == "success") {
                                        if (message != null) {
                                            email = message
                                        }
                                    } else {
                                        email = "Email not found.."
                                    }
                                }
                            }

                            if (email != null) {
                                viewModel.getCurrentUser?.uid?.let {
                                    ExpenseCard(modifier = Modifier, expense = expenses[index],
                                        currectUserUid =  it, expenseCreaterEmail = email!!, viewModel = viewModel, isLoading = isLoading
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }

                if (expenses.isNotEmpty() && activeExpensesCount <= 0) {
                    item {
                        Text(
                            text = "All expenses are paid \uD83D\uDC4F",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.surfaceTint,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        )
                    }
                }

                errorMessage?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    payExpenseFlow.value?.let {
        when (it) {
            is Resource.Failure -> {
                Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
            }
            Resource.Loading -> {
                isLoading = true
            }
            is Resource.Success -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Expense Paid", Toast.LENGTH_SHORT).show()
                    navController.navigate(
                        "selectedGroup/"+selectedGroup?.groupUid, // Use the route with the parameter
                        builder = {
                            launchSingleTop = true

                            popUpTo(Routes.Home.route) {
                                saveState = true
                            }
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            with(navController.currentBackStackEntry?.arguments) {
                                this?.getString("groupUid")?.let { groupUid ->
                                    putString("groupUid", groupUid) // Provide the groupId here
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    errorMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ExpenseCard(
    modifier: Modifier = Modifier,
    expense: Expense,
    currectUserUid: String,
    expenseCreaterEmail: String,
    viewModel: SelectedGroupViewModel,
    isLoading: Boolean
) {
    val background = if (expense.expensePayer == currectUserUid) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.outline
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.scrim
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 18.dp, bottom = 12.dp, end = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expenseCreaterEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(22.dp),
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }

            if (expense.expenseDesc != "") {
                Text(
                    text = expense.expenseDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(18.dp, 6.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(background)
                    .padding(18.dp, 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (expense.expenseCreator == currectUserUid) {
                    Text(
                        text = "You'll receive",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.surfaceTint
                    )
                } else if (expense.expensePayer == currectUserUid) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "You owe",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.surfaceTint
                            )

                            Text(
                                text = "${expense.amount} DKK",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.surfaceTint
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.getCurrentUser?.uid?.let {
                                    viewModel.payExpense(expenseUid = expense.uid,
                                        expensePayer = it, amount = expense.amount, paid = true)
                                }
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .fillMaxHeight()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    trackColor = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                Text(
                                    text = "PAY",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                } else {
                    Text(
                        text = "Not yours to worry about :D",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.surfaceTint
                    )
                }

                if (expense.expensePayer != currectUserUid || expense.expenseCreator == currectUserUid) {
                    Text(
                        text = "${expense.amount} DKK",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
        }
    }
}
