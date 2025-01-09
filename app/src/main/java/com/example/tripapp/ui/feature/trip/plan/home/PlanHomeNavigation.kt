package com.example.tripapp.ui.feature.trip.plan.home

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.swithscreen.PlanHomeScreen

val PLAN_HOME_ROUTE = "plan_home"

fun genPlanHomeNavigationRoute() = PLAN_HOME_ROUTE

fun NavGraphBuilder.planHomeRoute(
    navController: NavHostController
) {
    composable(
        route = PLAN_HOME_ROUTE,
    ) {
        PlanHomeScreen(
            navController = navController,
            planHomeViewModel = viewModel(),
            requestVM = viewModel()
        )
    }
}