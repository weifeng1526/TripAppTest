package com.example.tripapp.ui.feature.spending.addlist

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tripapp.ui.feature.spending.list.SpendingListRoute

// 頁面的種類
val SPENDING_ADD_KEYID = "Add"

// 頁面的參數的key
val SPENDING_ADD_ARGS_SCHNO = "schno"
val SPENDING_EDIT_ARGS_SCHNO = "costno"

// 頁面的真正的路徑
val SPENDING_ADD_ROUTE = "$SPENDING_ADD_KEYID/{$SPENDING_ADD_ARGS_SCHNO}/{$SPENDING_EDIT_ARGS_SCHNO}"

fun getSpendingAddNavigationRoute(schNo: Int, costNo:Int) = "$SPENDING_ADD_KEYID/$schNo/$costNo"

fun NavGraphBuilder.spendingAddRoute(navController: NavHostController) {
    composable(
        route = SPENDING_ADD_ROUTE,
        arguments = listOf(
            navArgument(SPENDING_ADD_ARGS_SCHNO) { type = NavType.IntType },
            navArgument(SPENDING_EDIT_ARGS_SCHNO) { type = NavType.IntType }
        )
    ) {
        val schNo = it.arguments?.getInt(SPENDING_ADD_ARGS_SCHNO, 0) ?: 0
        val costNo = it.arguments?.getInt(SPENDING_EDIT_ARGS_SCHNO, 0) ?: 0
        Log.d("tag","schNo: $schNo, costno $costNo")
        SpendingAddRoute(navHostController = navController, schNo = schNo, costNo=costNo)

    }
}
