package com.example.tripapp.ui.feature.spending.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable


val SPENDING_SET_ROUTE = "SettingContent"

fun genSpendingNavigationRoute() = SPENDING_SET_ROUTE

fun NavGraphBuilder.spendingSetRoute(navController: NavHostController) {
    composable(
        route = SPENDING_SET_ROUTE,
    ) {
        SpendingRoute(navController)
    }
}
