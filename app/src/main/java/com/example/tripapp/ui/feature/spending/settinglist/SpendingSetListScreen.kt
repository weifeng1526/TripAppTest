package com.example.tripapp.ui.feature.spending.settinglist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.spending.addlist.SPENDING_ADD_ROUTE
import com.example.tripapp.ui.feature.spending.deposit.SPENDING_DEPOSIT_ROUTE
import com.example.tripapp.ui.feature.spending.setting.SPENDING_SET_ROUTE
import com.example.tripapp.ui.theme.black700
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.white400


@Composable
fun SpendingRoute(
    navHostController: NavHostController,
) {
    SpendingSetListScreen(
        spendingSettingDepositBtn = {
            //導頁專用語法
            navHostController.navigate(SPENDING_DEPOSIT_ROUTE)
        },
    )
}

@Preview
@Composable
fun PreviewSpendingRoute() {
    SpendingSetListScreen()
}

@Composable
fun SpendingSetListScreen(
    spendingSettingDepositBtn: () -> Unit = {}
) {

    val setting = mutableListOf(
        "出遊幣別",
        "結算幣別",
        "分帳方式",
        "預設匯率",
        "公費餘額"
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {


//            when(setting){
//                "出遊幣別" -> currencyStart(),
//                "結算幣別" -> currencyEnd(),
//                "分帳方式" -> splitMethod(),
//                "預設匯率" -> exchangeRate(),
//                "公帳儲值" -> depositRecord(),
//
//            }

            //forEach
//            setting.forEachIndexed { index: Int, title: String ->
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//
//                        }
//                        .padding(20.dp)
//                ) {
//                    Text(text = setting[index])
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        Image(
//                            painter = painterResource(R.drawable.ic_next),
//                            contentDescription = "next"
//                        )
//                    }
//                }
//                HorizontalDivider(
//                    thickness = 1.dp,
//                    color = white400
//                )
//            }


            // [0] 出遊幣別
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
//                        .clickable {}
                    .padding(20.dp, 20.dp, 28.dp, 20.dp)
            ) {
                Text(text = setting[0],
                    fontSize = 15.sp,)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "日幣",
                        color = black700,
                        fontSize = 16.sp,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = white400
            )

// [1] 結算方式
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
//                    .clickable {}
                    .padding(20.dp, 20.dp, 28.dp, 20.dp)
            ) {
                Text(text = setting[1],
                    fontSize = 15.sp,)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "台幣",
                        color = black700,
                        fontSize = 16.sp,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = white400
            )

// [2] 分帳方式
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
//                    .clickable {}
                    .padding(20.dp, 20.dp, 28.dp, 20.dp)
            ) {
                Text(text = setting[2],
                    fontSize = 15.sp,)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "均分",
                        color = black700,
                        fontSize = 16.sp,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = white400
            )


            // [3] 預設匯率
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
//                    .clickable {}
                    .padding(20.dp, 20.dp, 28.dp, 20.dp)
            ) {
                Text(text = setting[3],
                    fontSize = 15.sp,)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "結算時間",
                        color = black700,
                        fontSize = 16.sp,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = white400
            )

// [4] 公帳儲值
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { spendingSettingDepositBtn() }
                    .padding(20.dp, 20.dp, 20.dp, 20.dp)
            ) {
                Text(text = setting[4],
                    fontSize = 15.sp,)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        Text(
                            text = "12,000",
                            color = black700,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 20.dp, 0.dp)

                        )
                        Image(
                            painter = painterResource(R.drawable.ic_next),
                            contentDescription = "next"
                        )


                    }

                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = white400
            )


        }
    }
}