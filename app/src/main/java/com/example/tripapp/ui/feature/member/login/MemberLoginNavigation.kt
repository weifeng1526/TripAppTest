package com.example.tripapp.ui.feature.member.login

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

val MEMBER_LOGIN_ROUTE = "Login"

fun genMemberLoginNavigationRoute() = MEMBER_LOGIN_ROUTE

fun NavGraphBuilder.memberLoginRoute(navController: NavHostController) {
    composable(
        route = MEMBER_LOGIN_ROUTE,
    ) {
        MemberLoginRoute(
            navController = navController,
        )
    }
}