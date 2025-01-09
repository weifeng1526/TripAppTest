package com.example.tripapp.ui.feature.trip.plan.alter

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

val PLAN_ALTER_ROUTE = "plan_alter"
fun genPlanAlterNavigationRoute() = PLAN_ALTER_ROUTE

fun NavGraphBuilder.planAlterRoute(navController: NavHostController) {
    composable(
        route = "${PLAN_ALTER_ROUTE}/{sch_no}",

    ) {

        PlanAlterScreen(
            navController = navController,
            planHomeViewModel = viewModel(),
            requestVM = viewModel(),
            schNo = it.arguments?.getString("sch_no")?.toIntOrNull() ?: 0
        )
    }
}