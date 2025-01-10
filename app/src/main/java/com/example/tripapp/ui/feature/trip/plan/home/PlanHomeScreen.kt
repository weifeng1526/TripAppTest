package com.example.swithscreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripapp.R
import com.example.tripapp.ui.feature.trip.plan.alter.PLAN_ALTER_ROUTE
import com.example.tripapp.ui.feature.trip.plan.create.PLAN_CREATE_ROUTE
import com.example.tripapp.ui.feature.trip.plan.crew.PLAN_CREW_ROUTE
import com.example.tripapp.ui.feature.trip.plan.edit.PLAN_EDIT_ROUTE
//import com.example.tripapp.ui.feature.trip.plan.home.Plan
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
//import com.example.tripapp.ui.feature.trip.plan.restful.CreatePlan
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.restful.RequestVM
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white400
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanHomeScreen(
    navController: NavController,
    planHomeViewModel: PlanHomeViewModel,
    requestVM: RequestVM
) {
    val memNo = 1
    //當有新值發佈到StateFlow時，狀態更新而重組。
    val plans by planHomeViewModel.plansState.collectAsState()
    val plansOfMember by planHomeViewModel.plansOfMemberState.collectAsState()
    val plansOfContry by planHomeViewModel.plansByContryState.collectAsState()
    val contryNames by planHomeViewModel.contriesState.collectAsState()
    // 資料庫編號從1開始，0代表沒有
    var selectedPlanId by remember { mutableIntStateOf(0) }
    //搜尋列:國家
    val searchWord by planHomeViewModel.searchWord.collectAsState()
    var inputedContry by remember { mutableStateOf("") }
    var selectedContry by remember { mutableStateOf("") }
    var expandPlans by remember { mutableStateOf(false) }
    val filteredContries = contryNames.filter {
        it.startsWith(inputedContry) || it.contains(inputedContry, ignoreCase = false)
    }
    var expandContries by remember { mutableStateOf(false) }
    expandContries = expandContries && filteredContries.isNotEmpty()
    //我創的行程表
    var myCreatedPlans by remember { mutableStateOf(listOf<Plan>()) }
    //我加入的群組
    var myJoinedPlans by remember { mutableStateOf(listOf<Plan>()) }
    //選擇的文字
    var titleName = listOf("已創建的行程", "已加入的行程")
    var selectedTitle by remember { mutableStateOf(titleName[0]) }
    //其他
    var expandPlanConfigDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val response = requestVM.GetPlans()
        Log.d("response getplans" , "$response")
        response.let {
            planHomeViewModel.setPlans(response)
            planHomeViewModel.setContryNamesFromPlans(response)
        }
    }
    LaunchedEffect(Unit) {
        val response = requestVM.GetPlanByMemId(1)
        Log.d("getPlanByMemId", "${response}")
        response.let {
            planHomeViewModel.setPlansByMemberByApi(memId = memNo)
            Log.d("response in home", "${response}")
            Log.d("plansOfMember in home", "${plansOfMember}")
        }
    }


    Log.d("init plans", "${plans}")
    Log.d("init plans of member", "${plansOfMember}")
    Log.d("init contryNames", "${contryNames}")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(white100, white400))),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {  }
            ) {
                TextField(
                    value = inputedContry,
                    readOnly = false,
                    maxLines = 1,
                    singleLine = true,
                    onValueChange = {
                        expandContries = true
                        inputedContry = it
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.add_box),
                            contentDescription = "",
                            modifier = Modifier.size(30.dp)
                                .clickable { navController.navigate(PLAN_CREATE_ROUTE) },
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            MenuAnchorType.PrimaryEditable,
                            true
                        ),
                )
                ExposedDropdownMenu(
                    expanded = expandContries,
                    onDismissRequest = { expandContries = false }
                ) {
                    filteredContries.forEach {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(it) },
                            onClick = {
                                selectedContry = it
                                planHomeViewModel.setSearchWord(selectedContry)
                                expandContries = false
                                expandPlans = true
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (selectedTitle.equals(titleName[0])) colorResource(id = R.color.white_300)
                        else colorResource(id = R.color.white_100)
                    )
                    .clickable { selectedTitle = titleName[0] },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${myCreatedPlans.size}\r\n${titleName[0]}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (selectedTitle.equals(titleName[1])) colorResource(id = R.color.white_300)
                        else colorResource(id = R.color.white_100)
                    )
                    .clickable { selectedTitle = titleName[1] },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${myJoinedPlans.size}\r\n${titleName[1]}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (selectedTitle.equals(titleName[0]) && plansOfMember.size > 0) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // 每列 1 個小卡
                modifier = Modifier.fillMaxSize()
            ) {
                //所有plan
                items(plansOfMember.size) { index ->
                    var plan = plansOfMember[index]
                    //行程表
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(320.dp)
                            .background(Color.LightGray)
                            .clickable {
                                navController.navigate("${PLAN_EDIT_ROUTE}/${plan.schNo}")
                            }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.dst),//預設圖
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .fillMaxHeight()
                                    .padding()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.6f)
                                ) {
                                    Text(
                                        text = plan.schName, //行程名稱
                                        maxLines = 2,
                                        fontSize = 24.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                ) {
                                    Text(
                                        text = "${plan.schStart} ~ ${plan.schEnd}", //開始日期~結束日期
                                        maxLines = 1,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f)
                                ) {
                                    IconButton(
                                        onClick = {
                                            expandPlanConfigDialog = true
                                            selectedPlanId = plan.schNo
                                            Log.d("d expand", "expand: ${expandPlanConfigDialog}")
                                        },
                                        modifier = Modifier.size(50.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.more_horiz),
                                            contentDescription = "more Icon",
                                            modifier = Modifier.size(48.dp),
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                ) {
                                    IconButton(
                                        onClick = { navController.navigate(PLAN_CREW_ROUTE) },
                                        modifier = Modifier
                                            .size(52.dp)
                                            .padding(1.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.group),
                                            contentDescription = "group Icon",
                                            modifier = Modifier.size(50.dp),
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (expandPlanConfigDialog) {
            ShowPlanConfigsDialog(
                plan = plans.first { it.schNo == selectedPlanId },
                onDismiss = { expandPlanConfigDialog = false },
                navController = navController,
                planHomeViewModel = planHomeViewModel,
                requestVM = requestVM,
                coroutineScope = coroutineScope
            )
        }
        //監聽這個狀態是否展開，然後打api
        //少用coroutin打api，不好控制，建議在vm做
        LaunchedEffect(expandPlans) {
            if (expandPlans) {
                planHomeViewModel.setPlansByContry(searchWord)
                Log.d("122", "${searchWord}")
                Log.d("d expand", "expand: ${plansOfContry}")
            }
        }
        //接收viewModel的負責記憶開關的狀態變數
        val isShowDialog by planHomeViewModel.isDialogShow.collectAsState()

        //如果可以財會展開，記得要在VM寫狀態切換的方法，用於關閉開啟
        if (isShowDialog) {
            ShowPlansDialogAfterSearch(
                planHomeViewModel = planHomeViewModel,
                navController = navController,
                plans = plansOfContry,
                onDismiss = {
                    expandPlans = false
                    planHomeViewModel.onDismissDialog()
                },
                onConfrim = {
                    navController.navigate("${PLAN_CREATE_ROUTE}/1/${it.schNo}/${it.schName}/${it.schCon}/${it.schCur}")
                    planHomeViewModel.onDismissDialog()
                }
            )
        }
    }
}

@Composable
fun ShowPlansDialogAfterSearch(
    navController: NavController,
    planHomeViewModel: PlanHomeViewModel,
    plans: List<Plan>,
    onDismiss: () -> Unit,
    onConfrim: (Plan) -> Unit
) {
    var selected by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf<Plan?>(null) }
    AlertDialog(
        title = { plans.first().schCon },
        shape = RectangleShape,
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                plans.forEach { plan ->
                    if(plan.memNo == -1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    color = if (selected) colorResource(id = R.color.white_300) else colorResource(
                                        id = R.color.white_100
                                    )
                                )
                                .clickable {
                                    selectedPlan = plan
                                    selected = true
                                },
                        ) {
                            Text(
                                text = "${plan.schName}",
                                maxLines = 2,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                selectedPlan?.let {
//                    planHomeViewModel.createPlan(it)
                    onConfrim(it)
                }
                onDismiss()
            }) { }

        },
        dismissButton = {
            Button(onClick = { onDismiss() }) { }
        }
    )
}


