package com.sdu.sharewise.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.ProfileItemComp
import com.sdu.sharewise.ui.components.SettingsClickableComp
import com.sdu.sharewise.ui.components.SettingsGroup

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .size(46.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                onClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    }
                }) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = Icons.Default.ArrowBack,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Go Back"
                )
            }

            Text(text = viewModel.getCurrentUser?.displayName?: "")
        }

        Spacer(modifier = Modifier.height(32.dp))

        SettingsGroup(
            name = "Profile",
            topBorder = false,
            bottomBorder = false,
        ) {
            ProfileItemComp(
                name = "Name",
                value = viewModel.getCurrentUser?.displayName?: "",
                color = MaterialTheme.colorScheme.primary
            )
            ProfileItemComp(
                name = "E-mail",
                value = viewModel.getCurrentUser?.email?: "",
                color = MaterialTheme.colorScheme.primary
            )
            ProfileItemComp(
                name = "Mobile no.",
                value = "",
                color = MaterialTheme.colorScheme.primary
            )
        }

        SettingsGroup(
            name = "Settings",
            topBorder = false,
            bottomBorder = false,
        ) {
            SettingsClickableComp(
                name = "Notifications",
                color = MaterialTheme.colorScheme.primary
            ) {
                // Navigate to another page
            }
            SettingsClickableComp(
                name = "Transactions",
                color = MaterialTheme.colorScheme.primary
            ) {
                // Navigate to another page
            }
            SettingsClickableComp(
                name = "Sign Out",
                color = MaterialTheme.colorScheme.error
            ) {
                viewModel.logout()
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        }

        SettingsGroup(
            name = "",
            topBorder = true,
            bottomBorder = false,
        ) {
            ProfileItemComp(
                name = "User ID",
                value = viewModel.getCurrentUser?.uid?: "",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}