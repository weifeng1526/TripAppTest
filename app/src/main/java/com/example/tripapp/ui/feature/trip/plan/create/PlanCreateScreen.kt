package com.example.swithscreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerDefaults.DateRangePickerHeadline
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.trip.plan.create.PlanCreateViewModel
import com.example.tripapp.ui.feature.trip.plan.edit.PLAN_EDIT_ROUTE
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.dataObjects.getCurrentTimeAsString
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.restful.RequestVM
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.purple500
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white200
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanCreateScreen(
    navController: NavController,
    planCreateViewModel: PlanCreateViewModel,
    planHomeViewModel: PlanHomeViewModel,
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

    val memNo = GetUid(MemberRepository)
    var coroutineScope = rememberCoroutineScope()
    val plan by planCreateViewModel.planForCreateteState.collectAsState()
    val isSample by planCreateViewModel.isSampleState.collectAsState()
    //行程名稱
    var planName by remember { mutableStateOf("") }
    //前往國家
    val contries = listOf("台灣", "日本")
    var selectedContry by remember { mutableStateOf("") }
    var expandContries by remember { mutableStateOf(false) }
    var indexOfContry = contries.indexOf(selectedContry)

    //幣別
    val currencies = listOf("TWD", "JPY")
    var currency = if (indexOfContry != -1) currencies[indexOfContry] else ""

    //行程日期
    var initStartDate = LocalDate.now().toString()
    var initEndDate = LocalDate.now().toString()
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

    val context = LocalContext.current

    var dateRangePickerState = rememberDateRangePickerState()
    var expandDateRangePickerDialog by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf(initStartDate) }
    var selectedEndDate by remember { mutableStateOf(initEndDate) }
    var concatDate = if (selectedStartDate.isNotEmpty() && selectedEndDate.isNotEmpty()) {
        "${selectedStartDate} ~ ${selectedEndDate}"
    } else ""

    val scrollState = rememberScrollState()
    //第一層
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //建立一個監聽器(Metirial3提供)
        val interactionSource = remember { MutableInteractionSource() }
        // 記錄按鈕的按下狀態
        val isPressed by interactionSource.collectIsPressedAsState()

        val buttonColor by animateColorAsState(
            targetValue = if (isPressed) white400 else purple200
        )
        //圖片
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, purple500, shape = RoundedCornerShape(8.dp))
                .background(white200),
        ) {
            Box(
                modifier = Modifier
            ) {
                Image(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    painter = if (selectedImageUri != null) rememberAsyncImagePainter(
                        selectedImageUri
                    ) else painterResource(id = R.drawable.aaa),
                    contentDescription = "default",
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.BottomEnd)
                        .clip(RectangleShape)
                        .padding(12.dp)
                        .background(white100)
                        .clickable {
                            pickImageLauncher.launch(
                                PickVisualMediaRequest(
                                    // 設定只能挑選圖片
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_box),
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(40.dp),
                        tint = purple200
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .background(white200),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 2.dp)
                    .background(white400),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                )
            ) {
                Text(
                    text = "行程名稱",
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 2.dp)
                )
                TextField(
                    value = planName,
                    onValueChange = { planName = it },
                    textStyle = TextStyle(fontSize = 18.sp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = white100,
                        focusedContainerColor = white100,
                        focusedIndicatorColor = purple100,
                        unfocusedIndicatorColor = purple100,
                    ),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
            }

            //前往國家
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 2.dp)
                    .background(white400),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                )
            ) {
                Text(
                    text = "前往國家",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 2.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expandContries,
                    onExpandedChange = { expandContries = it }
                ) {
                    TextField(
                        value = selectedContry,
                        textStyle = TextStyle(fontSize = 18.sp),
                        readOnly = true,
                        maxLines = 1,
                        onValueChange = {},
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.drop_down),
                                contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = purple500
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = white100,
                            focusedContainerColor = white100,
                            focusedIndicatorColor = purple100,
                            unfocusedIndicatorColor = purple100,
                        ),
                        shape = RectangleShape,
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
                                text = { Text(text = it, style = TextStyle(fontSize = 18.sp)) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 2.dp)
                    .background(white400),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                )
            ) {
                Text(
                    text = "幣別",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 2.dp)
                )
                TextField(
                    value = currency,
                    textStyle = TextStyle(fontSize = 18.sp),
                    readOnly = true,
                    maxLines = 1,
                    onValueChange = {},
                    trailingIcon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.drop_down),
