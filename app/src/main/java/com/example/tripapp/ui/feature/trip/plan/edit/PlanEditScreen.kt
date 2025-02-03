package com.example.tripapp.ui.feature.trip.plan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.map.MAP_ROUTE
import com.example.tripapp.ui.feature.map.genMapNavigationRoute
import com.example.tripapp.ui.feature.trip.plan.edit.PlanEditViewModel
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.feature.trip.dataObjects.*
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.dataObjects.Poi
import com.example.tripapp.ui.restful.RequestVM
import com.example.tripapp.ui.theme.black300
import com.example.tripapp.ui.theme.green100
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.purple500
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white200
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

//理想是新增所有卡都可以隨時拖拉
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlanEditScreen(
    navController: NavController,
    planEditViewModel: PlanEditViewModel,
    planHomeViewModel: PlanHomeViewModel,
    requestVM: RequestVM,
    schNo: Int
) {
    //測試用poi
    var poi by remember { mutableStateOf(Poi()) }
    //CoroutineScope
    val coroutineScope = rememberCoroutineScope()
    //單筆行程明細
    val dst by planEditViewModel.dstState.collectAsState()
    //所有行程明細
    val dsts by planEditViewModel.dstsState.collectAsState()
    //某個日期的行程明細
    val dstsForDate by planEditViewModel.dstsForDateState.collectAsState()
    //某個行程範本的行程明細
    val plans by planHomeViewModel.plansState.collectAsState()
    //單個行程表
    val plan by planHomeViewModel.planState.collectAsState()
    //加入景點按鈕
    var addDstBtAtTop by remember { mutableStateOf(false) }
    //觀察行程開始日期、結束日期、最後編輯日期、對應的：行程天數、行程日期、星期
    var planStart by remember { mutableStateOf("") }
    var planEnd by remember { mutableStateOf("") }
    var planLastEdit by remember { mutableStateOf("") }
    var days by remember { mutableStateOf(mutableListOf<Int>()) }
    var dates by remember { mutableStateOf(mutableListOf<LocalDate>()) }
    var dayOfWeek by remember { mutableStateOf(emptyList<Int>()) }
    //觀察已選日期、第幾天、星期幾
    var selectedDate by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(0) }
    var selectedDayOfWeek by remember { mutableStateOf(0) }
    var isShowDaysDeleteDialog by remember { mutableStateOf(false) }
    val needRefreshRequest by planEditViewModel.needRefreshRequest.collectAsState()

    LaunchedEffect(Unit) {
        planHomeViewModel.setPlanByApi(schNo)
    }

    LaunchedEffect(needRefreshRequest) {
        if (needRefreshRequest) {
            planHomeViewModel.setPlanByApi(schNo)
            planEditViewModel.setDstsByApi(schNo)
            planEditViewModel.consumeNeedRefreshRequest()
        }
    }

    LaunchedEffect(plan.schNo) {
        if (plan.schNo != 0)
            planEditViewModel.setDstsByApi(schNo)
    }

    LaunchedEffect(dsts.size, selectedDate) {
//        planEditViewModel.setDstsForDateByApi(selectedDate)
        planEditViewModel.setDstsForDateFromDsts(selectedDate)
    }


    //日期轉換
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    if (plan.schNo != 0) {
        LaunchedEffect(plan.schStart, plan.schEnd) {
            planStart = plan.schStart
            planEnd = plan.schEnd
            planLastEdit = plan.schLastEdit
            days = (0..ChronoUnit.DAYS.between(
                LocalDate.parse(planStart, dateFormatter), LocalDate.parse(planEnd, dateFormatter)
            ).toInt()).toMutableList()
            dates = days.map {
                LocalDate.parse(planStart).plusDays(it.toLong())
            }.toMutableList()
            dayOfWeek = dates.map { it.dayOfWeek.value }
            selectedDate = planStart
        }
//        //建立一個監聽器(Metirial3提供)
//        val interactionSource = remember { MutableInteractionSource() }
//        // 記錄按鈕的按下狀態
//        val isPressed by interactionSource.collectIsPressedAsState()
//
//        val buttonColor by animateColorAsState(
//            targetValue = if (isPressed) white400 else purple300
//        )
        val showAlldeleteBth by planEditViewModel.showDeleteBtns.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(white100)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white100)
                    .padding(horizontal = 10.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp, Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "行程名稱: ${plan.schName}", style = TextStyle(
                        fontSize = 24.sp
                    ), modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${plan.schStart} ~ ${plan.schEnd}", style = TextStyle(
                        fontSize = 18.sp,
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp)
                )
