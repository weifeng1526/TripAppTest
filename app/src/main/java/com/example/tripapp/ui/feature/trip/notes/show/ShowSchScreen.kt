package com.example.tripview.show

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.spending.list.SPENDING_LIST_ROUTE
import com.example.tripapp.ui.feature.trip.notes.note.NOTES_ROUTE
import com.example.tripapp.ui.feature.trip.plan.edit.PlanEditViewModel
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.notes.show.SHOW_SCH_ROUTE

import com.example.tripapp.ui.restful.RequestVM
import com.example.tripapp.ui.theme.black800
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.white100
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.Int

@Composable
fun ShowSchScreen(
    navController: NavController,
    requestVM: RequestVM,
    planEditViewModel: PlanEditViewModel = viewModel(),
    planHomeViewModel: PlanHomeViewModel = viewModel(),
    destination: Destination,
    schNo: Int
) {
    Log.d("ShowSchScreen1", "Entered ShowSchScreen with schNo: $schNo") // 日誌輸出進入 ShowSchScreen 並顯示 schNo
    val destForNote by planEditViewModel.dstsState.collectAsState()

    val planForNote by planHomeViewModel.planState.collectAsState()
    var planStart by remember { mutableStateOf("") }
    var planEnd by remember { mutableStateOf("") }
    var planLastEdit by remember { mutableStateOf("") }
    var days by remember { mutableStateOf(emptyList<Int>()) }
    var dates by remember { mutableStateOf(emptyList<LocalDate>()) }
    var dayOfWeek by remember { mutableStateOf(emptyList<Int>()) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var selectDate by remember { mutableStateOf("${planForNote.schStart}") }
    Log.d("ShowSchScreen1.1", "Entered ShowSchScreen with schNo: ${destForNote}")

    //
    val destForNoteByDate =  destForNote.groupBy { it.dstDate }.toList()
//    val pair = "a" to listOf<String>()
//    pair.first
//    pair.second
    val selectedDestForNote = destForNoteByDate.find { it.first == selectDate }?.second ?: listOf()
    val uid = GetUid(MemberRepository)
    //

    LaunchedEffect(destForNote) {
        try {
            val planResponse = requestVM.GetPlan(schNo)
            planResponse?.let {
                planHomeViewModel.setPlan(it) // 更新 ViewModel 的數據
            }
            if (planForNote.schStart.isNotEmpty()) {
                selectDate = planForNote.schStart
            }
            Log.d("planResponse", "$planResponse")
            Log.d("planHomeViewModel", "${planForNote}")
            val dstsResponse = requestVM.GetDstsBySchedId(schNo)
            dstsResponse?.let {
                planEditViewModel.setDsts(it) // 更新目標數據
            }
            Log.d("dstsResponse", "$dstsResponse")
        } catch (e: Exception) {
            Log.e("LaunchedEffect", "數據加載出錯: ${e.message}")
        }
    }
//    Log.e("plan", "${plan}")
    if (planForNote.schNo != 0){
        planStart = planForNote.schStart
        planEnd = planForNote.schEnd
        planLastEdit = planForNote.schLastEdit
        Log.d("ShowSchScreen5", "Plan Start: $planStart, Plan End: $planEnd")  // 日誌輸出計劃的開始和結束日期
        days = (0..ChronoUnit.DAYS.between(
            LocalDate.parse(planStart, dateFormatter),
            LocalDate.parse(planEnd, dateFormatter)
        ).toInt()).toList()

        Log.d("ShowSchScreen6", "Days: $days")  // 日誌輸出 days 數據
    }

    dates = days.map {
        LocalDate.parse(planStart).plusDays(it.toLong())
    }
    Log.d("ShowSchScreen7", "Dates: $dates")  // 日誌輸出日期列表

    dayOfWeek = dates.map { it.dayOfWeek.value }
    Log.d("ShowSchScreen8", "Day of Week: $dayOfWeek")  // 日誌輸出每一天的星期幾

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (planForNote.schStart.isNotEmpty()) {
            Log.d("plansForNote", "${planForNote}")
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(43.dp)
            ) {
                items(days.size) {
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .clickable {
                            selectDate = dates[it].format(dateFormatter)
                            Log.d("ShowSchScreen3","Selected Date: ${selectDate}")  // 日誌輸出選擇的日期
                        }) {
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
                        Log.d("plansForNote2", "${planForNote}")
                    }
                }
            }
        }
