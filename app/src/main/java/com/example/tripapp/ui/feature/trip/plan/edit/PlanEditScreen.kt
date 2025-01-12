package com.example.tripapp.ui.feature.trip.plan

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.map.MAP_ROUTE
import com.example.tripapp.ui.feature.trip.plan.edit.PlanEditViewModel
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.feature.trip.dataObjects.*
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.dataObjects.Poi
import com.example.tripapp.ui.restful.RequestVM
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    val memNo = 1
    val samplesInMemNo = -1
    //測試用poi
    var poi by remember { mutableStateOf(Poi()) }
    //CoroutineScope
    val coroutineScope = rememberCoroutineScope()
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
    var isShowPlanConfigDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        planHomeViewModel.setPlanByApi(schNo)
        planEditViewModel.setDstsByApi(schNo)
    }

    //日期轉換
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
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
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .border(1.dp, Color.Black)
                        .clickable { addDstBtAtTop = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_location),
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "新增景點", //變數
                        style = TextStyle(
                            fontSize = 16.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .border(1.dp, Color.Black)
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
                        painter = painterResource(id = R.drawable.add_location),
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "日期+", //變數
                        style = TextStyle(
                            fontSize = 16.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .border(1.dp, Color.Black)
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
                        painter = painterResource(id = R.drawable.add_location),
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "日期-", //變數
                        style = TextStyle(
                            fontSize = 16.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .border(1.dp, Color.Black)
                        .clickable {
                            isShowPlanConfigDialog = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_location),
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "更改日期", //變數
                        style = TextStyle(
                            fontSize = 16.sp, textAlign = TextAlign.Center
                        ), modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .weight(0.2f)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(0.9f)
                ) {
                    items(days.size) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .border(1.dp, Color.Black)
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
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            Text(
                                text = "第${days[it] + 1}天",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(0.3f),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp, Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = plan.schName, style = TextStyle(
                        fontSize = 24.sp
                    ), modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${plan.schStart} ~ ${plan.schEnd}", style = TextStyle(
                        fontSize = 16.sp
                    ), modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "最後編輯時間: 2024-11-1", style = TextStyle(
                        fontSize = 16.sp
                    ), modifier = Modifier.fillMaxWidth()
                )
            }
            val endTimeMap by planEditViewModel.endTimeMap.collectAsState()
            var isShowHintColor by remember { mutableStateOf(false) }
            if (selectedDate.isNotEmpty()) {
                planEditViewModel.setDstsForDateByApi(selectedDate)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1), // 每列 1 個小卡
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f)
                ) {
                    items(dstsForDate.size) { index ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            ShowDstRow(
                                planEditViewModel = planEditViewModel,
                                dst = dstsForDate[index],
                                gonnaDelete = {
                                    planEditViewModel.deleteDstByApi(it)
                                },
                                upSwap = { planEditViewModel.setDstUpSwap(index, dstsForDate.toMutableList()) },
                                downSwap = { planEditViewModel.setDstDownSwap(index, dstsForDate.toMutableList()) }
                            )
                            ConfigTimeInputForStartAndStay(
                                planEditViewModel = planEditViewModel,
                                dst = dstsForDate[index],
                                showHintColor = isShowHintColor,
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
                                    if (index + 1 < dstsForDate.size) {
                                        if (finalTime > dstsForDate[index + 1].dstStart)
                                            isShowHintColor = true
                                        else isShowHintColor = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        if (addDstBtAtTop) {
            mainAddDstAlertDialog(requestVM = requestVM,
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
                for(i in 0..dstsForDate.size - 1){
                    planEditViewModel.deleteDstByApi(dstsForDate[i])
                }
            },
            selectedDay = selectedDay,
            selectedDate = selectedDate,
        )
    }

    if(isShowPlanConfigDialog) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDstRow(
    planEditViewModel: PlanEditViewModel,
    dst: Destination,
    gonnaDelete: (Destination) -> Unit,
    upSwap: () -> Unit,
    downSwap: () -> Unit
) {
    var removeDstRowDialog by remember { mutableStateOf(false) }
    //輸入時間：開始、停留、轉移
//    var showStartTimeInput by remember { mutableStateOf(false) }
//    var showStayTimeInput by remember { mutableStateOf(false) }
////    var showTransferTimeInput by remember { mutableStateOf(false) }
//    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//    val HMregex = "\\d{2}:\\d{2}".toRegex()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .border(1.dp, Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Image(
                painter = painterResource(R.drawable.dst), // 預設圖
                contentDescription = "Dst image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .size(100.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(6.dp),
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
                IconButton(
                    onClick = { removeDstRowDialog = true }, modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.disabled),
                        contentDescription = "delete Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                }
                IconButton(
                    onClick = { upSwap() }, modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_box),
                        contentDescription = "delete Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                }
                IconButton(
                    onClick = { downSwap() }, modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.remove),
                        contentDescription = "delete Icon",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
        if (removeDstRowDialog == true) {
            showDstDeleteDialog(
                onConfirm = {
                    if(it) gonnaDelete(dst)
                },
                onDismissRequest = {removeDstRowDialog = false},
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
    showHintColor: Boolean,
    onResult: (String) -> Unit
) {
    var showStartTimeInput by remember { mutableStateOf(false) }
    var showStayTimeInput by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .border(1.dp, color = if (showHintColor) Color.Red else Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "預計開始: ${dst.dstStart}",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, Color.Black)
                .clickable { showStartTimeInput = true },
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .border(1.dp, Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "預計停留: ${dst.dstEnd}",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, Color.Black)
                .clickable { showStayTimeInput = true },
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
            .background(Color.LightGray)
            .border(1.dp, Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "預計行程間隔時間: ${dst.dstInr}",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, Color.Black)
                .clickable { showTransferTimeInput = true },
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
//    startTime: String,
//    stayTime: String,
    endTime: String,
    onResult: (String) -> Unit
) {
    var hint = if (endTime.equals("")) true else false
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .border(1.dp, Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "行程結束時間: ${endTime}",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            //color = if (hint) Color.Red else Color.Black,
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, Color.Black)
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
fun showDstDeleteDialog(
    dst: Destination,
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean) -> Unit,
) {
    AlertDialog(
        title = { Text(text = "${dst.dstDate}$-${dst.dstName}") },
        text = {
            Text(text = "是否刪除此行程")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Button(onClick = {
                onConfirm(false)
                onDismissRequest()
            }) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(true)
                onDismissRequest()
            }) {
                Text(text = "確定")
            }
        }
    )
}


@Composable
fun mainAddDstAlertDialog(
    requestVM: RequestVM, onDismissRequest: () -> Unit, poiSelected: (Poi) -> Unit, navController: NavController, schNo: Int, dstDate: String
) {
    var showDialog by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismissRequest, title = {}, text = {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDialog = true
                        navController.navigate("${MAP_ROUTE}/${schNo}/${dstDate}")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.travel_explore),
                    contentDescription = "map Icon",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Unspecified
                )
                Text("地圖")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.turn_right),
                    contentDescription = "map Icon",
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(90f),
                    tint = Color.Unspecified
                )
                Text("返回")
            }
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
    AlertDialog(onDismissRequest = onDismiss, title = {}, text = {
        Column {
            TimeInput(state = timePickerState)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
            }
        }
    }, confirmButton = {
        Button(onClick = {
            onConfirm(timePickerState)
            onDismiss()
        }) {
            Text("確定")
        }
    },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("取消")
            }
        }
    )
}

