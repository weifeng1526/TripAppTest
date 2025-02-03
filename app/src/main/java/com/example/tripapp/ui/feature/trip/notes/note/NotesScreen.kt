package com.example.tripapp.ui.feature.trip.notes.note

import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.example.tripapp.ui.theme.Pink40
import com.example.tripapp.ui.theme.Pink80
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.green100
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.yellow100


@Composable
fun NotesScreen(
    navController: NavController,
    notesViewModel: NotesViewModel,
    dstNo: Int,
    uid: Int,
    dstName: String,
    onBackPress: () -> Unit
) {
    val dstPicForNotes by notesViewModel.imageState.collectAsState()
    val newNotesState by notesViewModel.notesState.collectAsState()
    val localDrText = remember(newNotesState?.drText) {
        mutableStateOf(newNotesState?.drText ?: "")
    }
    var isBackPressed by remember { mutableStateOf(false) }
//    Log.d("save", "isBackPressed: $isBackPressed")
    // 畫面進入時執行
//    Log.d("Top_newNotesState", "NotesState: $newNotesState")
    LaunchedEffect(Unit) {
        notesViewModel.setNotesByApi(dstNo, uid)
        notesViewModel.setImageByApi(dstNo)
//        Log.d("NotesScreen_LaunchedEffect_uid", "NotesState: $uid")
//        Log.d("NotesScreen_LaunchedEffect_newNotesState", "NotesState: $newNotesState")
//        Log.d("NotesScreen_LaunchedEffect_notesViewModel", "dstNo: ${dstNo}")
    }
    val imageBitmap = dstPicForNotes?.dstPic?.let { picBytes ->
        if (picBytes.isNotEmpty()) {
            BitmapFactory.decodeByteArray(picBytes, 0, picBytes.size)?.asImageBitmap()
        } else {
            null
        }
    }
    LaunchedEffect(isBackPressed) {
        if (isBackPressed) {
            // 這段代碼會在返回時執行
//            Log.d("save", "LaunchedEffect triggered on back press")
            newNotesState?.let {
                notesViewModel.updateNotes(it.copy(drText = localDrText.value))
            }
            navController.popBackStack() // 返回上一頁
        }
    }

    BackHandler {
        // 儲存筆記
//        Log.d("save", "BackHandler triggered")
        newNotesState?.let {
            notesViewModel.updateNotes(it.copy(drText = localDrText.value))
        }
        onBackPress() // 返回上一頁
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
                    text = "${dstName}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                if (imageBitmap != null) {
//                    Log.d("PIC", "imageBitmap: $imageBitmap")
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.aaa),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
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
//            Log.d("NotesScreen_OutlinedTextField", "NotesState: $newNotesState")
            OutlinedTextField(
                value = localDrText.value, // 使用本地狀態
                onValueChange = { newText ->
                    localDrText.value = newText // 更新本地狀態
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(color = green100),
                placeholder = { Text(text = "這邊可以輸入文字") },
                singleLine = false,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                // 儲存筆記並返回
//                Log.d("save", "TopBar back button clicked")
                newNotesState?.let {
                    notesViewModel.updateNotes(it.copy(drText = localDrText.value))
                }
                onBackPress()
            }, colors = ButtonDefaults.buttonColors(contentColor = purple100)){
                Text(text = "儲存"
                , color = white100, fontSize = 16.sp)
            }
        }
    }
}

fun saveNote(notes: String) {
//    Log.d("TravelNoteScreen", "Note saved: $notes")
}

@Preview
@Composable
fun NotesScreenPreview() {
    NotesScreen(
        navController = rememberNavController(),
        notesViewModel = NotesViewModel(),
        dstNo = viewModel(),
        uid = viewModel(),
        dstName = viewModel(),
        onBackPress = {}
    )
}

