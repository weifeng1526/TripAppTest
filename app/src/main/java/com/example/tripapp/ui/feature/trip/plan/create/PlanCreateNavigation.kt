package com.example.tripapp.ui.feature.trip.plan.create

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.swithscreen.PlanCreateScreen
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import java.time.LocalDate

val PLAN_CREATE_ROUTE = "plan_create"

fun genPlanCreateNavigationRoute() = PLAN_CREATE_ROUTE

fun NavGraphBuilder.planCreateRoute(navController: NavHostController) {
    composable(
        route = PLAN_CREATE_ROUTE,
    ) {
        val planCreateViewModel: PlanCreateViewModel = viewModel()
        PlanCreateScreen(
            navController = navController,
            planCreateViewModel = planCreateViewModel,
            requestVM = viewModel()
        )
    }
    composable(
        route = "${PLAN_CREATE_ROUTE}/{isSample}/{sch_no}/{sch_name}/{sch_con}/{sch_cur}",
    ) {
        val planCreateViewModel: PlanCreateViewModel = viewModel()
        planCreateViewModel.setIsSample(true)
        val schNo = it.arguments?.getString("sch_no")?.toInt() ?: 0
        if(schNo == 0) {
            navController.popBackStack(PLAN_HOME_ROUTE, false)
        } else {
            var plan = Plan().apply {
                this.schNo = schNo
                schName = it.arguments?.getString("sch_name") ?: ""
                schCon = it.arguments?.getString("sch_con") ?: ""
                schCur = it.arguments?.getString("sch_cur") ?: ""
                schStart = LocalDate.now().toString()
                schEnd = LocalDate.now().toString()
            }
            Log.d("schNo in create page", "${plan.schNo}")
            planCreateViewModel.setPlanForCreate(plan)
            PlanCreateScreen(
                navController = navController,
                planCreateViewModel = planCreateViewModel,
                requestVM = viewModel()
            )
        }
    }
}