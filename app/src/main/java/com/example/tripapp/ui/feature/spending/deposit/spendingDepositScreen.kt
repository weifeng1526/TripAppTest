package com.example.tripapp.ui.feature.spending.deposit

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.spending.settinglist.SPENDING_SETLIST_ROUTE
import com.example.tripapp.ui.theme.black600
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white200
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400


@Composable
fun SpendingRoute(navController: NavHostController) {
    spendingDepositRoute(
        saveButtonClick ={
            navController.navigate(SPENDING_SETLIST_ROUTE)
        }

    )
}

@Preview
@Composable
fun PreviewSpendingRoute() {
    spendingDepositRoute()
}

@Composable
fun spendingDepositRoute(
    saveButtonClick: () -> Unit = {}
) {

    val context = LocalContext.current
    var moneyInput by remember { mutableStateOf("") }
    var ccyExpanded by remember { mutableStateOf(false) }
    var ccySelected by remember { mutableStateOf("日幣") }
    var inputCurrent by remember { mutableStateOf("JPY") }
    val ccyOptions = remember {
        mutableStateOf(
            mapOf(
                "日幣" to "JPY",
                "台幣" to "TWD"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(white100, white300)
                    ),
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp, 20.dp),
            ) {
                // Drop-down currency
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "支出金額 ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = black900
                    )
                    Button(
                        onClick = {
                            ccyExpanded = !ccyExpanded
//                            Toast.makeText(context, "幣別下拉選單", Toast.LENGTH_SHORT).show()
                        },
                        border = BorderStroke(2.dp, white400),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = purple300, containerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)

                    ) {
                        Text(
                            text = ccySelected,
                            color = purple300,
                        )
                        Image(
                            painter = painterResource(R.drawable.ic_popselect),
                            contentDescription = "pop",
                            modifier = Modifier
                                .size(22.dp)
                                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(68.dp, 52.dp, 0.dp, 0.dp)
                    ) {
                        DropdownMenu(
                            expanded = ccyExpanded,
                            onDismissRequest = { ccyExpanded = false },
                            modifier = Modifier
                                .width(160.dp)
                                .padding(20.dp, 0.dp),
                            containerColor = white100,
                            border = BorderStroke(1.dp, white200)

                        ) {
                            ccyOptions.value.forEach { (option: String, text: String) ->
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(56.dp)
                                        .clickable {
                                            ccySelected = option
                                            inputCurrent = text
//                                            Toast
//                                                .makeText(context, text, Toast.LENGTH_SHORT)
//                                                .show()
                                            ccyExpanded = false

                                        }
                                ) {
                                    Text(text = option)

                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                saveButtonClick()
                                Toast.makeText(context, "儲存", Toast.LENGTH_SHORT).show()

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = purple300,
                                contentColor = white100
                            ),
                            border = BorderStroke(
                                2.dp, purple400,
                            ),
                            modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                        ) {
                            Text(
                                text = "儲存",
                                fontSize = 15.sp
                            )

                        }

                    }

                }


                // spending money input
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(0.dp, 48.dp, 0.dp, 28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
//                        Text(
//                            text = "20,000",
//                            fontSize = 44.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = black900
//                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {

                            TextField(
                                value = moneyInput,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                onValueChange = { moneyInput = it },
                                label = {
                                    Text(
                                        text = "請輸入金額",
                                        textAlign = TextAlign.End,
                                        color = black600,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 20.dp, 0.dp, 0.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = white400,
                                    focusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = white400,
//                                    focusedIndicatorColor = purple200,
                                )
                            )
                        }

                        Text(
                            textAlign = TextAlign.End,
                            text = inputCurrent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp, 48.dp, 0.dp, 0.dp),
                            color = black900
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 2.dp, color = white400
            )


        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp,20.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(text = "2024/12/24  19:25",
                    fontSize = 16.sp)
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text =  "6,000",
                        fontSize = 20.sp,
                        color = black600,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(0.dp,0.dp,12.dp,0.dp))
                    Text(text = "JPY",
                        color = black600,
                        fontWeight = FontWeight.Medium
                        )
                }

            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = white400
            )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp,20.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(text = "2024/12/24  19:25",
                    fontSize = 16.sp)
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text =  "6,000",
                        fontSize = 20.sp,
                        color = black600,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(0.dp,0.dp,12.dp,0.dp))
                    Text(text = "JPY",
                        color = black600,
                        fontWeight = FontWeight.Medium
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