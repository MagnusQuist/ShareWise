package com.sdu.sharewise.ui.profile

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.ProfileItemComp
import com.sdu.sharewise.ui.components.SettingsClickableComp
import com.sdu.sharewise.ui.components.SettingsGroup
import com.sdu.sharewise.ui.components.SettingsSwitchComp

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current.applicationContext

    LaunchedEffect(null) {
        viewModel.getCurrentUser?.uid?.let { viewModel.fetchUser(it) }
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
                        text = "Profile",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SettingsGroup(
                name = "",
            ) {
                viewModel.user.value?.let {
                    SettingsClickableComp(
                        name = "Name",
                        value = it.name,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        navController.navigate(Routes.ProfileName.route)
                        // Navigate to another page
                    }
                }

                viewModel.user.value?.let {
                    SettingsClickableComp(
                        name = "E-mail",
                        value = it.email,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        navController.navigate(Routes.ProfileEmail.route)
                    }
                }
                viewModel.user.value?.phone?.let {
                    SettingsClickableComp(
                        name = "Mobile no.",
                        value = it,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        navController.navigate(Routes.ProfilePhone.route)
                        // Navigate to another page
                    }
                }
            }

            SettingsGroup(
                name = "",
            ) {
                SettingsSwitchComp(
                    name = "Notifications",
                    state = viewModel.isSwitchOn.collectAsState(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    viewModel.toggleSwitch()
                }
                SettingsClickableComp(
                    name = "Transactions",
                    value = "",
                    color = MaterialTheme.colorScheme.primary
                ) {
                    // Navigate to another page
                    navController.navigate(Routes.Transactions.route)
                }
            }

            SettingsGroup(
                name = "",
            ) {
                SettingsClickableComp(
                    name = "Sign Out",
                    value = "",
                    color = MaterialTheme.colorScheme.error
                ) {
                    viewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
                ProfileItemComp(
                    name = "User ID",
                    value = viewModel.getCurrentUser?.uid?: "",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}