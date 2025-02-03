package com.example.tripapp.ui.feature.spending.list

import SpendingListViewModel
import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.member.home.memIcon
import com.example.tripapp.ui.feature.spending.SpendingRecord
import com.example.tripapp.ui.feature.spending.TotalSum
import com.example.tripapp.ui.feature.spending.addlist.SPENDING_ADD_ROUTE
import com.example.tripapp.ui.feature.spending.addlist.SpendingAddViewModel
import com.example.tripapp.ui.feature.spending.addlist.getSpendingAddNavigationRoute
import com.example.tripapp.ui.theme.*



@Preview
@Composable
fun tripTabPre() {
    tripTab(
        rememberNavController(),
        spendingListViewModel = viewModel(),
        totalSum = viewModel(),
        schoNo = 0
    )
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun tripTab(
    navHostController: NavHostController,
    spendingStatusList: List<SpendingRecord> = listOf(),
    spendingListViewModel: SpendingListViewModel,
    totalSum: List<TotalSum>,
    schoNo: Int
) {
    val TAG = "TAG---tripTab---"
    //資料流，每一頁都可以動（新增修改），最後是把最新狀態撈出來。
    val isSettleExpanded by spendingListViewModel.settleExpanded.collectAsState()
//    （0預設;1食物;2交通;3票卷;4住宿;5購物;6娛樂;-1其他）

    var showText = ""










    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)
            .verticalScroll(rememberScrollState())
    ) {

        Log.d(TAG, "tripTab:${isSettleExpanded} ")
//結算清單-------------------------------------F-------------------------------------

        AnimatedVisibility(
            visible = isSettleExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()

        ){
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 24.dp, 20.dp, 16.dp),
                    text = "結算清單",
                    fontSize = 18.sp
                )

                //結算清單
                Column {
                    totalSum.forEach {
                        totalSumRow(
                            it,
                            navController = navHostController,
                            spendingListViewModel = spendingListViewModel
                        )
                    }
                }
            }
        }



//消費明細--------------------------------------------------------------------------


        if (spendingStatusList.size <= 0) {
            showText = "目前沒有資料喔 !"

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Image(
                    painter = painterResource(R.drawable.empty_nodata),
                    contentDescription = "no data!",
                    modifier = Modifier
                        .size(332.dp)
                        .padding(0.dp, 112.dp, 0.dp, 4.dp)
                )
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize(),
                    text = showText,
//                text = "消費明細",
                    fontSize = 18.sp,
                    color = black600

                )




            }
        }else{
            showText = "消費明細"
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 24.dp, 20.dp, 16.dp),
                    text = showText,
//                text = "消費明細",
                    fontSize = 18.sp
                )

                //消費明細
                Column {
                    val sortDescList =  spendingStatusList.sortedByDescending { it.costNo }
                    sortDescList.forEach { spendingStatus ->
                        spendingListStatusRow(
                            schNo = schoNo,
                            spendingStatus = spendingStatus,
                            navController = navHostController,
                            spendingAddViewModel = viewModel(),
                            record = SpendingRecord()
                        )
                    }
                }
            }
        }













    }
}

//結算清單列表
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun totalSumRow(
    totalSumStatus: TotalSum,
    navController: NavHostController,
    spendingListViewModel: SpendingListViewModel
) {
    var showText by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(black900) }


    if (totalSumStatus.totalSum.toInt() > 0) {
        showText = "應收帳款 >"
    } else {
        showText = "應付帳款 >"
    }

    if (totalSumStatus.totalSum.toInt() > 0) {
        color = green200
    } else {
        color = red200

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 0.dp)
            .clickable { navController.navigate(SPENDING_ADD_ROUTE) },

        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_member_01),
                contentDescription = "member icon",
                modifier = Modifier
                    .padding(0.dp, 0.dp, 12.dp, 0.dp)
                    .size(56.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 0.dp, 20.dp, 0.dp)
            ) {
                Text(
                    //分帳會員
                    text = totalSumStatus.userName,
                    modifier = Modifier
                        .width(90.dp),
                    color = black900,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    //應付帳款與應收帳款
                    text = showText,
                    modifier = Modifier
                        .width(110.dp)
                        .offset(-8.dp, 0.dp),
                    textAlign = TextAlign.Center,
                    color = black700,
                    fontSize = 15.sp,
                )
                Text(
                    //結算金額
                    text = totalSumStatus.totalSum,
                    modifier = Modifier
                        .width(80.dp),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )


            }

        }
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = white400,
        modifier = Modifier.padding(0.dp, 12.dp)
    )
}




