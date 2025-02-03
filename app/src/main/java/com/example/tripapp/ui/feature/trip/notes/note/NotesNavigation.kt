package com.example.tripapp.ui.feature.trip.notes.note

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.ui.feature.trip.plan.edit.PlanEditViewModel

val NOTES_ROUTE = "notes"

fun genShowSchNavigation () = NOTES_ROUTE

fun NavGraphBuilder.notesRoute(navController: NavController) {
    composable(
        route = "$NOTES_ROUTE/{dst_no}/{uid}/{dst_name}"
    ) {BackStackEntry ->
val dstNo = BackStackEntry.arguments?.getString("dst_no")?.toIntOrNull() ?: 0
        val uid = BackStackEntry.arguments?.getString("uid")?.toIntOrNull() ?: 0
        val dstName = BackStackEntry.arguments?.getString("dst_name") ?: ""
        Log.d("Navigation", "Navigating to notes with dstNo=$dstNo and uid=$uid")
        if (uid != null) {
            NotesScreen(
                navController = navController,
                notesViewModel = viewModel(),
                dstNo = dstNo,
                uid = uid,
                dstName = dstName,
                onBackPress = { navController.popBackStack() }
            )
        }
    }
}



