package com.example.catalist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.catalist.cats.details.catDetailsScreen
import com.example.catalist.cats.list.catsListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController();

    NavHost(
        navController = navController,
        startDestination = "cats"
    ) {

        catsListScreen(
            route = "cats",
            navController = navController
        )

        catDetailsScreen(
            route = "cats/{id}",
            navController = navController,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
            })
        )
    }
}

