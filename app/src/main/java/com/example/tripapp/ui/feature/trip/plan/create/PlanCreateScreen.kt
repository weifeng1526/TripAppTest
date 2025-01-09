package com.example.swithscreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.trip.plan.create.PlanCreateViewModel
import com.example.tripapp.ui.feature.trip.plan.edit.PLAN_EDIT_ROUTE
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.dataObjects.getCurrentTimeAsString
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanCreateScreen(
    navController: NavController,
    planCreateViewModel: PlanCreateViewModel,
    requestVM: RequestVM
) {
    //選照片
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    /* 呼叫rememberLauncherForActivityResult並搭配PickVisualMedia()
    以建立可以啟用photo picker的launcher物件。
    照片挑選完畢會執行onResult並傳來該照片的URI供後續處理 */
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )
    var coroutineScope = rememberCoroutineScope()
    val plan by planCreateViewModel.planForCreateteState.collectAsState()
    val isSample by planCreateViewModel.isSampleState.collectAsState()
    //行程名稱
    var planName by remember { mutableStateOf("") }
    //前往國家
    val contries = listOf("Taiwan", "Japan")
    var selectedContry by remember { mutableStateOf("") }
    var expandContries by remember { mutableStateOf(false) }
    var indexOfContry = contries.indexOf(selectedContry)

    //幣別
    val currencies = listOf("TWD", "JPY")
    var currency = if (indexOfContry != -1) currencies[indexOfContry] else ""

    //行程日期
    var initStartDate = "1970-01-01"
    var initEndDate = "1970-01-01"
    if (isSample) {
        plan.apply {
            planName = schName
            selectedContry = schCon
            currency = schCur
            expandContries = false
            initStartDate = schStart
            initEndDate = schEnd
        }
    }
    var dateRangePickerState = rememberDateRangePickerState()
    var expandDateRangePickerDialog by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf(initStartDate) }
    var selectedEndDate by remember { mutableStateOf(initEndDate) }
    var concatDate = if (selectedStartDate.isNotEmpty() && selectedEndDate.isNotEmpty()) {
        "${selectedStartDate} ~ ${selectedEndDate}"
    } else ""

    //第一層
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        //圖片
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(250.dp)
//                .padding(bottom = 20.dp)
//                .background(Color.LightGray),
//        ) {
//            Box(
//                modifier = Modifier
//            ) {
//                Image(
//                    modifier = Modifier
//                        .padding(6.dp)
//                        .fillMaxSize()
//                        .clip(RoundedCornerShape(8.dp)),
//                    painter = if (selectedImageUri != null) rememberAsyncImagePainter(
//                        selectedImageUri
//                    ) else painterResource(id = R.drawable.dst),
//                    contentDescription = "dstt description",
//                    contentScale = ContentScale.Crop
//                )
//                IconButton(
//                    onClick = {
//                        pickImageLauncher.launch(
//                            PickVisualMediaRequest(
//                                // 設定只能挑選圖片
//                                ActivityResultContracts.PickVisualMedia.ImageOnly
//                            )
//                        )
//                    },
//                    modifier = Modifier
//                        .size(50.dp)
//                        .align(Alignment.BottomEnd)
//                        .padding(10.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.add_photo),
//                        contentDescription = "Add Icon",
//                        modifier = Modifier
//                            .size(48.dp)
//                            .background(Color.White),
//                        tint = Color.Unspecified
//                    )
//                }
//            }
//        }
        //行程名稱
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "行程名稱",
                style = TextStyle(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = planName,
                onValueChange = { planName = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            //前往國家
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                )
            ) {
                Text(
                    text = "前往國家",
                    style = TextStyle(
                        fontSize = 24.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = expandContries,
                    onExpandedChange = { expandContries = it }
                ) {
                    TextField(
                        value = selectedContry,
                        readOnly = true,
                        maxLines = 1,
                        onValueChange = {},
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.drop_down),
                                contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = Color.Unspecified
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                MenuAnchorType.PrimaryEditable,
                                true
                            )
                    )
                    ExposedDropdownMenu(
                        expanded = expandContries,
                        onDismissRequest = { expandContries = false }
                    ) {
                        contries.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    selectedContry = it
                                    expandContries = false
                                }
                            )
                        }
                    }
                }
            }
            //幣別
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                )
            ) {
                Text(
                    text = "幣別",
                    style = TextStyle(
                        fontSize = 24.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = currency,
                    readOnly = true,
                    maxLines = 1,
                    onValueChange = {},
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.drop_down),
                            contentDescription = "",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "行程日期",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = concatDate,
                    placeholder = {
                        Text(
                            text = if (concatDate.isEmpty()) {
                                "出發日期 -> 結束日期"
                            } else "",
                            maxLines = 1
                        )
                    },
                    //搭配clickable必須enabled = false
                    //不監聽與響應使用者是否輸入
                    enabled = false,
                    readOnly = false,
                    singleLine = true,
                    onValueChange = {},
                    textStyle = TextStyle(color = Color.Black),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.date_range),
                            contentDescription = "",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandDateRangePickerDialog = true },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                //取消
                Button(onClick = { navController.popBackStack(PLAN_HOME_ROUTE, false) }) { }
                //確定
                Button(
                    onClick = {
                        val newPlan = Plan(
                            schNo = 0,
                            memNo = 1,
                            schState = 0,
                            schName = planName,
                            schCon = selectedContry,
                            schCur = currency,
                            schStart = selectedStartDate,
                            schEnd = selectedEndDate,
                            schPic = ByteArray(0),
                            schLastEdit = getCurrentTimeAsString()
                        )
                        coroutineScope.run {
                            launch {
                                var planResponse: Plan? = null
                                launch {
                                    planResponse = requestVM.CreatePlan(newPlan)
                                }.join()
                                planResponse?.let {
                                    //在create時0是為了自動編號，當有response，要把編號後的值抓回來
                                    Log.d("plan created autonumber", "${it.schNo}")
                                    navController.navigate("${PLAN_EDIT_ROUTE}/${it.schNo}")
                                }
                            }
                        }
                    }
                ) { }
            }
        }
        if (expandDateRangePickerDialog) {
            ShowDateRangePikerDialog(
                dateRangePickerState = dateRangePickerState,
                onDismissRequest = { expandDateRangePickerDialog = false },
                dismissButton = {
                },
                confirmButton = {
                    Button(
                        onClick = {
                            expandDateRangePickerDialog = false
                            var instant: Instant
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            dateRangePickerState.selectedStartDateMillis?.let {
                                instant = Instant.ofEpochMilli(it)
                                selectedStartDate =
                                    formatter.format(instant.atZone(ZoneId.systemDefault()))
                            }
                            dateRangePickerState.selectedEndDateMillis?.let {
                                instant = Instant.ofEpochMilli(it)
                                selectedEndDate =
                                    formatter.format(instant.atZone(ZoneId.systemDefault()))
                            }
                        }
                    ) {
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDateRangePikerDialog(
    dateRangePickerState: DateRangePickerState,
    onDismissRequest: () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = dismissButton,
        confirmButton = confirmButton
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = { Text(text = "") },
            headline = {
            }
        )
    }
}

@Composable
fun SelectImageFromGallery() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    /* 呼叫rememberLauncherForActivityResult並搭配PickVisualMedia()
        以建立可以啟用photo picker的launcher物件。
        照片挑選完畢會執行onResult並傳來該照片的URI供後續處理 */
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        pickImageLauncher.launch(
//            PickVisualMediaRequest(
//                // 設定只能挑選圖片
//                ActivityResultContracts.PickVisualMedia.ImageOnly
//            )
//        )
//    }
//
//    Spacer(modifier = Modifier.height(16.dp))
//
//    // 當照片被選取則uri不為null，就將該照片顯示
//    selectedImageUri?.let { uri ->
//        Image(
//            painter = rememberAsyncImagePainter(uri),
//            contentDescription = null,
//            modifier = Modifier
//                .size(200.dp)
//                .clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//    }
}


@Preview
@Composable
fun PreviewPlanCreateScreen() {
    PlanCreateScreen(
        rememberNavController(),
        planCreateViewModel = viewModel(),
        requestVM = viewModel()
    )
}