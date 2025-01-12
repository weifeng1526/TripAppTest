package com.example.tripapp.ui.feature.baggage.baglist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.baggage.BagItems
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import kotlinx.coroutines.launch

@Composable
fun BagRoute(
    navController: NavHostController,
    schNo: Int?
) {
    Log.d("BagRoute", "schNo: $schNo")
    BagListScreen(navController, schNo)
}

@Composable
fun BagListScreen(
    navController: NavHostController,
    schNo: Int?,
    bagViewModel: BagViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditing = remember { mutableStateOf(false) }
    // 控制行李箱圖片切換的狀態
    val isSuitcaseImage1 = remember { mutableStateOf(true) }
    val memNo = GetUid(MemberRepository)
    val trips = bagViewModel.trips.collectAsState()
    val selectedTrip by bagViewModel.selectedTrip.collectAsState()
    val userTrips = trips.value.filter { it.memNo == memNo }

    val isNeedDefaultSelected by bagViewModel.isNeedDefaultSelected.collectAsState()

    LaunchedEffect(userTrips, schNo) {
        if (userTrips.isNotEmpty() && schNo != null && isNeedDefaultSelected) {
            bagViewModel.onDefaultSelected(memNo, schNo)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
//            第一行的操作行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.green_100))
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // 左右分散排列
            ) {
//                // 返回按鈕
//                IconButton(onClick = {
//                    navController.popBackStack()
//                    scope.launch {
//                        snackbarHostState.showSnackbar(
//                            "回到上一頁", withDismissAction = true
//                        )
//                    }
//                }) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        tint = colorResource(id = R.color.white_100),
//                        contentDescription = "back"
//
//                    )
//                }
                Spacer(modifier = Modifier.width(44.dp)) // 這樣設定 Spacer 的高度為 24.dp

                // 標題文字置中
                Text(
                    text = "開始準備行李",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(id = R.color.white_100),
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally), // 填滿剩餘空間
                    maxLines = 1
                )

                // 我的會員按鈕
                IconButton(onClick = {
                    navController.navigate("member")
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "前往我的會員", withDismissAction = true
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        tint = colorResource(id = R.color.white_100),
                        contentDescription = "我的會員",
                        modifier = Modifier.size(36.dp) // 設定圖示大小，這裡設定為 48.dp

                    )
                }
            }

//行李箱上方的空白區塊
            Spacer(modifier = Modifier.height(12.dp))


            // 行李箱圖片 預設225.dp,為了測試改150
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .border(
                        width = 6.dp,
                        color = colorResource(id = R.color.green_200),
                        shape = RoundedCornerShape(50)
                    )
                    .background(
                        color = colorResource(id = R.color.white_400),
                        shape = RoundedCornerShape(50)
                    )
                    .align(Alignment.CenterHorizontally)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isSuitcaseImage1.value = false
                                try {
                                    awaitRelease()
                                } finally {
                                    isSuitcaseImage1.value = true
                                }
                            }
                        )
                    }
            ) {
//                根據狀態切換圖片
                Image(
                    painter = painterResource(
                        id = if (isSuitcaseImage1.value) R.drawable.ashley___suitcase_1_new
                        else R.drawable.ashley___suitcase_2_new
                    ),
                    contentDescription = "suitcase Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Center),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.purple_300))
                )
            }

//行李箱與下拉選單的空白區塊
            Spacer(modifier = Modifier.height(12.dp))

            // 使用 BagViewModel 中的狀態
            val selectedTrip by bagViewModel.selectedTrip.collectAsState()
            // 下拉式選單
            TripPickDropdown(
                options = userTrips.map { it.schName },
                selectedOption = selectedTrip?.schName ?: "選擇一個行程",
                onOptionSelected = { selectedSchName ->
                    val selectedTrip = userTrips.find { it.schName == selectedSchName }
                    selectedTrip?.let { bagViewModel.onTripSelected(memNo, selectedTrip.schNo) }
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(74.dp)
                    .align(Alignment.CenterHorizontally)
            )
//            下拉式選單跟物品清單之間的空白區塊
            Spacer(modifier = Modifier.height(4.dp))
            val items by bagViewModel.items.collectAsState()
            val checkedState by bagViewModel.checkedState.collectAsState()


            // 物品清單


            ScrollContent(
                innerPadding = PaddingValues(),
                items = items,
                checkedState = checkedState,
                isEditing = isEditing,
                onCheckedChange = { itemNo, isChecked ->
                    bagViewModel.updateCheckedState(itemNo, isChecked)
                },
                onItemRemoved = { itemNo ->
                    bagViewModel.removeItem(itemNo)
                }
            )
        }

//         懸浮增加按鈕
//        有跳頁的route
        FloatingActionButton(
            onClick = {
                navController.navigate("additem/${selectedTrip?.schNo}")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "跳轉至增加物品頁", withDismissAction = true
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.AddCircle, "增加物品")
        }
    }
}

