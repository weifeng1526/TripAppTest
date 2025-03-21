package com.example.tripapp.ui.feature.trip.plan.crew

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.swithscreen.PlanCreateScreen

val PLAN_CREW_ROUTE = "plan_crew"

fun genPlanCrewNavigationRoute() = PLAN_CREW_ROUTE

fun NavGraphBuilder.planCrewRoute(navController: NavHostController) {
    composable(
        route = "${PLAN_CREW_ROUTE}/{schNo}/{schName}",
    ) {
        PlanCrewScreen(
            navController = navController,
            planCrewViewModel = viewModel(),
            schNo = it.arguments?.getString("schNo").let { it?.toIntOrNull() ?: 0 },
            schName = it.arguments?.getString("schName") ?: ""
        )
    }
}