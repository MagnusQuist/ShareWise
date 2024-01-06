package com.sdu.sharewise.ui.group

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.NavigateToHomeButton

@Composable
fun SelectedGroupView (
    viewModel: SelectedGroupViewModel,
    navController: NavHostController
) {
    val selectedGroup by viewModel.selectedGroup.observeAsState(Group())

    // Observe error message from ViewModel
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    // Fetch groups when the composable is first created
    val groupUid = navController.currentBackStackEntry?.arguments?.getString("groupUid").orEmpty()

    LaunchedEffect(groupUid) {
        viewModel.fetchGroup(groupUid)
    }

    val context = LocalContext.current.applicationContext

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
                    NavigateToHomeButton(navController = navController)

                    selectedGroup?.let {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = it.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Add Expense Button
            Button(
                onClick = {
                    navController.navigate(Routes.CreateExpense.route) {
                        popUpTo(Routes.SelectedGroup.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Create Expense",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    errorMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
