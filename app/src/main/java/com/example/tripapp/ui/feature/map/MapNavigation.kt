package com.example.tripapp.ui.feature.map

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

val MAP_ROUTE = "Map"

fun genMapNavigationRoute() = MAP_ROUTE

fun NavGraphBuilder.mapRoute(navController: NavHostController) {
    composable(
        route = MAP_ROUTE,
    ) {
        MapRoute(navController)
    }
}
