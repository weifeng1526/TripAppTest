package com.example.tripapp.ui.feature.spending.addlist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable


val SPENDING_ADD_ROUTE = "Add"

fun getSpendingAddNavigationRoute() = SPENDING_ADD_ROUTE

fun NavGraphBuilder.spendingAddRoute(navController: NavHostController) {
    composable(
        route = SPENDING_ADD_ROUTE,
    ) {
        SpendingRoute(navController)
    }
}