@Preview
@Composable
fun PreviewPlanEditScreen() {
    PlanEditScreen(
        rememberNavController(), viewModel(), viewModel(), requestVM = viewModel(), schNo = 2
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ShowDstRow(
//    planEditViewModel: PlanEditViewModel,
//    dst: Destination,
//    onTimeInputChange: (String) -> Unit,
//) {
//    var addDstBtAtRows by remember { mutableStateOf(false) }
//    //輸入時間：開始、停留、轉移
//    var showStartTimeInput by remember { mutableStateOf(false) }
//    var showStayTimeInput by remember { mutableStateOf(false) }
//    var showTransferTimeInput by remember { mutableStateOf(false) }
//    //轉換已選的開始、停留、轉移秒數，秒數總和
//    var secondOfDstStart by remember { mutableIntStateOf(0) }
//    var secondOfDstStay by remember { mutableIntStateOf(0) }
//    var secondOfDstTransfer by remember { mutableIntStateOf(0) }
//    //計算結束時間、下一個抵達時間
//    var secondOfDstEnd by remember { mutableIntStateOf(0) }
//    var secondOfDstTransferDone by remember { mutableIntStateOf(0) }
//    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//    //輸入時間：開始、停留、轉移的字串
//    var stringOfDstStart by remember { mutableStateOf(dst.dstStart) }
//    var stringOfDstStay by remember { mutableStateOf(dst.dstEnd) }
//    var stringOfDstEnd by remember { mutableStateOf("") }
//    var stringOfDstTransfer by remember { mutableStateOf(dst.dstInr) }
//    var stringOfDstTransferDone by remember { mutableStateOf("") }
//
//    val HMregex = "\\d{2}:\\d{2}".toRegex()
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.LightGray)
//            .border(1.dp, Color.Black),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.9f)
//        ) {
//            Image(
//                painter = painterResource(R.drawable.dst), // 預設圖
//                contentDescription = "Dst image",
//                contentScale = ContentScale.FillBounds,
//                modifier = Modifier
//                    .padding(6.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .size(100.dp)
//            )
//            Column(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .padding(6.dp),
//                verticalArrangement = Arrangement.spacedBy(6.dp)
//            ) {
//                Text(
//                    text = dst.dstName, fontSize = 20.sp, textAlign = TextAlign.Start
//                )
//                Text(
//                    text = dst.dstAddr, fontSize = 16.sp, textAlign = TextAlign.Start
//                )
//            }
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(),
//            horizontalArrangement = Arrangement.End, // 右對齊
//            verticalAlignment = Alignment.Top
//        ) {
//            IconButton(
//                onClick = { addDstBtAtRows = true }, modifier = Modifier.size(32.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.more_horiz),
//                    contentDescription = "menu Icon",
//                    modifier = Modifier.size(30.dp),
//                    tint = Color.Unspecified
//                )
//            }
//        }
//    }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.LightGray)
//            .border(1.dp, Color.Black),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(
//            text = "預計開始: ${HMregex.find(dst.dstStart)?.value ?: ""}",
//            fontSize = 16.sp,
//            textAlign = TextAlign.Start,
//            modifier = Modifier
//                .padding(8.dp)
//                .border(1.dp, Color.Black)
//                .clickable { showStartTimeInput = true },
//        )
//        Spacer(modifier = Modifier.width(100.dp))
//        Text(
//            text = "停留時間: ${HMregex.find(dst.dstEnd)?.value}",
//            fontSize = 16.sp,
//            textAlign = TextAlign.Start,
//            modifier = Modifier
//                .padding(8.dp)
//                .border(1.dp, Color.Black)
//                .clickable { showStayTimeInput = true },
//        )
//    }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.LightGray)
//            .border(1.dp, Color.Black),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(
//            text = "預計結束: ${
//                HMregex.find(
//                    stringOfDstEnd
//                )?.value ?: ""
//            }",
//            fontSize = 16.sp,
//            textAlign = TextAlign.Start,
//            modifier = Modifier.padding(8.dp)
//        )
//        Spacer(modifier = Modifier.width(100.dp))
//        Text(
//            text = "預計轉移：${HMregex.find(dst.dstInr)?.value}",
//            fontSize = 16.sp,
//            textAlign = TextAlign.Start,
//            modifier = Modifier
//                .padding(8.dp)
//                .border(1.dp, Color.Black)
//                .clickable { showTransferTimeInput = true }
//        )
//    }
//    if (showStartTimeInput) {
//        ShowTimeInput(
//            onDismiss = { showStartTimeInput = false },
//            onConfirm = {
//                stringOfDstStart = LocalTime.of(it.hour, it.minute, 0).format(timeFormatter)
//                dst.dstStart = stringOfDstStart
//                planEditViewModel.updateDstRquest(dst)
//                //planEditViewModel.onStartTimeChange()
//            }
//        )
//    }
//    if (showStayTimeInput) {
//        ShowTimeInput(onDismiss = { showStayTimeInput = false }, onConfirm = {
//            stringOfDstStay = LocalTime.of(it.hour, it.minute, 0).format(timeFormatter)
//            dst.dstEnd = stringOfDstStay
//            planEditViewModel.updateDstRquest(dst)
//        })
//    }
//    if (showTransferTimeInput) {
//        ShowTimeInput(onDismiss = { showTransferTimeInput = false }, onConfirm = {
//            stringOfDstTransfer = LocalTime.of(it.hour, it.minute, 0).format(timeFormatter)
//            dst.dstInr = stringOfDstTransfer
//            planEditViewModel.updateDstRquest(dst)
//        })
//    }
//    LaunchedEffect(dst.dstStart, dst.dstEnd, dst.dstInr) {
//        stringOfDstEnd = addTimes(stringOfDstStart, stringOfDstStay)
//        stringOfDstTransferDone = addTimes(stringOfDstEnd, stringOfDstTransfer)
//        Log.d("stringOfDstEnd", "$stringOfDstEnd")
//        Log.d(
//            "stringOfDstTransferDone",
//            "$stringOfDstTransferDone"
//        )
//        //onTimeInputChange(stringOfDstTransferDone)
//    }
////    LaunchedEffect(dst.dstStart, dst.dstEnd, dst.dstInr) {
////        secondOfDstStart = convertTimeToSeconds(dst.dstStart)
////        secondOfDstStay = convertTimeToSeconds(dst.dstEnd)
////        secondOfDstTransfer = convertTimeToSeconds(dst.dstInr)
////        secondOfDstEnd = secondOfDstStart + secondOfDstStay
////        stringOfDstEnd = convertSecondsToTimeString(secondOfDstEnd.toLong())
////        secondOfDstTransferDone = secondOfDstEnd + secondOfDstTransfer
////        Log.d("stringOfDstEnd", "$stringOfDstEnd")
////        Log.d("secondOfDstTransferDone", "${convertSecondsToTimeString(secondOfDstTransferDone.toLong())}")
////        Log.d("dst in row", "${dst}")
////        onTimeInputChange(secondOfDstTransferDone)
////        //onTimeInputChange()
////        val time1 = "12:00:00"
////        val time2 = "11:00:00"
////    }
//}
