package com.example.tripapp.ui.feature.trip.plan.edit

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.tripapp.ui.feature.trip.plan.PlanEditScreen
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel

val PLAN_EDIT_ROUTE = "plan_edit"

fun genPlanEditNavigationRoute() = PLAN_EDIT_ROUTE

fun NavGraphBuilder.planEditRoute(navController: NavHostController) {
    composable(
        route = "${PLAN_EDIT_ROUTE}/{sch_no}",
    ) {BackStackEntry ->
        PlanEditScreen(
            navController = navController,
            planEditViewModel = viewModel(),
            planHomeViewModel = viewModel(),
            requestVM = viewModel(),
            schNo = BackStackEntry.arguments?.getString("sch_no").let { it?.toIntOrNull() ?: 0 }
        )
    }
}