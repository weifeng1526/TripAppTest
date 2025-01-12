package com.example.tripapp.ui.feature.trip.plan.crew


import MemberInviteScreen
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.swithscreen.PlanCreateScreen

val MEMBER_INVITE_ROUTE = "member_invite"

fun genMemberInviteNavigationRoute() = MEMBER_INVITE_ROUTE

fun NavGraphBuilder.memberInviteRoute(navController: NavHostController) {
    composable(
        route = "${MEMBER_INVITE_ROUTE}/{schNo}/{schName}",
    ) {
        MemberInviteScreen(
            navController = navController,
            memberInviteViewModel = viewModel(),
            planCrewViewModel = viewModel(),
            schNo = it.arguments?.getString("schNo")?.toInt() ?: 0,
            schName = it.arguments?.getString("schName") ?: ""
        )
    }
}