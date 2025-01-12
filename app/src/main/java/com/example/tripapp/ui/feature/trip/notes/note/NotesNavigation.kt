package com.example.tripapp.ui.feature.trip.notes.note

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

val NOTES_ROUTE = "notes"

fun genShowSchNavigation () = NOTES_ROUTE

fun NavGraphBuilder.notesRoute(navController: NavController) {
    composable(
        route = "$NOTES_ROUTE/{dst_no}/{uid}"
    ) {BackStackEntry ->
val dstNo = BackStackEntry.arguments?.getString("dst_no")?.toIntOrNull() ?: 0
        val uid = BackStackEntry.arguments?.getString("uid")?.toIntOrNull() ?: 0
        Log.d("Navigation", "Navigating to notes with dstNo=$dstNo and uid=$uid")
        if (uid != null) {
            NotesScreen(
                navController = navController,
                notesViewModel = NotesViewModel(),
                dstNo = dstNo,
                uid = uid
            )
        }
    }
}



