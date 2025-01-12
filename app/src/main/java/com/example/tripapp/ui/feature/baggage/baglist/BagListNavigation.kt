package com.example.tripapp.ui.feature.baggage.baglist

import android.util.Log
import androidx.fragment.app.FragmentManager.BackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlin.math.log

/**
 * todo 2-1 將首頁的路由獨立出來
 * */

val BAG_NAVIGATION_ROUTE = "bag"

fun genBagNavigationRoute() = BAG_NAVIGATION_ROUTE


fun NavGraphBuilder.bagListScreenRoute(navController: NavHostController) {
    composable(
        route = BAG_NAVIGATION_ROUTE,
    ) {
        BagRoute(
            navController = navController,
            schNo = null
        )
    }
}


fun NavGraphBuilder.bagListScreenRouteSelected(navController: NavHostController) {
    composable(
        route = "$BAG_NAVIGATION_ROUTE/{sch_no}",
        arguments = listOf(navArgument("sch_no") { type = NavType.IntType })
    ) {
        Log.d("BagRoute", "schNo: ${it.arguments?.getInt("sch_no")}")
        BagRoute(
            navController = navController,
            schNo = it.arguments?.getInt("sch_no"),
        )
    }
}


