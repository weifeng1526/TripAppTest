package com.example.tripapp.ui.feature.spending.deposit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

// 路徑名稱 enum
val SPENDING_DEPOSIT_ROUTE = "Deposit"

fun getSpendingDepositNavigationRoute() = SPENDING_DEPOSIT_ROUTE

fun NavGraphBuilder.spendingDepositRoute(navController: NavHostController) {
    composable(
        // 路徑比對
        route = SPENDING_DEPOSIT_ROUTE,
    ) {
        // 定義函式寫在頁面裡面
        SpendingRoute(navController)
    }
}