//                Text(
//                    text = "最後編輯時間: ${plan.schLastEdit}", style = TextStyle(
//                        fontSize = 18.sp
//                    ), modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 2.dp)
//                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .background(white100),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, white300, shape = RoundedCornerShape(8.dp)) // 使用相同的圓角形狀
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    white100,
                                    white400
                                )
                            )
                        )
                        .padding(4.dp)
                        .clickable { addDstBtAtTop = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_location),
                        contentDescription = "Add Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(3.dp),
                        tint = purple200
                    )
                    Text(
                        text = "新增景點", //變數
                        style = TextStyle(
                            fontSize = 18.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, white300, shape = RoundedCornerShape(8.dp)) // 使用相同的圓角形狀
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    white100,
                                    white400
                                )
                            )
                        )
                        .padding(4.dp)
                        .clickable {
                            var newSchEnd = LocalDate.parse(plan.schEnd, dateFormatter)
                            plan.schEnd = newSchEnd
                                .plusDays(1)
                                .format(dateFormatter)
                            coroutineScope.run {
                                launch {
                                    var planResponse = requestVM.UpdatePlan(plan)
                                    planResponse?.let { planHomeViewModel.setPlan(it) }
                                }
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_circle_24),
                        contentDescription = "Add Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(3.dp),
                        tint = purple200
                    )
                    Text(
                        text = "日期+", //變數
                        style = TextStyle(
                            fontSize = 18.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, white300, shape = RoundedCornerShape(8.dp)) // 使用相同的圓角形狀
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    white100,
                                    white400
                                )
                            )
                        )
                        .padding(4.dp)
                        .clickable {
                            var newSchEnd = LocalDate.parse(plan.schEnd, dateFormatter)
                            plan.schEnd = newSchEnd
                                .minusDays(1)
                                .format(dateFormatter)
                            coroutineScope.run {
                                launch {
                                    var planResponse = requestVM.UpdatePlan(plan)
                                    planResponse?.let { planHomeViewModel.setPlan(it) }
                                }
                            }
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.remove_circle),
                        contentDescription = "Add Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(3.dp),
                        tint = purple200
                    )
                    Text(
                        text = "日期-", //變數
                        style = TextStyle(
                            fontSize = 18.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)

                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // 佔據剩餘空間
                if (!showAlldeleteBth) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, white300, shape = RoundedCornerShape(8.dp)) // 使用相同的圓角形狀
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        white100,
                                        white400
                                    )
                                )
                            )
                            .padding(4.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        white100,
                                        white400
                                    )
                                )
                            )
                            .clickable {
                                planEditViewModel.setShowDeleteBtns(!showAlldeleteBth)
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "delete Icon",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(3.dp),
                            tint = purple200
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .border(
                                1.dp,
                                white300,
                                shape = RoundedCornerShape(8.dp)
                            ) // 使用相同的圓角形狀
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        white100,
                                        white400
                                    )
                                )
                            )
                            .clickable {
                                planEditViewModel.setShowDeleteBtns(!showAlldeleteBth)
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "delete Icon",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(3.dp),
                            tint = purple200
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white300)
                    .wrapContentHeight()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(days.size) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(2.dp, white400, RoundedCornerShape(6.dp))
                                .background(
                                   color = if (days[it] == selectedDay) white100 else white300
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .combinedClickable(onClick = {
                                    selectedDate = dates[it].format(dateFormatter)
                                    selectedDay = it
                                }, onLongClick = {
                                    selectedDate = dates[it].format(dateFormatter)
                                    selectedDay = it
                                    isShowDaysDeleteDialog = true
                                }),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${dates[it]}",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 2.dp)
                            )
                            Text(
                                text = "第${days[it] + 1}天",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(start = 6.dp, end = 6.dp, bottom = 2.dp)
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(6.dp)
                    .background(Color.LightGray)
            )
            val endTimeMap by planEditViewModel.endTimeMap.collectAsState()
            if (selectedDate.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1), // 每列 1 個小卡
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
                ) {
                    items(dstsForDate.size) { index ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                //.padding(horizontal = 6.dp)
                                //.clip(shape = RoundedCornerShape(8.dp))
                                .border(1.dp, white400, RoundedCornerShape(8.dp))
                                .background(purple100)
                        ) {
                            val startTime = System.currentTimeMillis()
                            ShowDstRow(
                                planEditViewModel = planEditViewModel,
                                dst = dstsForDate[index],
                                gonnaDelete = {
                                    planEditViewModel.deleteDstByApi(it)
                                },
                                upSwap = {
                                    val startTime = System.currentTimeMillis()
                                    planEditViewModel.setDstUpSwap(
                                        index,
                                        dstsForDate.toMutableList()
                                    )
                                    val endTime = System.currentTimeMillis()
                                    Log.d("in dd Upswap time", "${endTime - startTime}毫秒")
                                },
                                downSwap = {
                                    planEditViewModel.setDstDownSwap(
                                        index,
                                        dstsForDate.toMutableList()
                                    )
                                },
                                showDeleteBtn = showAlldeleteBth
                            )
                            val endTime = System.currentTimeMillis()
                            Log.d("end time of showDstRow：", "${(endTime - startTime)} 毫秒")
                            ConfigTimeInputForStartAndStay(
                                planEditViewModel = planEditViewModel,
                                dst = dstsForDate[index],
                                onResult = { end ->
                                    //planEditViewModel.onStartTimeChange()
                                    planEditViewModel.updateEndTime(index, end)
                                }
                            )
                            ShowResultTimeOfEnd(
                                endTime = endTimeMap[index] ?: "",
                                onResult = {}
                            )
                            ConfigTimeInputForTransfer(
                                planEditViewModel = planEditViewModel,
                                dst = dstsForDate[index]
                            )
                            CaculateTime(
                                startTime = dstsForDate[index].dstStart,
                                stayTime = dstsForDate[index].dstEnd,
                                trnsferTime = dstsForDate[index].dstInr,
                                onResult = { finalTime ->
                                    Log.d("finalTime", "${finalTime}")
                                }
                            )
                        }
                    }
                }
            }
        }
        if (addDstBtAtTop) {
            mainAddDstAlertDialog(
                requestVM = requestVM,
                onDismissRequest = { addDstBtAtTop = false },
                poiSelected = {
                    //單個行程明細
                    val dst = planEditViewModel.dstState
                    planEditViewModel.onAddDstWhenPoiSelect(it, schNo, selectedDate)
                    //planEditViewModel.addToDsesByApi(dst.value)
                },
                navController = navController,
                schNo = schNo,
                dstDate = selectedDate
            )
        }
    }
    if (isShowDaysDeleteDialog) {
        showDaysDeleteDialog(
            onDismissRequest = { isShowDaysDeleteDialog = false },
            onConfirm = {
                Log.d("d delete day", "${it}")
                for (i in 0..dstsForDate.size - 1) {
                    planEditViewModel.deleteDstByApi(dstsForDate[i])
                }
            },
            selectedDay = selectedDay,
            selectedDate = selectedDate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDstRow(
    planEditViewModel: PlanEditViewModel,
    showDeleteBtn: Boolean,
    dst: Destination,
    gonnaDelete: (Destination) -> Unit,
    upSwap: () -> Unit,
    downSwap: () -> Unit
) {
    var removeDstRowDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var picture by remember { mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)) }
    var bitMap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    // 觀察dst.dstPic內容是否改變
    // 使用 LaunchedEffect 來在背景執行緒處理 Bitmap 轉換
    LaunchedEffect(dst.dstPic) {
        picture = withContext(Dispatchers.IO) {
            try {
                Log.d("dst.dstPic", "test")
                val decodedBitmap =
                    BitmapFactory.decodeByteArray(dst.dstPic, 0, dst.dstPic?.size ?: 0)
                decodedBitmap ?: BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.aaa
                )
            } catch (e: Exception) {
                e.printStackTrace()
                bitMap
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white300),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.86f)
        ) {
            Log.d("decodedBitmap", "${dst.dstPic?.size}")
            Image(
                bitmap = picture.asImageBitmap(),
                contentDescription = "Dst image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .size(100.dp)
            )
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = dst.dstName, fontSize = 20.sp, textAlign = TextAlign.Start
                )
                Text(
                    text = dst.dstAddr, fontSize = 16.sp, textAlign = TextAlign.Start
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.End, // 右對齊
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.fillMaxWidth()) {
                if (showDeleteBtn) {
                    IconButton(
                        onClick = { removeDstRowDialog = true }, modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.disabled),
                            contentDescription = "delete Icon",
                            modifier = Modifier.size(42.dp),
                            tint = purple200
                        )
                    }
                } else {
                    IconButton(
                        onClick = { upSwap() },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_drop_up),
                            contentDescription = "arrow_drop_up Icon",
                            modifier = Modifier.size(42.dp),
                            tint = purple200
                        )
                    }
                    IconButton(
                        onClick = { downSwap() },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_drop_down),
                            contentDescription = "arrow_drop_down Icon",
                            modifier = Modifier.size(42.dp),
                            tint = purple200
                        )
                    }
                }
            }
        }
        if (removeDstRowDialog == true) {
            showDstDeleteDialog(
                onConfirm = {
                    if (it) gonnaDelete(dst)
                },
                onDismissRequest = { removeDstRowDialog = false },
                dst = dst,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigTimeInputForStartAndStay(
    planEditViewModel: PlanEditViewModel,
    dst: Destination,
    onResult: (String) -> Unit
) {
    var showStartTimeInput by remember { mutableStateOf(false) }
    var showStayTimeInput by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white100)
            .border(1.dp, white400)
            .clickable { showStartTimeInput = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "開始: ${
                if(isTimeFormat(dst.dstStart)) {
                    dst.dstStart.substring(0, 5)
                } else ""
            }",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white100)
            .border(1.dp, white400)
            .clickable { showStayTimeInput = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "停留: ${
                if(isTimeFormat(dst.dstEnd)) {
                    dst.dstEnd.substring(0, 5)
                } else ""
            }",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
        )
    }
    if (showStartTimeInput) {
        ShowTimeInput(
            onDismiss = { showStartTimeInput = false },
            onConfirm = {
                dst.dstStart = LocalTime.of(it.hour, it.minute, 0).format(timeFormatter)
                planEditViewModel.setDst(dst)
                planEditViewModel.updateDstRquest(dst)
            }
        )
    }
    if (showStayTimeInput) {
        ShowTimeInput(onDismiss = { showStayTimeInput = false }, onConfirm = {
            dst.dstEnd = LocalTime.of(it.hour, it.minute, 0).format(timeFormatter)
            planEditViewModel.setDst(dst)
            planEditViewModel.updateDstRquest(dst)
        })
    }

    LaunchedEffect(dst.dstStart, dst.dstEnd) {
        var endTime = if (isTimeFormat(dst.dstStart) && isTimeFormat(dst.dstEnd)) {
            var secondOfStartTime = convertTimeToSeconds(dst.dstStart)
            var secondOfStayTime = convertTimeToSeconds(dst.dstEnd)
            if (secondOfStartTime + secondOfStayTime < 86400) {
                addTimes(dst.dstStart, dst.dstEnd)
            } else ""
        } else ""
        onResult(endTime)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigTimeInputForTransfer(
    planEditViewModel: PlanEditViewModel,
    dst: Destination,
) {
    var showTransferTimeInput by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white100)
            .border(1.dp, white400)
            .clickable { showTransferTimeInput = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "行程間隔時間: ${
                if(isTimeFormat(dst.dstInr)) {
                    dst.dstInr.substring(0, 5)
                } else ""
            }",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
        )
    }
    if (showTransferTimeInput) {
        ShowTimeInput(onDismiss = { showTransferTimeInput = false }, onConfirm = {
            dst.dstInr = LocalTime.of(it.hour, it.minute, 0).format(timeFormatter)
            planEditViewModel.setDst(dst)
            planEditViewModel.updateDstRquest(dst)
        })
    }

}

