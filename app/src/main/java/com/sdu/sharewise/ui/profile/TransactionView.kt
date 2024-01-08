package com.sdu.sharewise.ui.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.R
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.NavigateToHomeButton
import com.sdu.sharewise.ui.components.navigateToHome
import com.sdu.sharewise.ui.home.GroupCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionView(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    // Observe groups from ViewModel
    val transactions by viewModel.transactions.observeAsState(emptyList())

    // Observe error message from ViewModel
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    // Fetch transactions when the composable is first created
    LaunchedEffect(Unit) {
        // Fetch Users transactions
        viewModel.fetchExpenses()
    }

    val context = LocalContext.current.applicationContext

    Surface(
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier.fillMaxSize()
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
                    IconButton(
                        modifier = Modifier
                            .size(22.dp),
                        onClick = {
                            navController.navigate(Routes.Profile.route) {
                                popUpTo(Routes.Home.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the home destination when navigating up
                                launchSingleTop = true
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
                        text = "Transactions",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (transactions.isEmpty()) {
                    item {
                        Text(
                            text = "No transactions yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                } else {
                    itemsIndexed (transactions) { index, _ ->
                        // Display transaction
                        TransactionCard(modifier = Modifier, expense = transactions[index], viewModel = viewModel)

                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                errorMessage?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    modifier: Modifier,
    expense: Expense,
    viewModel: ProfileViewModel
) {
    Box (
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.scrim,
                // rounded corner to match with the OutlinedTextField
                shape = RoundedCornerShape(10.dp)
            ),

    ) {
        Row (
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(9.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(30.dp),
                    imageVector = if (expense.expensePayer == viewModel.getCurrentUser?.uid) Icons.Default.SubdirectoryArrowLeft else Icons.Default.SubdirectoryArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row (
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(
                        text = expense.expenseDesc,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (expense.expensePayer == viewModel.getCurrentUser?.uid) "You paid ${expense.expenseCreator.dropLast(18)}..." else "${expense.expensePayer.dropLast(18)}... paid you",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Column (
                    modifier = modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = convertTimestampToDateFormat(expense.time),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End
                    )
                    Text(
                        modifier = modifier.fillMaxWidth(),
                        text = if (expense.expensePayer == viewModel.getCurrentUser?.uid) "- ${expense.amount},-" else "+ ${expense.amount},-",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (expense.expensePayer == viewModel.getCurrentUser?.uid) Color.Red else Color.Green,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

fun convertTimestampToDateFormat(timestamp: Long): String {
    // Create a SimpleDateFormat object with the desired date format
    val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    // Convert the timestamp to a Date object
    val date = Date(timestamp)

    // Format the Date object to the desired format and return the result
    return sdf.format(date)
}