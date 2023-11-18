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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.SettingsClickableComp
import com.sdu.sharewise.ui.components.SettingsSwitchComp

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
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape),
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

        SettingsClickableComp(
            icon = Icons.Default.Shield,
            iconDesc = "Name",
            name = "Change Name"
        ) {
            // Navigate to another page
        }
        SettingsSwitchComp(
            name = "Notifications",
            icon = Icons.Outlined.Notifications,
            iconDesc = "Enable push notifications",
            // value is collected from StateFlow - updates the UI on change
            state = viewModel.isSwitchOn.collectAsState()
        ) {
            // call ViewModel to toggle the value
            viewModel.toggleSwitch()
        }

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Logout",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}