@Composable
fun ShowResultTimeOfEnd(
    endTime: String,
    onResult: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white100)
            .border(1.dp, white400),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "行程結束時間: ${
                if(isTimeFormat(endTime)) {
                    endTime.substring(0, 5)
                } else ""
            }",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
        )
    }
    onResult(endTime)
}

@Composable
fun CaculateTime(
    startTime: String,
    stayTime: String,
    trnsferTime: String,
    onResult: (String) -> Unit
) {
    var endTime = addTimes(startTime, stayTime)
    var finalTime = addTimes(endTime, trnsferTime)
    onResult(finalTime)
    Log.d("finalTime", "${finalTime}")
}


@Composable
fun showChangePlanInfoDialog(
    selectedDay: Int,
    selectedDate: String,
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    AlertDialog(title = { Text(text = "取消日期") },
        text = { Text(text = "將會取消${selectedDate}的行程") },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Button(onClick = {
                onDismissRequest()
            }) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedDay)
                onDismissRequest()
            }) {
                Text(text = "確定")
            }
        })
}


@Composable
fun showDaysDeleteDialog(
    selectedDay: Int,
    selectedDate: String,
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    AlertDialog(
        containerColor = white100,
        shape = RectangleShape,
        title = { Text(text = "取消日期") },
        text = {
            Text(
                text = "將會取消${selectedDate}的行程",
                style = TextStyle(fontSize = 18.sp, color = Color.Black)
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Button(onClick = {
                onDismissRequest()
            }) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedDay)
                onDismissRequest()
            }) {
                Text(text = "確定")
            }
        })
}

