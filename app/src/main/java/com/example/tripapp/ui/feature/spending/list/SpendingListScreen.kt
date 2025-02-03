package com.example.tripapp.ui.feature.spending.list

import SpendingListViewModel
import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.GetName
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.spending.CrewRecord
import com.example.tripapp.ui.feature.spending.SpendingRecordVM
import com.example.tripapp.ui.feature.spending.TotalSumVM
import com.example.tripapp.ui.feature.spending.addlist.SpendingAddViewModel
import com.example.tripapp.ui.feature.spending.addlist.getSpendingAddNavigationRoute
import com.example.tripapp.ui.feature.spending.settinglist.SPENDING_SETLIST_ROUTE
import com.example.tripapp.ui.theme.*

enum class tabsTrip {
    tripA,
    tripB,
    tripC

}

//VM 和 UI 連結的地方（VM）
@Composable
fun SpendingListRoute(navHostController: NavHostController) {
    SpendingListScreen(
        navController = navHostController,
        floatingButtonAddClick = {
            //導頁專用語法
            navHostController.navigate(getSpendingAddNavigationRoute(it, -1))
        },
        spendingSettingBtn = {
            navHostController.navigate(SPENDING_SETLIST_ROUTE)
        }
    )
}

//單純預覽，可以放假資料。
@Preview
@Composable
fun PreviewSpendingRoute() {
    SpendingListScreen(
//Item 假資料
    )
}

//純UI，跟資料一點關係都沒有
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SpendingListScreen(
//    requestVM: RequestVM = viewModel(),
    spendingRecordVM: SpendingRecordVM = viewModel(),
    totalSumVM: TotalSumVM = viewModel(),
    navController: NavHostController = rememberNavController(),
    spendingListViewModel: SpendingListViewModel = viewModel(),
    spendingAddViewModel: SpendingAddViewModel = viewModel(),
//    items:List<User> = listOf(),
    floatingButtonAddClick: (Int) -> Unit = {},
    spendingSettingBtn: () -> Unit = {},
) {
    val TAG = "TAG---SpendingListScreen---"
    val numFormatter = DecimalFormat("#,###.##") // Double 僅保留兩位小數

    val context = LocalContext.current
    val plans by spendingRecordVM.plan.collectAsState()
    val spendList by spendingRecordVM.spendingListInfo.collectAsState()
    val listDetail by spendingRecordVM.tabTripListSelectedList.collectAsState()
    val tabsTripListIndex by spendingRecordVM.tabsTripListSelectedIndex.collectAsState()
    val tripName by spendingListViewModel.tripName.collectAsState()

    //取得選中tab的行程編號(schNo)
    val selectedSchoNo = tripName?.getOrNull(tabsTripListIndex)?.schNo
    //取得選中清單的清單編號(costNo)
    val spendingOneListInfo by spendingListViewModel.spendingOneListInfo.collectAsState()

//    var settleExpanded by remember { mutableStateOf(false) }
    val settleExpanded by spendingListViewModel.settleExpanded.collectAsState()



    //取得會員編號/名稱
    val memberNo = GetUid(MemberRepository)
    val memberName = GetName()

    val totalSum by spendingRecordVM.totalSumStatus.collectAsState()
    val averageCost by spendingRecordVM.averageCost.collectAsState()

    LaunchedEffect(Unit) {
        spendingRecordVM.initPlan()
        //要換成清單編號
        spendingListViewModel.GetData(2)
        spendingListViewModel.getTripName(1)
//        Log.d(TAG, "有沒有來 :))))))))))) ")

    }

    LaunchedEffect(selectedSchoNo) {
        if (selectedSchoNo != null) {
            spendingRecordVM.tripCrew(selectedSchoNo)
        }

    }


    //取得行程編號
//    Log.d(TAG, "${tripName?.getOrNull(tabsTripListIndex)?.schNo}")
//    Log.d(TAG, "spendList:${spendList.getOrNull(tabsTripListIndex)}")
//    Log.d(TAG, "spendingOneListInfo: ${spendingOneListInfo}")
//    Log.d(TAG, "tabsTripListSelectedList: ${listDetail}")
//    Log.d(TAG, "tabsTripListIndex: ${tabsTripListIndex}")

    //這個才有真正的tab資料
//    Log.d(TAG, "plan: ${plans.getOrNull(tabsTripListIndex)}")
    Log.d(TAG, "index: ${tabsTripListIndex}")
    Log.d(TAG, "tabsTripListName: ${tripName}")
    Log.d(TAG, "selectedSchoNo: ${selectedSchoNo}")
    Log.d(TAG, "spendingOneListInfo: ${spendingOneListInfo}")


//
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)

    ) {


        Column(
            modifier = Modifier
                .padding(32.dp, 20.dp),
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Hi,$memberName ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,

                    )

                val isSettleExpanded by spendingListViewModel.settleExpanded.collectAsState()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            Log.d(TAG, "結算click: ${spendingListViewModel.settleExpanded.value}")
                            if (isSettleExpanded) {
                                spendingListViewModel.setSettleExpanded(false)
                            } else {
                                spendingListViewModel.setSettleExpanded(true)
                            }
//                            spendingListViewModel.getsettleExpanded()

                            Toast.makeText(context, "結算", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = white100,
                            contentColor = purple300
                        ),
                        border = BorderStroke(
                            2.dp, white400,
                        ),
                        modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        Text(
                            text = "結算",
                            fontSize = 15.sp
                        )
                    }



                    Button(
                        onClick = {
                            spendingSettingBtn()
//                            Toast.makeText(context, "設定", Toast.LENGTH_SHORT).show()

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = white100,
                            contentColor = purple300
                        ),
                        border = BorderStroke(
                            2.dp, color = white400,
                        )
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_setting),
                            contentDescription = "more",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }


            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 18.dp, 0.dp, 0.dp),

                ) {
                Row(

                    horizontalArrangement = Arrangement.Start

                ) {
                    Text(
                        text = "團體花費",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    val totalCost by spendingRecordVM.totalCost.collectAsState()
                    Text(
                        //團體花費金額
//                        text = totalCost.toString(),
                        text = numFormatter.format(totalCost),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text = "JPY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 25.sp,
                        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                    )
                }


            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 12.dp, 0.dp, 0.dp),

                ) {
                Row(

                    horizontalArrangement = Arrangement.Start

                ) {
                    Text(
                        text = "個人花費",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        //個人花費金額
                        text = numFormatter.format(averageCost),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text = "JPY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 25.sp,
                        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                    )
                }


            }

