package com.sdu.sharewise.ui.group

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.FormFieldText
import com.sdu.sharewise.ui.components.NavigateToHomeButton

@Composable
fun CreateExpenseView(viewModel: CreateExpenseViewModel, navController: NavHostController) {
    var amount by remember { mutableStateOf("") }

    val createExpenseFlow = viewModel.createExpenseFlow.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current.applicationContext

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Create Expsense",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        FormFieldText(
            text = amount,
            placeholder = "Expense amount",
            onChange = {
                amount = it
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.AttachMoney,
                    "Money Icon",
                )
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Decimal,
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Create Button
        Button(
            onClick = {
                // viewModel.createExpense(amount)
                Toast.makeText(context, "Creating Expense", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
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
                    text = "Create",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        createExpenseFlow.value?.let {
            when (it) {
                is Resource.Failure -> {
                    val context = LocalContext.current
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                }
                Resource.Loading -> {
                    isLoading = true
                }
                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Expense Created", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.SelectedGroup.route) {
                            popUpTo("SelectedGroup") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}