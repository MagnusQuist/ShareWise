package com.sdu.sharewise.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sdu.sharewise.ui.auth.AuthViewModel
import com.sdu.sharewise.ui.auth.LoginView
import com.sdu.sharewise.ui.auth.RegisterView
import com.sdu.sharewise.ui.home.HomeView
import com.sdu.sharewise.ui.intro.IntroView
import com.sdu.sharewise.ui.profile.ProfileView
import com.sdu.sharewise.ui.profile.ProfileViewModel

@Composable
fun ShareWiseNavHost(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Intro.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Intro.route) {
            IntroView(viewModel, navController)
        }
        composable(Routes.Login.route) {
            LoginView(viewModel, navController)
        }
        composable(Routes.Register.route) {
            RegisterView(viewModel, navController)
        }
        composable(Routes.Home.route) {
            HomeView(viewModel, navController)
        }
        composable(Routes.Profile.route) {
            ProfileView(profileViewModel = ProfileViewModel(), navController)
        }
    }
}