@Composable
fun ShowPlanConfigsDialog(
    plan: Plan,
    onDismiss: () -> Unit,
    navController: NavController,
    planHomeViewModel: PlanHomeViewModel,
    requestVM: RequestVM,
    coroutineScope: CoroutineScope
) {
    AlertDialog(
        title = { },
        shape = RectangleShape,
        onDismissRequest = onDismiss,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    navController.navigate("${PLAN_ALTER_ROUTE}/${plan.schNo}")
                    Log.d("spendList", "${plan.schNo}")
                }) {
                    Text("變更行程設定")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        val response = requestVM.CreatePlan(plan)
                        response?.let { planHomeViewModel.addPlan(plan) }
                        onDismiss()
                    }
                }) {
                    Text("複製行程表")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        val response = requestVM.DeletePlan(plan.schNo)
                        response.let { planHomeViewModel.removePlan(plan.schNo) }
                        onDismiss()
                    }
                }) {
                    Text("刪除行程表")
                }
                Button(onClick = onDismiss) {
                    Text("返回")
                }
            }
        },
        confirmButton = {}
    )
}


@Preview
@Composable
fun PreviewPlanHomeScreen() {
    PlanHomeScreen(
        rememberNavController(),
        planHomeViewModel = viewModel(),
        requestVM = RequestVM()
    )
}
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(6.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Spacer(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(horizontal = 5.dp)
//            ) // 中間空白，讓其他內容排在最右邊
//            IconButton(
//                onClick = { navController.navigate(PLAN_CREATE_ROUTE) },
//                modifier = Modifier
//                    .size(50.dp)
//                    .background(Color.White)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.note_add),
//                    contentDescription = "Add Icon",
//                    modifier = Modifier.size(48.dp),
//                    tint = Color.Unspecified
//                )
//            }
//            IconButton(
//                onClick = {
//                },
//                modifier = Modifier
//                    .size(50.dp)
//                    .background(Color.White)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.list_sort),
//                    contentDescription = "sort Icon",
//                    modifier = Modifier.size(48.dp),
//                    tint = Color.Unspecified
//                )
//            }
//        }
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(2.dp)
//                .background(color = Color(0xFFDFDCEF))
//        )

//            Column(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .padding(start = 50.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.Start
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_member),
//                    contentDescription = "schedule Icon",
//                    tint = Color.Unspecified,
//                    modifier = Modifier.size(80.dp)
//                )
//                Text(
//                    text = "會員暱稱${memNo}",
//                    style = TextStyle(
//                        fontSize = 20.sp,
//                        textAlign = TextAlign.Center
//                    ),
//                    modifier = Modifier
//                        .padding(top = 10.dp)
//                )
//            }
//            Spacer(
//                modifier = Modifier.width(60.dp)
//            )
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(),
//                verticalArrangement = Arrangement.Center
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    IconButton(
//                        onClick = { navController.navigate(PLAN_CREATE_ROUTE) },
//                        modifier = Modifier
//                            .size(50.dp)
//                            .background(Color.White)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.note_add),
//                            contentDescription = "Add Icon",
//                            modifier = Modifier.size(48.dp),
//                            tint = Color.Unspecified
//                        )
//                    }
//                }
//                Row(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    IconButton(
//                        onClick = { /*排序*/ },
//                        modifier = Modifier
//                            .size(50.dp)
//                            .background(Color.White)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.list_sort),
//                            contentDescription = "list_sort Icon",
//                            modifier = Modifier.size(48.dp),
//                            tint = Color.Unspecified
//                        )
//                    }
//                }
//            }