package com.example.tripapp.ui.feature.member.home.tabs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

val NOTIFY_ROUTE = "Notify"

fun genNotifyNavigationRoute() = NOTIFY_ROUTE

fun NavGraphBuilder.notifyRoute(navController: NavHostController) {
    composable(
        route = NOTIFY_ROUTE,
    ) {
        NotifyRoute()
    }
}
//
//val HOME_BAG_ROUTE = "Bag"
//
//fun genHomeBagNavigationRoute() = HOME_BAG_ROUTE
//
//fun NavGraphBuilder.HomeBagRoute(navController: NavHostController) {
//    composable(
//        route = HOME_BAG_ROUTE,
//    ) {
//        HomeBagRoute()
//    }
//}
