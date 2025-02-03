package com.example.tripapp.ui.feature.trip.plan.home

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.swithscreen.PlanHomeScreen

val JOIN_PLAN_ROUTE = "joined_plan"

fun genJoinedPlanNavigationRoute() = JOIN_PLAN_ROUTE

fun NavGraphBuilder.joinedPlanRoute(
    navController: NavHostController
) {
    composable(
        route = JOIN_PLAN_ROUTE,
    ) {
        JoinedPlanScreen(
            navController,
            planHomeViewModel = viewModel(),
            requestVM = viewModel()
        )
    }
}