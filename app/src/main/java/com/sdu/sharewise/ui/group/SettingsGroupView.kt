package com.sdu.sharewise.ui.group

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.data.model.Expense
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.ProfileItemComp
import com.sdu.sharewise.ui.components.SettingsClickableComp
import com.sdu.sharewise.ui.components.SettingsGroup

@Composable
fun SettingsGroupView(
    viewModel: SettingsGroupViewModel = hiltViewModel(),
    navController: NavHostController,
    group: Group?
) {
    val group by viewModel.group.observeAsState(Group())

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
                group?.name?.let {
                    SettingsClickableComp(
                        name = "Name",
                        value = it,
                        color = MaterialTheme.colorScheme.primary
                    ) {

                    }
                }
            }

            SettingsGroup(
                name = "",
            ) {
                SettingsClickableComp(
                    name = "Delete Group",
                    value = "",
                    color = MaterialTheme.colorScheme.error
                ) {
                    if (!viewModel.hasUnpaidExpenses()) {
                        viewModel.deleteGroup()
                        navController.navigate(Routes.Home.route) {
                            // Pop up to the home destination and clear the back stack
                            popUpTo(Routes.Home.route) {
                                saveState = false
                            }
                            // Avoid multiple copies of the home destination when navigating up
                            launchSingleTop = true
                        }
                    }
                }
                group?.groupUid?.let {
                    ProfileItemComp(
                        name = "Group ID",
                        value = it,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
