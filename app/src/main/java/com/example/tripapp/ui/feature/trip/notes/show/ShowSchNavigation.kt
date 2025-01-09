package com.example.tripapp.ui.feature.trip.notes.show

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.plan.edit.PlanEditViewModel
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.restful.RequestVM
import com.example.tripview.show.ShowSchScreen

val SHOW_SCH_ROUTE = "show_sch"

fun genShowSchNavigation () = SHOW_SCH_ROUTE

fun NavGraphBuilder.showSchRoute(navController: NavController) {
    composable(
        route = "$SHOW_SCH_ROUTE/{sch_no}"
//        route = SHOW_SCH_ROUTE
    ) {BackStackEntry ->
        ShowSchScreen(
            navController = navController,
//            schNo = 1
            schNo = BackStackEntry.arguments?.getString("sch_no").let { it?.toIntOrNull() ?: 0 },
            requestVM = RequestVM(),
            planEditViewModel = PlanEditViewModel(),
            planHomeViewModel = PlanHomeViewModel(),
            destination = Destination(),
        )
    }
}