package com.example.tripapp.ui.feature.member.turfav

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

val TUR_FAV_ROUTE = "TurFav"

fun genTurFavNavigationRoute() = TUR_FAV_ROUTE

fun NavGraphBuilder.turFavRoute(navController: NavHostController) {
    composable(
        route = TUR_FAV_ROUTE,
    ) {
        TurFavRoute(navController)
    }
}