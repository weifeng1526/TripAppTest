package com.example.tripapp.ui.feature.spending.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

//path:Spending
val SPENDING_LIST_ROUTE = "Spending"

fun genSpendingNavigationRoute() = SPENDING_LIST_ROUTE

fun NavGraphBuilder.spendingListRoute(navController: NavHostController) {
    composable(
        route = SPENDING_LIST_ROUTE,
    ) {
        SpendingListRoute(navController)
    }
}


