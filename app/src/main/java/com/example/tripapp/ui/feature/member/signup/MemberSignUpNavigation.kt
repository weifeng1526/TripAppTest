package com.example.tripapp.ui.feature.member.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

val MEMBER_SIGNUP_ROUTE = "SignUp"

fun genMemberSignUpNavigationRoute() = MEMBER_SIGNUP_ROUTE

fun NavGraphBuilder.memberSignUpRoute(navController: NavHostController) {
    composable(
        route = MEMBER_SIGNUP_ROUTE,
    ) {
        MemberSignUpRoute(navController = navController)
    }
}