@Composable
fun showDstDeleteDialog(
    dst: Destination,
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean) -> Unit,
) {
    AlertDialog(
        shape = RectangleShape,
        containerColor = white100,
        title = { Text(text = "${dst.dstDate}\r\n${dst.dstName}") },
        text = {
            Text(text = "是否刪除此行程")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Button(
                onClick = {
                    onConfirm(false)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple200,
                    contentColor = white100
                )
            ) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(true)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple200,
                    contentColor = white100
                )
            ) {
                Text(text = "確定")
            }
        }
    )
}


@Composable
fun mainAddDstAlertDialog(
    requestVM: RequestVM,
    onDismissRequest: () -> Unit,
    poiSelected: (Poi) -> Unit,
    navController: NavController,
    schNo: Int,
    dstDate: String
) {
    var showDialog by remember { mutableStateOf(false) }
    AlertDialog(
        shape = RectangleShape,
        containerColor = white100,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "新增景點") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("${MAP_ROUTE}/${schNo}/${dstDate}")
                            //showDialog = true
                            //navController.navigate(genMapNavigationRoute(schNo, dstDate))
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.travel_explore),
                        contentDescription = "map Icon",
                        modifier = Modifier.size(30.dp),
                        tint = purple200
                    )
                    Text(
                        text = "地圖",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black)
                    )
                }
                // 添加底線
                Divider(
                    color = white400,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 3.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onDismissRequest() }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.turn_right),
                        contentDescription = "map Icon",
                        modifier = Modifier
                            .size(30.dp)
                            .rotate(90f),
                        tint = purple200
                    )
                    Text(
                        text = "返回",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black)
                    )
                }                    // 添加底線
                Divider(
                    color = white400,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 3.dp)
                )
            }
        }, confirmButton = {})
    if (showDialog) {
        // 呼叫 SelectableGridDialog
        SelectableGridDialog(requestVM = requestVM, onItemClick = { selectedItem ->
            showDialog = false // 點擊後關閉對話框
            onDismissRequest()
            poiSelected(selectedItem)
        }, onDismiss = {
            showDialog = false // 點擊關閉按鈕或背景
        })
    }
}