//                            contentDescription = "",
//                            modifier = Modifier.size(30.dp),
//                            tint = Color.Unspecified
//                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = white100,
                        focusedContainerColor = white100,
                        focusedIndicatorColor = purple100,
                        unfocusedIndicatorColor = purple100,
                    ),
                    shape = RectangleShape,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 2.dp)
                    .background(white400),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 2.dp)
                        .background(white400)
                )
                TextField(
                    value = concatDate,
                    placeholder = {
                        Text(
                            text = if (concatDate.isEmpty()) {
                                "出發日期 -> 結束日期"
                            } else "",
                            style = TextStyle(fontSize = 18.sp),
                            maxLines = 1
                        )
                    },
                    //搭配clickable必須enabled = false
                    //不監聽與響應使用者是否輸入
                    enabled = false,
                    readOnly = false,
                    singleLine = true,
                    onValueChange = {},
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.date_range),
                            contentDescription = "",
                            modifier = Modifier.size(30.dp),
                            tint = purple200
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = white100,
                        focusedContainerColor = white100,
                        focusedIndicatorColor = purple100,
                        unfocusedIndicatorColor = purple100,
                        disabledIndicatorColor = purple100,
                        disabledContainerColor = white100
                    ),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandDateRangePickerDialog = true },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //取消
            Button(
                onClick = {
                    navController.popBackStack(
                        PLAN_HOME_ROUTE,
                        false
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                ),
            )
            {
                Text(
                    text = "取消",
                    fontSize = 20.sp
                )
            }
            //確定
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                ),
                onClick = {
                    val newPlan = Plan(
                        schNo = 0,
                        memNo = memNo,
                        schState = 1,
                        schName = planName,
                        schCon = selectedContry,
                        schCur = currency,
                        schStart = selectedStartDate,
                        schEnd = selectedEndDate,
                        schPic = ByteArray(0),
                        schLastEdit = getCurrentTimeAsString()
                    )

                    val imagePart = selectedImageUri?.let { uri ->
                        Log.d("image", "Selected image URI: $uri")
                        context.contentResolver.openInputStream(uri)?.readBytes()
                            ?.let { byteArray ->
                                // 獲取圖片格式
                                val mimeType =
                                    context.contentResolver.getType(uri) ?: "image/jpeg"
                                val imageRequestBody =
                                    byteArray.toRequestBody(mimeType.toMediaTypeOrNull())
                                Log.d("image", "RequestBody created with MIME type: $mimeType")
                                MultipartBody.Part.createFormData(
                                    "image",
                                    "image.${mimeType.substringAfter("/")}",
                                    imageRequestBody
                                )
                            }
                    }  ?: run {
                        // 當 imageUri 為 null 時，使用預設圖片
                        val defaultImage = BitmapFactory.decodeResource(context.resources, R.drawable.aaa)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        defaultImage.compress(Bitmap.CompressFormat.JPEG, 2, byteArrayOutputStream)
                        val defaultImageByteArray = byteArrayOutputStream.toByteArray()

                        val mimeType = "image/jpeg"  // 預設圖片格式
                        val imageRequestBody = defaultImageByteArray.toRequestBody(mimeType.toMediaTypeOrNull())
                        Log.d("image", "Using default image")

                        MultipartBody.Part.createFormData(
                            "image",
                            "aaa.jpg",  // 預設圖片名稱
                            imageRequestBody
                        )
                    }
                    planCreateViewModel.createPlanWithCrewByApi(newPlan) { responseId ->
                        if (responseId > 0) {
                            planHomeViewModel.updatePlanImage(responseId, imagePart)
                            navController.navigate("${PLAN_EDIT_ROUTE}/${responseId}")
                        } else
                            Log.d("Not Result", "Created Plan ID: $responseId")
                    }
//                        planCreateViewModel.createPlanWithCrewByApi(newPlan) { responseId ->
//                            if(responseId > 0) {
//                                val putIdPart =
//                                    responseId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//                                Log.d("putIdPart", "${putIdPart}")
//                                planHomeViewModel.updatePlanImage(putIdPart, imagePart)
//                                navController.navigate("${PLAN_EDIT_ROUTE}/${responseId}")
//                            }
//                            else
//                                Log.d("Not Result", "Created Plan ID: $responseId")
//                        }
                }
            ) {
                Text(
                    text = "確定",
                    fontSize = 20.sp
                )
            }
        }
        if (expandDateRangePickerDialog) {
            ShowDateRangePikerDialog(
                dateRangePickerState = dateRangePickerState,
                onDismissRequest = { expandDateRangePickerDialog = false },
                dismissButton = {
                    Button(
                        onClick = { expandDateRangePickerDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        )) {
                        Text(
                            text = "取消",
                            style = TextStyle(fontSize = 18.sp),
                            maxLines = 1
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        ),
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
                        Text(
                            text = "確定",
                            style = TextStyle(fontSize = 18.sp),
                            maxLines = 1
                        )
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
        confirmButton = confirmButton,
        colors = DatePickerDefaults.colors(
            containerColor = white100
        ),
        shape = RectangleShape, // 保持矩形外觀
        modifier = Modifier
            .padding(16.dp) // 設置外邊距
            .fillMaxWidth() // 使對話框寬度填滿
    ) {
        DateRangePicker(
            modifier = Modifier.background(Color.LightGray), // 背景顏色設為白色
            state = dateRangePickerState,
            title = {
                Text(
                    text = "選擇行程日期",
                    style = TextStyle(fontSize = 28.sp, color = Color.Black, textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            headline = {
                // 這裡可以放額外的副標題，如果需要
            },
            colors = DatePickerDefaults.colors(
                containerColor = white100,
                selectedDayContainerColor = purple100,
                dayInSelectionRangeContainerColor = purple100
            )
        )
    }
}

@Preview
@Composable
fun PreviewPlanCreateScreen() {
    PlanCreateScreen(
        rememberNavController(),
        planCreateViewModel = viewModel(),
        viewModel(),
        requestVM = viewModel()
    )
}