//            //公費餘額
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(0.dp, 12.dp, 0.dp, 0.dp),
//
//                ) {
//                Row(
//                    horizontalArrangement = Arrangement.Start
//                ) {
//                    Text(
//                        text = "公費餘額",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium,
//                        textAlign = TextAlign.Start,
//                        lineHeight = 24.sp,
//                    )
//                }
//                Row(
//                    horizontalArrangement = Arrangement.End,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//
//                    Text(
//                        text = "200,000",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.ExtraBold,
//                        textAlign = TextAlign.End,
//                    )
//                    Text(
//                        text = "JPY",
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Medium,
//                        lineHeight = 25.sp,
//                        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
//                    )
//                }
//            }

        }

        HorizontalDivider(thickness = 2.dp, color = white400)

        Column(
            modifier = Modifier
                .wrapContentSize()

        ) {
            ScrollableTabRow(
                modifier = Modifier
                    .wrapContentSize()
                    .background(white100),
                selectedTabIndex = tabsTripListIndex,

                indicator = { tabPositions ->
//                    SecondaryIndicator(
//                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabsTripListIndex]),
//                        color = Color.Green, // 修改指標顏色為綠色
//                    )


                },
                divider = {
//                    HorizontalDivider(
//                        color = Color.Blue // 修改分隔線顏色為淺灰色
//                    )
                },
                containerColor = white300
            ) {

                tripName?.forEachIndexed { index: Int, tripName: CrewRecord ->
                    Tab(
                        modifier = Modifier
                            .offset(x = -52.dp, y = 0.dp)
                            .weight(1f)
                            .background(
                                if (index == tabsTripListIndex) white100 else white300
                            ),
                        text = {
                            Text(
                                text = tripName.schName,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(20.dp, 0.dp)
                            )
                        },
                        selected = index == tabsTripListIndex,
                        selectedContentColor = black900,
                        unselectedContentColor = black700,
                        onClick = { spendingRecordVM.onTabChanged(index) }
                    )
                }


            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                if (selectedSchoNo != null) {
                    tripTab(
                        navHostController = navController,
                        spendingStatusList = listDetail?.second ?: listOf(),
                        spendingListViewModel = spendingListViewModel,
                        schoNo = selectedSchoNo,
                        totalSum = totalSum ?: listOf()
                    )
                }
            }

//            Column (){
//                AnimatedVisibility(visible = settleExpanded) {
//                    spendingResult(
//                        navHostController = navController
//                    )
//                }
//            }


        }


    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {
        FloatingActionButton(
//            onClick = {floatingButtonAddClick.invoke(ListDetail?.first ?:0)},
            onClick = {
                if (selectedSchoNo != null) {
                    floatingButtonAddClick.invoke(selectedSchoNo)
                }
            },
            containerColor = purple200,
            shape = RoundedCornerShape(50),
            modifier = Modifier.align(Alignment.BottomEnd)

        ) {
            Image(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "add",
                modifier = Modifier
                    .size(33.dp)
            )
        }
    }
}