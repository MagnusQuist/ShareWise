package com.sdu.sharewise.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sdu.sharewise.navigation.Routes

@Composable
fun NavigateToHomeButton(navController: NavController) {
    IconButton(
        modifier = Modifier
            .size(22.dp),
        onClick = {
            navigateToHome(navController)
        }) {
        Icon(
            modifier = Modifier
                .size(22.dp),
            imageVector = Icons.Default.ArrowBackIos,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Go Back"
        )
    }
}

fun navigateToHome(navController: NavController) {
    navController.navigate(Routes.Home.route) {
        // Pop up to the home destination and clear the back stack
        popUpTo(Routes.Home.route) {
            saveState = true
        }
        // Avoid multiple copies of the home destination when navigating up
        launchSingleTop = true
    }
}