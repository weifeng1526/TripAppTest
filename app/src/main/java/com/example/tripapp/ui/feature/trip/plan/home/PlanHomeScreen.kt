package com.example.swithscreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.trip.plan.alter.PLAN_ALTER_ROUTE
import com.example.tripapp.ui.feature.trip.plan.create.PLAN_CREATE_ROUTE
import com.example.tripapp.ui.feature.trip.plan.crew.PLAN_CREW_ROUTE
import com.example.tripapp.ui.feature.trip.plan.edit.PLAN_EDIT_ROUTE
//import com.example.tripapp.ui.feature.trip.plan.home.Plan
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
//import com.example.tripapp.ui.feature.trip.plan.restful.CreatePlan
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.plan.crew.PlanCrewScreen
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.restful.RequestVM
import com.example.tripapp.ui.theme.black100
import com.example.tripapp.ui.theme.black200
import com.example.tripapp.ui.theme.black300
import com.example.tripapp.ui.theme.black400
import com.example.tripapp.ui.theme.black600
import com.example.tripapp.ui.theme.black700
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.green100
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.purple500
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white400
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanHomeScreen(
    navController: NavController,
    planHomeViewModel: PlanHomeViewModel,
    requestVM: RequestVM
) {
    val getUid = GetUid(MemberRepository)
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
    //選擇的標籤
    var titleName = listOf("已創建的行程", "已加入的行程")
    var selectedTitle by remember { mutableStateOf(titleName[0]) }
    //其他
    var expandPlanConfigDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        val response = requestVM.GetPlans()
        Log.d("response getplans", "$response")
        response.let {
            planHomeViewModel.setPlans(response)
        }
    }
    LaunchedEffect(selectedTitle, plans.size) {
        if (selectedTitle.equals(titleName[0])) {
            val response = requestVM.GetPlanByMemId(getUid)
            Log.d("getPlanByMemId", "${response}")
            response.let {
                planHomeViewModel.setPlansByMemberByApi(memId = getUid)
            }
        }
        if (selectedTitle.equals(titleName[1])) {
            val response = requestVM.GetPlansOfMemberInCrew(getUid)
            Log.d("GetPlansOfMemberInCrew", "${response}")
            response.let {
                planHomeViewModel.setPlansOfMember(response)
            }
        }
    }


    Log.d("init plans", "${plans}")
    Log.d("init plans of member", "${plansOfMember}")
    Log.d("init contryNames", "${contryNames}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(white100, white400))),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate(PLAN_CREATE_ROUTE) },
                modifier = Modifier
                    .fillMaxWidth() // 按鈕寬度佔滿全螢幕
                    .padding(horizontal = 16.dp, vertical = 10.dp) // 可選：設定水平間距
                    .border(2.dp, purple300, shape = RoundedCornerShape(24.dp))
                    .height(56.dp), // 可選：設定固定高度
                shape = RoundedCornerShape(24.dp), // 設定圓角
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.white_100), // 設定背景顏色
                    contentColor = Color.Black, // 設定文字顏色
                    disabledContainerColor = colorResource(id = R.color.white_300), // 禁用狀態背景顏色
                    disabledContentColor = Color.LightGray // 禁用狀態文字顏色
                )
            ) {
                Text(text = "新增行程表", fontSize = 20.sp) // 按鈕文字與樣式
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)) // 裁剪為圓角
                    .border(2.dp, purple300, shape = RoundedCornerShape(24.dp)) // 使用相同的圓角形狀
                    .weight(1f)
                    .background(
                        color = if (selectedTitle.equals(titleName[0])) colorResource(id = R.color.purple_200)
                        else colorResource(id = R.color.white_100),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { selectedTitle = titleName[0] },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${titleName[0]}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = if (selectedTitle.equals(titleName[0])) colorResource(id = R.color.white_100)
                        else colorResource(id = R.color.black_900),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)) // 裁剪為圓角
                    .border(2.dp, purple300, shape = RoundedCornerShape(24.dp)) // 使用相同的圓角形狀
                    .weight(1f)
                    .background(
                        color = if (selectedTitle.equals(titleName[1])) colorResource(id = R.color.purple_200)
                        else colorResource(id = R.color.white_100),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { selectedTitle = titleName[1] },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${titleName[1]}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = if (selectedTitle.equals(titleName[1])) colorResource(id = R.color.white_100)
                        else colorResource(id = R.color.black_900),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }

        if (plansOfMember.size > 0) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // 每列 1 個小卡
                modifier = Modifier.fillMaxSize()
            ) {
                //所有plan
                Log.d("deffrence plan size", "${plansOfMember.size}")
                items(plansOfMember.size) { index ->
                    var plan = plansOfMember[index]
                    val context = LocalContext.current
                    var picture by remember {
                        mutableStateOf(
                            Bitmap.createBitmap(
                                1,
                                1,
                                Bitmap.Config.ARGB_8888
                            )
                        )
                    }
                    var bitMap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

                    // 觀察dst.dstPic內容是否改變
                    // 使用 LaunchedEffect 來在背景執行緒處理 Bitmap 轉換
                    LaunchedEffect(plan.schPic) {
                        picture = withContext(Dispatchers.IO) {
                            try {
                                val decodedBitmap =
                                    BitmapFactory.decodeByteArray(plan.schPic, 0, plan.schPic.size)
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
                    //行程表
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(300.dp)
                            .border(1.dp, purple300, shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = purple300,
                            contentColor = purple300
                        )
                    ) {
                        Image(
                            bitmap = picture.asImageBitmap(),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .padding(start = 6.dp, end = 6.dp, top = 6.dp)
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    navController.navigate("${PLAN_EDIT_ROUTE}/${plan.schNo}")
                                }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp)
                                .background(white100)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .fillMaxHeight()
                                    .padding()
                                    .clip(RoundedCornerShape(8.dp)) // 裁剪為圓角
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.6f)
                                ) {
                                    Text(
                                        text = "${plan.schName}", //開始日期~結束日期
                                        maxLines = 1,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = black900,
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
                                        color = black900,
                                        modifier = Modifier.padding(start = 18.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (selectedTitle.equals(titleName[0])) {
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.5f)
                                            .padding(end = 6.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                expandPlanConfigDialog = true
                                                selectedPlanId = plan.schNo
                                                plan.schState = 0
                                                planHomeViewModel.updatePlanByApi(plan)
                                            },
                                            modifier = Modifier.size(40.dp)
                                        ) {

                                            Icon(
                                                painter = painterResource(id = R.drawable.more_horiz),
                                                contentDescription = "more Icon",
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(purple200)
                                                    .padding(5.dp),
                                                tint = white100
                                            )
                                        }
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .padding(end = 6.dp)
                                ) {
                                    IconButton(
                                        onClick = { navController.navigate("${PLAN_CREW_ROUTE}/${plan.schNo}/${plan.schName}") },
                                        modifier = Modifier
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.group),
                                            contentDescription = "group Icon",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(purple200)
                                                .padding(5.dp),
                                            tint = white100
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
    }
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
        containerColor = white100,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                Button(onClick = {
//                    navController.navigate("${PLAN_ALTER_ROUTE}/${plan.schNo}")
//                    Log.d("spendList", "${plan.schNo}")
//                }) {
//                    Text("變更行程設定")
//                }
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
        navController = rememberNavController(),
        planHomeViewModel = viewModel(),
        requestVM = viewModel()
    )
}