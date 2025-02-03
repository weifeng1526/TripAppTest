package com.example.tripapp.ui.feature.baggage.itemlist

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tripapp.ui.feature.baggage.baglist.BAG_NAVIGATION_ROUTE
import com.example.tripapp.ui.feature.baggage.baglist.BagRoute

/**
 * todo 2-1 將首頁的路由獨立出來
 * */

val ADDITEM_NAVIGATION_ROUTE = "additem"

fun genAddItemNavigationRoute() = ADDITEM_NAVIGATION_ROUTE

//fun NavGraphBuilder.addItemScreenRoute(navController: NavHostController) {
//    composable(
//        route = ADDITEM_NAVIGATION_ROUTE,
//    ) {
//        AddItemRoute(navController = navController)
//    }
//}

fun NavGraphBuilder.addItemScreenRoute(navController: NavHostController) {
    composable(
        route = "$ADDITEM_NAVIGATION_ROUTE/{schNo}",
        arguments = listOf(navArgument("schNo") { type = NavType.IntType })
    ) { backStackEntry ->
        val schNo = backStackEntry.arguments?.getInt("schNo") ?: 0 // 使用 getInt 獲取 schNo 參數
        Log.d("AddItemScreenRoute", "schNo: $schNo") // 輸出 schNo 的值到 Logcat
        AddItemRoute(
            navController = navController,
            schNo = schNo // 傳遞 schNo 參數
        )
    }
}