//TRIP挑選 - 下拉式選單
@Composable
fun TripPickDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuExpanded = remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFE8DEF8),
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (menuExpanded.value) 0.dp else 12.dp,  // 未展開時圓角，展開後下端無圓角
                    bottomEnd = if (menuExpanded.value) 0.dp else 12.dp      // 未展開時圓角，展開後下端無圓角
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF65558F),
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (menuExpanded.value) 0.dp else 12.dp, // 未展開時圓角，展開後下端無圓角
                    bottomEnd = if (menuExpanded.value) 0.dp else 12.dp// 未展開時圓角，展開後下端無圓角
                )
            )
            .clickable { menuExpanded.value = true }
            .padding(1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // 垂直方向居中
            horizontalArrangement = Arrangement.SpaceBetween, // 水平方向居中
            modifier = Modifier
//                .fillMaxSize() // 填滿父容器
                .padding(horizontal = 12.dp) // 添加適當的水平內邊距
        ) {
            Icon(
                imageVector = Icons.Default.DateRange, // 左側圖標
                contentDescription = "Trip Icon",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f) // 文本容器占據剩餘空間
                    .fillMaxHeight(), // 垂直方向填滿
                contentAlignment = Center // 完全置中
            ) {
                Text(
                    text = selectedOption,
                    fontSize = 18.sp,
                    color = Color.Black,
                    maxLines = 1 // 保證文本不換行
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_drop_down_circle_24), // 右側下拉圖標
                contentDescription = "Dropdown Icon",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

//        下拉式選單的內容物

        DropdownMenu(
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = false },
            modifier = Modifier
                .width(280.dp) // 與外層 Box 寬度一致
                .heightIn(min = 56.dp, max = 224.dp) //預設336,為了測試改成228
                .background(
                    color = Color(0xFFE8DEF8),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
//                        bottomStart = if (menuExpanded.value) 0.dp else 30.dp,  // 展開後下端無圓角
//                        bottomEnd = if (menuExpanded.value) 0.dp else 30.dp      // 展開後下端無圓角
                        bottomStart = if (menuExpanded.value) 12.dp else 0.dp,  // 展開後下端圓角
                        bottomEnd = if (menuExpanded.value) 12.dp else 0.dp      // 展開後下端圓角

                    )
                )
                .border(
                    1.dp, Color(0xFF65558F),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
//                        bottomStart = if (menuExpanded.value) 0.dp else 30.dp,  // 開後下端無圓角
//                        bottomEnd = if (menuExpanded.value) 0.dp else 30.dp      // 展開後下端無圓角
                        bottomStart = if (menuExpanded.value) 12.dp else 0.dp,  // 展開後下端圓角
                        bottomEnd = if (menuExpanded.value) 12.dp else 0.dp      // 展開後下端圓角
                    )
                )
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        menuExpanded.value = false
                    },

                    text = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() // 選單項目寬度填滿
                                .height(56.dp) // 與外層 Box 高度一致
                                .background(
                                    color = Color(100f, 100f, 100f, 0f),
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

//                            下拉選單要不要有ICON
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_trip_origin_24),
//                                contentDescription = "Option Icon",
//                                tint = Color.Gray,
//                                modifier = Modifier.size(20.dp)
//                            )
//                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = option,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScrollContent(
    innerPadding: PaddingValues,
    items: List<BagItems>,
    checkedState: Map<Int, Boolean>, // 從 ViewModel 提供的勾選狀態
    isEditing: MutableState<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit, // 更新勾選狀態的回調
    onItemRemoved: (Int) -> Unit // 刪除操作
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 30.dp, end = 30.dp)
        ) {
            Text(
                text = "物品清單", fontSize = 20.sp, modifier = Modifier.weight(1f)
            )
//            IconButton(onClick = {
//                isEditing.value = !isEditing.value
//            }) {
//                Icon(
//                    imageVector = if (isEditing.value) Icons.Filled.Done else Icons.Filled.Edit,
//                    contentDescription = if (isEditing.value) "完成編輯" else "編輯"
//                )
//            }
        }

        // 列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(items) { bagItem ->
                // 確保每個 item 的勾選狀態是單獨管理的
                val isChecked = remember { mutableStateOf(checkedState[bagItem.itemNo] ?: false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0x8065558F),
                            shape = RoundedCornerShape(size = 10.dp)
                        )
                        .width(317.dp)
                        .height(44.dp)
                        .background(
                            color = Color(0xFFE8DEF8),
                            shape = RoundedCornerShape(size = 10.dp)
                        )
                        .clickable(enabled = !isEditing.value) { // 非編輯狀態才可打勾
                            val newState = !isChecked.value
                            isChecked.value = newState
                            onCheckedChange(bagItem.itemNo, newState) // 更新勾選狀態
                        }
                        .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .weight(1f)
                            .fillMaxHeight(), // 確保 Box 填滿可用高度
                        contentAlignment = Alignment.Center // 文字置中
                    ) {
                        Text(
                            text = bagItem.itemName
                        )
                    }

//                    // 勾選框
//                    Box(
//                        modifier = Modifier.size(24.dp)
//                    ) {
//                        if (isChecked.value) {
//                            Image(
//                                painter = painterResource(id = R.drawable.baseline_check_circle_24),
//                                contentDescription = "Checked",
//                            )
//                        } else {
//                            Image(
//                                painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
//                                contentDescription = "Unchecked"
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.width(24.dp))
//                    // 物品名稱
//                    Text(
//                        text = bagItem.itemName,
//                        modifier = Modifier.weight(1f)
//                    )
//                    // 刪除按鈕
//                    if (isEditing.value) {
//                        IconButton(onClick = {
//                            // 刪除選項並更新物品列表
//                            onItemRemoved(bagItem.itemNo) // 傳遞當前項目的 itemNo 給回調
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Delete,
//                                contentDescription = "刪除"
//                            )
//                        }
//                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewBagListRoute() {
    BagListScreen(navController = NavHostController(LocalContext.current), schNo = 1)
}
