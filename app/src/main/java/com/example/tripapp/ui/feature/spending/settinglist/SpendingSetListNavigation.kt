package com.example.tripapp.ui.feature.spending.settinglist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable


val SPENDING_SETLIST_ROUTE = "SettingList"

fun genSpendingNavigationRoute() =SPENDING_SETLIST_ROUTE

fun NavGraphBuilder.spendingSetListRoute(navController: NavHostController) {
    composable(
        route = SPENDING_SETLIST_ROUTE
    ) {
        SpendingRoute(navController)
    }
}