//        if (selectDate == destination.dstDate)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(selectedDestForNote) { destForNote ->
            Log.d("ShowSchScreen4", "Rendering destination: ${destForNote.dstName}")
            Column ( modifier = Modifier
                .fillMaxSize(1f)){
                Column(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(22.dp)
                                .offset(10.dp)
                        ) {
                            drawCircle(
                                color = purple300, // 外框顏色
                                radius = size.minDimension / 2 // 圓的半徑
                            )
                            // 內部填充圓
                            drawCircle(
                                color = white100, // 內部顏色
                                radius = size.minDimension / 2 - 3.dp.toPx() // 縮小3dp作為邊框
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = destForNote.dstStart, color = black800,
                        )
                    }
                    Row() {
                        Canvas(modifier = Modifier.fillMaxHeight()) {
                            drawLine(
                                color = purple300,
                                start = Offset(x = 59f, y = 0f),
                                end = Offset(x = 59f, y = size.height),
                                strokeWidth = 10f
                            )
                        }
                        Spacer(modifier = Modifier.width(40.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(R.drawable.aaa),
                                contentDescription = null,
                                modifier = Modifier.size(338.dp, 190.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .offset(25.dp)
                            ) {
                                Text(
                                    text = "${destForNote.dstName}. ${destForNote.dstNo}",
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    fontSize = 22.sp
                                )
                                Image(
                                    painter = painterResource(R.drawable.edit_note),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp).clickable {
                                        Log.d("NOTES_ROUTE", "dstNo: ${destForNote.dstNo}")
                                        Log.d("NOTES_ROUTE", "uid: ${uid}")
                                        navController.navigate("$NOTES_ROUTE/${destForNote.dstNo}/${uid}")
                                    }
                                )
                            }
                        }
                    }
                }
                if (destForNote.dstInr.isNotEmpty()){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .height(110.dp)
                                .fillMaxWidth(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Canvas(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .offset(10.dp)
                                ) {
                                    drawCircle(
                                        color = purple300, // 外框顏色
                                        radius = size.minDimension / 2 // 圓的半徑
                                    )
                                    // 內部填充圓
                                    drawCircle(
                                        color = white100, // 內部顏色
                                        radius = size.minDimension / 2 - 3.dp.toPx() // 縮小3dp作為邊框
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = destForNote.dstEnd, color = black800,
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Canvas(modifier = Modifier.fillMaxHeight()) {
                                    drawLine(
                                        color = purple300,
                                        start = Offset(x = 59f, y = 0f),
                                        end = Offset(x = 59f, y = size.height),
                                        strokeWidth = 10f
                                    )
                                }
                                Image(
                                    painter = painterResource(R.drawable.baseline_directions_walk_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .offset(x = 60.dp, y = 15.dp)
                                        .size(70.dp)
                                )
                                Text(
                                    text = "${destForNote.dstInr} 分鐘", fontSize = 22.sp,
                                    modifier = Modifier.offset(x = 70.dp, y = 30.dp)
                                )
                            }
                        }
                    }
                }
            }
            Log.d("destForNoteCard", "Rendering destination: ${destination}")
        }
    }

    }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(43.dp)
