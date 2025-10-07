package com.socialconnect.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ChatList : Screen("chat_list")
    object Chat : Screen(
        route = "chat/{userId}",
        arguments = listOf(
            navArgument("userId") {
                type = NavType.IntType
            }
        )
    ) {
        fun createRoute(userId: Int) = "chat/$userId"
    }
    object Profile : Screen("profile")
    object Search : Screen("search")
}

