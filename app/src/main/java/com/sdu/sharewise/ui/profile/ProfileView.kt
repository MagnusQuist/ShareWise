package com.sdu.sharewise.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sdu.sharewise.ui.auth.AuthViewModel
import com.sdu.sharewise.ui.components.SettingsClickableComp
import com.sdu.sharewise.ui.components.SettingsSwitchComp

@Composable
fun ProfileView(
    profileViewModel: ProfileViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
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
            state = profileViewModel.isSwitchOn.collectAsState()
        ) {
            // call ViewModel to toggle the value
            profileViewModel.toggleSwitch()
        }
    }
}