//                .background(color = colorResource((R.color.white_300)))
//                .horizontalScroll(rememberScrollState())
//        ) {
//            Row(
//                modifier = Modifier
//                    .width(86.dp)
//                    .height(43.dp)
//            )
//            {
//                Column(
//                    modifier = Modifier
//                        .width(46.dp)
//                        .height(43.dp)
//                ) {
//                    Text(
//                        text = "12/16 週一",
//                    )
//                }
//                Image(
//                    painter = painterResource(R.drawable.sun),
//                    contentDescription = "image",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .size(25.dp)
//                        .offset(y = 8.dp)
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .width(86.dp)
//                    .height(43.dp)
//            )
//            {
//                Column(
//                    modifier = Modifier
//                        .width(46.dp)
//                        .height(43.dp)
//                ) {
//                    Text(
//                        text = "12/17 週二",
//                    )
//                }
//                Image(
//                    painter = painterResource(R.drawable.rain),
//                    contentDescription = "image",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .size(25.dp)
//                        .offset(y = 8.dp)
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .width(86.dp)
//                    .height(43.dp)
//            )
//            {
//                Column(
//                    modifier = Modifier
//                        .width(46.dp)
//                        .height(43.dp)
//                ) {
//                    Text(
//                        text = "12/18 週三",
//                    )
//                }
//                Image(
//                    painter = painterResource(R.drawable.cloud),
//                    contentDescription = "image",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .size(25.dp)
//                        .offset(y = 8.dp)
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .width(86.dp)
//                    .height(43.dp)
//            )
//            {
//                Column(
//                    modifier = Modifier
//                        .width(46.dp)
//                        .height(43.dp)
//                ) {
//                    Text(
//                        text = "12/19 週四",
//                    )
//                }
//                Image(
//                    painter = painterResource(R.drawable.sun),
//                    contentDescription = "image",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .size(25.dp)
//                        .offset(y = 8.dp)
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .width(86.dp)
//                    .height(43.dp)
//            )
//            {
//                Column(
//                    modifier = Modifier
//                        .width(46.dp)
//                        .height(43.dp)
//                ) {
//                    Text(
//                        text = "12/20 週五",
//                    )
//                }
//                Image(
//                    painter = painterResource(R.drawable.cloud),
//                    contentDescription = "image",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .size(25.dp)
//                        .offset(y = 8.dp)
//                )
//            }
//        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(SPENDING_LIST_ROUTE) },
            containerColor = purple200,
            shape = RoundedCornerShape(50),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_attach_money_24),
                contentDescription = "add",
                modifier = Modifier
                    .size(33.dp)
            )
        }
    }
}

@Composable
fun schDestCard(
    navController: NavController,
    destination: Destination,
    ){

//    val dstinr = destination.dstInr.toIntOrNull() ?:0
    Log.d("schDestCard", "Entered schDestCard with dstInr: ${destination.dstInr}")
    Log.d("schDestCard", "Entered schDestCard with dest: ${destination}")
    Column ( modifier = Modifier
        .fillMaxSize(1f)){
        Column(
        modifier = Modifier
            .height(250.dp)
            .fillMaxWidth(1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .size(22.dp)
                    .offset(10.dp)
            ) {
                drawCircle(
                    color = purple300, // 外框顏色
                    radius = size.minDimension / 2 // 圓的半徑
                )
                // 內部填充圓
                drawCircle(
                    color = white100, // 內部顏色
                    radius = size.minDimension / 2 - 3.dp.toPx() // 縮小3dp作為邊框
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = destination.dstStart, color = black800,
            )
        }
        Row() {
            Canvas(modifier = Modifier.fillMaxHeight()) {
                drawLine(
                    color = purple300,
                    start = Offset(x = 59f, y = 0f),
                    end = Offset(x = 59f, y = size.height),
                    strokeWidth = 10f
                )
            }
            Spacer(modifier = Modifier.width(40.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.aaa),
                    contentDescription = null,
                    modifier = Modifier.size(338.dp, 190.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .offset(25.dp)
                ) {
                    Text(
                        text = destination.dstName,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        fontSize = 22.sp
                    )
                    Image(
                        painter = painterResource(R.drawable.edit_note),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
    if (destination.dstInr.isNotEmpty()){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(22.dp)
                            .offset(10.dp)
                    ) {
                        drawCircle(
                            color = purple300, // 外框顏色
                            radius = size.minDimension / 2 // 圓的半徑
                        )
                        // 內部填充圓
                        drawCircle(
                            color = white100, // 內部顏色
                            radius = size.minDimension / 2 - 3.dp.toPx() // 縮小3dp作為邊框
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = destination.dstEnd, color = black800,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Canvas(modifier = Modifier.fillMaxHeight()) {
                        drawLine(
                            color = purple300,
                            start = Offset(x = 59f, y = 0f),
                            end = Offset(x = 59f, y = size.height),
                            strokeWidth = 10f
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.baseline_directions_walk_24),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(x = 60.dp, y = 15.dp)
                            .size(70.dp)
                    )
                    Text(
                        text = "${destination.dstInr} 分鐘", fontSize = 22.sp,
                        modifier = Modifier.offset(x = 70.dp, y = 30.dp)
                    )
                }
            }
        }
    }else{
            return
    }
    }
}


@Preview(showBackground = true)
@Composable
fun ShowSchScreenPreview() {
    ShowSchScreen(
        navController = rememberNavController(),
        requestVM = RequestVM(),
        planEditViewModel = PlanEditViewModel(),
        planHomeViewModel = PlanHomeViewModel(),
        destination = Destination(),
        schNo = viewModel()
    )
}