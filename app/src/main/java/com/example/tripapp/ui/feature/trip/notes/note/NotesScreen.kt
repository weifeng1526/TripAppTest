package com.example.tripapp.ui.feature.trip.notes.note

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.plan.edit.PlanEditViewModel


@Composable
fun NotesDate(
    navController: NavController,
    planEditViewModel: PlanEditViewModel
){
val date by planEditViewModel.dstsState.collectAsState()
}

@Composable
fun NotesScreen(
    navController: NavController,
    destination: Destination
){
    var noteText by remember { mutableStateOf("") } // 更新本地狀態
Column (modifier = Modifier
    .fillMaxSize(1f)
    .background(color = colorResource(R.color.black_200))) {
Column (modifier = Modifier
    .fillMaxWidth(1f)
    .fillMaxHeight(0.3f)){
    Row (modifier = Modifier
        .fillMaxWidth(1f)
        .height(30.dp), Arrangement.Center){
        Text(
            text = "test", fontSize = 24.sp
        )
    }
    Row (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(1f)
            .padding(10.dp)
    ){
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
            text = "旅遊筆記",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = { newText ->
                noteText = newText
                saveNote(newText) // 即時保存筆記內容
                Log.d("Notes", "NotesScreen: $newText")
                Log.d("NotesSave", "NotesScreen: $noteText")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text(text = "這邊可以輸入文字") },
            singleLine = false,
            maxLines = 6
        )
    }
}
}

fun saveNote(notes: String) {
    Log.d("TravelNoteScreen", "Note saved: $notes")
}

@Preview
@Composable
fun NotesScreenPreview(){
    NotesScreen(navController = rememberNavController(), destination = Destination())
}