package com.example.tripapp.ui.feature.shop

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable



val SHOP_ROUTE = "shop"

fun genShopNavigationRoute() = SHOP_ROUTE

fun NavGraphBuilder.shopRoute(navController: NavHostController) {
    composable(
        route = SHOP_ROUTE,
    ) {
        ShopRoute()
    }
}