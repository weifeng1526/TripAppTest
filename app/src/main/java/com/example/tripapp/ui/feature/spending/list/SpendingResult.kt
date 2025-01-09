package com.example.tripapp.ui.feature.spending.list

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.tripapp.ui.feature.spending.TotalSum
import com.example.tripapp.ui.feature.spending.TotalSumVM
import com.example.tripapp.ui.feature.spending.addlist.SPENDING_ADD_ROUTE
import com.example.tripapp.ui.theme.black500
import com.example.tripapp.ui.theme.black600
import com.example.tripapp.ui.theme.black700
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white400


@Preview
@Composable
fun spendingResultPre() {
    spendingResult(
        rememberNavController()
    )
}



@Composable
fun spendingResult(
    navHostController: NavHostController,
//    spendingRecordVM: SpendingRecordVM = viewModel(),
    totalSum: TotalSumVM = viewModel(),
//    totalSumVM: List<TotalSumVM> = listOf()
) {


    //資料流，每一頁都可以動（新增修改），最後是把最新狀態撈出來。
    val totalSumStatus by totalSum.totalSum.collectAsState()
    Log.d("TAG", "spendingResult: $totalSumStatus")
    //    Log.d("TAG", "totalSum: ${totalSum}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)
            .verticalScroll(rememberScrollState())
    ) {
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
                fontSize = 17.sp
            )

            Column {
                totalSumStatus.forEach {
                    totalSumRow(it, navController = navHostController)
                }


            }

        }


    }
}


    @Composable
    fun totalSumRow(
        totalSumStatus: TotalSum,
        navController: NavHostController
    ) {
        var showText by remember { mutableStateOf("ssssss") }


            if (totalSumStatus.totalSum.toInt() > 0){
                showText = "應收帳款 >"
            }  else {showText = "應付帳款 >"}


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
                                .padding(12.dp,0.dp,20.dp,0.dp)
                ) {
                    Text(text = totalSumStatus.userName,
                        modifier = Modifier
                            .width(72.dp),
                        color = black900,
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        )
                    Text(text = showText,
                        modifier = Modifier
                            .width(110.dp),
                        textAlign = TextAlign.Center,
                        color = black700,
                        fontSize = 15.sp,)
                    Text(text = totalSumStatus.totalSum,
                        modifier = Modifier
                            .width(72.dp),
                        textAlign = TextAlign.End,
                        fontSize = 16.sp)



                }

            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = white400,
            modifier = Modifier.padding(0.dp, 12.dp)
        )

    }
