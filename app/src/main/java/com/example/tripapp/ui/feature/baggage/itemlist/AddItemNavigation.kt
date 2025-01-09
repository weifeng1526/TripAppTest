package com.example.tripapp.ui.feature.baggage.itemlist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

/**
 * todo 2-1 將首頁的路由獨立出來
 * */

val ADDITEM_NAVIGATION_ROUTE = "additem"

fun genAddItemNavigationRoute() = ADDITEM_NAVIGATION_ROUTE

fun NavGraphBuilder.addItemScreenRoute(navController: NavHostController) {
    composable(
        route = ADDITEM_NAVIGATION_ROUTE,
    ) {
        AddItemRoute(navController = navController)
    }
}
