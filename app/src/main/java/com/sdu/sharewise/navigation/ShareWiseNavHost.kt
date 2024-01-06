package com.sdu.sharewise.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.sdu.sharewise.ui.auth.AuthViewModel
import com.sdu.sharewise.ui.auth.LoginView
import com.sdu.sharewise.ui.auth.RegisterView
import com.sdu.sharewise.ui.group.CreateExpenseView
import com.sdu.sharewise.ui.group.CreateExpenseViewModel
import com.sdu.sharewise.ui.group.CreateGroupView
import com.sdu.sharewise.ui.group.CreateGroupViewModel
import com.sdu.sharewise.ui.group.SelectedGroupView
import com.sdu.sharewise.ui.group.SelectedGroupViewModel
import com.sdu.sharewise.ui.home.HomeView
import com.sdu.sharewise.ui.home.HomeViewModel
import com.sdu.sharewise.ui.intro.IntroView
import com.sdu.sharewise.ui.profile.ProfileView
import com.sdu.sharewise.ui.profile.ProfileNameView
import com.sdu.sharewise.ui.profile.ProfileEmailView
import com.sdu.sharewise.ui.profile.ProfilePhoneView
import com.sdu.sharewise.ui.profile.ProfileViewModel

@Composable
fun ShareWiseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    val isFirstLaunch = rememberSaveable { mutableStateOf(true) }
    val startDestination = if (isFirstLaunch.value) {
        Routes.Intro.route
    } else {
        Routes.Home.route
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Intro.route) {
            IntroView(navController) {
                isFirstLaunch.value = false
                navController.navigate(Routes.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }
        }

        navigation(
            startDestination = Routes.Login.route,
            route = "auth"
        ) {
            composable(Routes.Login.route) {
                val viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)
                LoginView(viewModel, navController)
            }
            composable(Routes.Register.route) {
                val viewModel = it.sharedViewModel<AuthViewModel>(navController = navController)
                RegisterView(viewModel, navController)
            }
        }

        navigation(
            startDestination = Routes.Home.route,
            route = "home"
        ) {
            composable(Routes.Home.route) {
                if (isAuthenticated) {
                    val viewModel = hiltViewModel<HomeViewModel>()
                    HomeView(viewModel, navController)
                } else {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }

            }
            composable(Routes.CreateGroup.route) {
                val viewModel = hiltViewModel<CreateGroupViewModel>()
                CreateGroupView(viewModel, navController)
            }
            composable(Routes.Profile.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()
                ProfileView(viewModel, navController)
            }
            composable(Routes.ProfileName.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()
                ProfileNameView(viewModel, navController)
            }
            composable(Routes.ProfileEmail.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()
                ProfileEmailView(viewModel, navController)
            }
            composable(Routes.ProfilePhone.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()
                ProfilePhoneView(viewModel, navController)
            }

            navigation(
                startDestination = Routes.SelectedGroup.route,
                route = "selectedGroup/{groupUid}"
            ) {
                composable(Routes.SelectedGroup.route) {
                    val viewModel = hiltViewModel<SelectedGroupViewModel>()
                    SelectedGroupView(viewModel, navController)
                }

                composable(Routes.CreateExpense.route) {
                    val viewModel = hiltViewModel<CreateExpenseViewModel>()
                    CreateExpenseView(viewModel, navController)
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}