package com.example.tripapp.ui.feature.trip.plan.crew

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.swithscreen.PlanCreateScreen

val PLAN_CREW_ROUTE = "plan_crew"

fun genPlanCrewNavigationRoute() = PLAN_CREW_ROUTE

fun NavGraphBuilder.planCrewRoute(navController: NavHostController) {
    composable(
        route = PLAN_CREW_ROUTE,
    ) {
        PlanCrewScreen(navController)
    }
}