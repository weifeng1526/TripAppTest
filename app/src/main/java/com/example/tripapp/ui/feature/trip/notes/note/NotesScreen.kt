package com.example.tripapp.ui.feature.trip.notes.note

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.dataObjects.Notes


@Composable
fun NotesScreen(
    navController: NavController,
    notesViewModel: NotesViewModel,
    dstNo: Int,
    uid: Int
) {
    val newNotesState by notesViewModel.notesState.collectAsState()
    val context = LocalContext.current
    // 畫面進入時執行
    LaunchedEffect(newNotesState) {
        notesViewModel.setNotesByApi(dstNo, uid)
        Log.d("NotesScreen", "NotesState: $uid")
        Log.d("NotesScreen", "NotesState: $newNotesState")
        Log.d("notesViewModel", "dstNo: ${dstNo}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.black_200))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "旅遊筆記",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.aaa),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "旅遊筆記內容",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newNotesState?.drText ?: "",
                onValueChange = { newText ->
                    if (newNotesState!=null){
                        notesViewModel.updateNotes(
                            newNotesState?.copy(drText = newText) ?: Notes()
                            // 使用新內容更新資料庫
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text(text = "這邊可以輸入文字") },
                singleLine = false,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }
    }
}


fun saveNote(notes: String) {
    Log.d("TravelNoteScreen", "Note saved: $notes")
}

@Preview
@Composable
fun NotesScreenPreview() {
    NotesScreen(
        navController = rememberNavController(),
        notesViewModel = NotesViewModel(),
        dstNo = viewModel(),
        uid = viewModel()
    )
}