@Composable
fun SelectableGridDialog(
    requestVM: RequestVM, onItemClick: (Poi) -> Unit, // 點擊事件回調
    onDismiss: () -> Unit // 關閉對話框
) {
    val coroutineScope = rememberCoroutineScope()
    var items by remember { mutableStateOf(emptyList<Poi>()) }
    coroutineScope.run {
        launch {
            var response = requestVM.GetPois()
            response.let {
                items = it
                Log.d("d items", "message: ${items}")
            }
        }
    }
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(text = "選擇一個項目")
    }, text = {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp) // 設置對話框的高度上限
        ) {
            items(items.size) { index ->
                val item = items[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onItemClick(item) }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${item.poiNo}:  ${item.poiName}", modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text("關閉")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTimeInput(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true,
    )
    AlertDialog(
        shape = RectangleShape,
        containerColor = white100,
        onDismissRequest = onDismiss, title = {}, text = {
            Column {
                TimeInput(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor = white200,
                        timeSelectorUnselectedContainerColor = white400
                    )
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                }
            }
        }, confirmButton = {
            Button(
                onClick = {
                    onConfirm(timePickerState)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple200,
                    contentColor = white100
                )
            ) {
                Text(
                    text = "確定",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = white100
                    )
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple200,
                    contentColor = white100
                )
            ) {
                Text(
                    text = "取消",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = white100
                    )
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewPlanEditScreen() {
    PlanEditScreen(
        rememberNavController(), viewModel(), viewModel(), requestVM = viewModel(), schNo = 1
    )
}