// 消費明細列表
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun spendingListStatusRow(
    schNo: Int,
    spendingStatus: SpendingRecord,
    spendingAddViewModel: SpendingAddViewModel,
    navController: NavHostController,
    record: SpendingRecord
) {

    Log.d("TAG", "spendingListStatusRow: $spendingStatus")

    val numFormatter = DecimalFormat("#,###.##") // Double 僅保留兩位小數
    val memberNum = GetUid(MemberRepository)



    val classNametoString: Map<Int, String> = mapOf(
        -1 to "其他",
        1 to "食物",
        2 to "交通",
        3 to "票卷",
        4 to "住宿",
        5 to "購物",
        6 to "娛樂",
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 0.dp)
            .clickable {
                navController.navigate(
                    getSpendingAddNavigationRoute(
                        spendingStatus.schNo,
                        spendingStatus.costNo
                    )
                )
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

//            Log.d("TAG", "頭像圖(spendingStatus.paidBy): ${spendingStatus.paidBy}")
//            Log.d("TAG", "頭像圖(memberNum): ${memberNum}")

            Log.d("TAG", "頭像圖(memberNum): ${spendingStatus.paidByNo }")
            Log.d("TAG", "頭像圖(memberNum2): ${memberNum }")


                val newPic = memIcon()[spendingStatus.paidByNo].img

                Image(
//                painter = painterResource(R.drawable.ic_member_01),
                    painter = painterResource(newPic),
                    contentDescription = "member icon",
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 12.dp, 0.dp)
                        .size(56.dp)
                )









            Column(

            ) {
                Text(
                    //直接使用物件屬性
//                    text = RetrofitInstance.api.getSpendingList(),
                    //會員名稱
                    text = spendingStatus.paidByName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.padding(0.dp, 4.dp),
                ) {
                    Text(
                        //直接使用屬性，不是物件的，不要搞搞混
                        //消費類別
                        text = (classNametoString[spendingStatus.costType.toInt()] ?: ""),
                        color = black600,
                        fontSize = 15.sp,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = black500,
                        modifier = Modifier
                            .padding(8.dp, 4.dp)
                            .height(14.dp)
                    )

                    if (spendingStatus.costItem == "") {
                        Text(
                            //消費項目
                            text = "----",
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = black600,
                        )
                    } else {
                        Text(
                            //消費項目
                            text = spendingStatus.costItem,
                            fontSize = 15.sp,
                            color = black600
                        )
                    }


                }


                Text(
                    //消費時間
                    text = spendingStatus.crCostTime,
                    fontSize = 15.sp,
                    color = black600
                )

            }
            Column() {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {


                    Column(
                        horizontalAlignment = Alignment.End,


                        ) {

                        Text(
                            //消費金額
//                            text = spendingStatus.costPrice.toString(),
                            text = numFormatter.format(spendingStatus.costPrice),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp)

                        )

                        Text(
                            //均分金額/公費則為0
                            text = "= ${numFormatter.format(spendingStatus.costPrice/spendingStatus.countCrew)}",
                            fontSize = 15.sp,
                            color = black600,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End

                        )


                    }


                    Text(
                        //紀錄幣別
                        text = spendingStatus.crCurRecord,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
                    )


                }
                Text(
                    //均分人數
                    text = "Share by ${spendingStatus.countCrew} People",
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = black600,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 4.dp, 0.dp, 0.dp),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = white400,
        modifier = Modifier.padding(0.dp, 12.dp)
    )

}


//好像是多餘的，最後確認不用就直接刪掉
//@SuppressLint("StateFlowValueCalledInComposition")
//@Composable
//fun spendingResult(
//    navHostController: NavHostController,
//    spendingRecordVM: SpendingRecordVM,
//    spendingListViewModel: SpendingListViewModel,
//    spendingAddViewModel: SpendingAddViewModel,
//    totalSum: TotalSumVM,
//    totalSumStatus: TotalSum,
//    navController: NavHostController,
//    schoNo: Int
//) {
//
//    var showResultText by remember { mutableStateOf("") }
//
//    val crewList = spendingAddViewModel.tripCrew(schoNo)
//
//
//    //資料流，每一頁都可以動（新增修改），最後是把最新狀態撈出來。
////    val totalSumStatus by totalSum.totalSum.collectAsState()
////    Log.d("TAG", "spendingResult: $totalSumStatus")
//    //    Log.d("TAG", "totalSum: ${totalSum}")
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(white100)
//            .verticalScroll(rememberScrollState())
//    ) {
//        Column(
//            horizontalAlignment = Alignment.Start,
//            modifier = Modifier
//                .fillMaxWidth(),
//        ) {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp, 24.dp, 20.dp, 16.dp),
//                text = "結算清單",
//                fontSize = 17.sp
//            )
//
//
//        }
//    }
//}
