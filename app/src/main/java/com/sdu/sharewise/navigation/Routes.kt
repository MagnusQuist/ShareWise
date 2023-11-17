package com.sdu.sharewise.navigation

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object Intro : Routes("Intro")
    object Register : Routes("Register")
    object Home : Routes("Home")
}