package com.sdu.sharewise.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("Login")
    data object Intro : Routes("Intro")
    data object Register : Routes("Register")
    data object Home : Routes("Home")
    data object Profile : Routes("Profile")
    data object CreateGroup : Routes("CreateGroup")
}