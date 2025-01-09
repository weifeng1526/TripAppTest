package com.example.tripapp.ui.feature.spending.list

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.tripapp.ui.feature.spending.SpendingRecordVM
import com.example.tripapp.ui.feature.spending.TotalSumVM
import com.example.tripapp.ui.feature.spending.addlist.SPENDING_ADD_ROUTE
import com.example.tripapp.ui.feature.spending.settinglist.SPENDING_SETLIST_ROUTE
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.theme.*

enum class tabsTrip {
    tripA,
    tripB,
    tripC

}

//VM 和 UI 連結的地方（VM）
@Composable
fun SpendingRoute(navHostController: NavHostController) {
    SpendingListScreen(
        navController = navHostController,
        floatingButtonAddClick = {
            //導頁專用語法
            navHostController.navigate(SPENDING_ADD_ROUTE)
        },
        spendingSettingBtn = {
            navHostController.navigate(SPENDING_SETLIST_ROUTE)
        },
        schNo = 0,

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
@Composable
fun SpendingListScreen(
//    requestVM: RequestVM = viewModel(),
    spendingRecordVM: SpendingRecordVM = viewModel(),
    totalSumVM: TotalSumVM = viewModel(),
    navController: NavHostController = rememberNavController(),
//    items:List<User> = listOf(),
    floatingButtonAddClick: () -> Unit = {},
    spendingSettingBtn: () -> Unit = {},
    schNo: Int = 0
) {

    LaunchedEffect(Unit) {
        spendingRecordVM.initPlan()
    }

    val plans by spendingRecordVM.plan.collectAsState()
    val spendList by spendingRecordVM.spendingListInfo.collectAsState()
    val tabsTripListIndex by spendingRecordVM.tabsTripListSelectedIndex.collectAsState()
    val tabsTripListScheNo by spendingRecordVM.tabTripListSelectedList.collectAsState()

    Log.d("TAG", "spendList:${spendList}")
    Log.d("TAG", "totalSum: ${totalSumVM.totalSum}")

    val context = LocalContext.current


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
                    text = "Hi,Rubyyyyyer ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,

                    )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
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
                            Toast.makeText(context, "設定", Toast.LENGTH_SHORT).show()

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = white100,
                            contentColor = purple300
                        ),
                        border = BorderStroke(
                            2.dp, Color(0xFFDFDCEF),
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

                    Text(
                        text = "10,000",
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
                        text = "2,000",
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
                        text = "公費餘額",
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
                        text = "200,000",
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

        }

        HorizontalDivider(thickness = 2.dp, color = white400)

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {


            ScrollableTabRow(
                modifier = Modifier
                    .fillMaxWidth()
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

                plans.forEachIndexed { index: Int, plan: Plan ->
                    Tab(
                        modifier = Modifier
                            .background(
                                if (index == tabsTripListIndex) white100 else white300
                            ),
                        text = {
                            Text(
                                text = plan.schName,
                                fontSize = 16.sp
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
                tripTab(
                    navHostController = navController,
                    spendingRecordVM = spendingRecordVM,
                    spendingListStatus = tabsTripListScheNo?.second ?: listOf()
                )
            }

        }


    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {
        FloatingActionButton(
            onClick = floatingButtonAddClick,
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