package com.example.tripapp.ui.feature.spending.addlist

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.spending.list.SPENDING_LIST_ROUTE
import com.example.tripapp.ui.theme.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.FormatStyle


@Composable
fun SpendingRoute(navHostController: NavHostController) {
    SpendingAddScreen(
        floatingButtonSaveClick = {
            //導頁專用語法
            navHostController.navigate(SPENDING_LIST_ROUTE)
        },
        saveButtonClick = {
            navHostController.navigate(SPENDING_LIST_ROUTE)
        }
    )

}

@Preview
@Composable
fun PreviewSpendingRoute() {
    SpendingAddScreen()
}


//@OptIn(ExperimentalMaterial3Api::class)
//fun DatePicker(state: DatePickerState, colors: DatePickerColors) {
//    val customColors = DatePickerColors(
//        containerColor = Color.LightGray,
//        selectedDayContentColor = Color.Blue,
//        dividerColor = Color.Gray
//    )
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun SpendingAddScreen(
    floatingButtonSaveClick: () -> Unit = {},
    saveButtonClick: () -> Unit = {},
//    DatePicker: (DatePickerState, DatePickerColors) -> Unit = {}


) {
    val context = LocalContext.current
    var ccySelected by remember { mutableStateOf("日幣") }
    var moneyInput by remember { mutableStateOf("") }
    var inputCurrent by remember { mutableStateOf("JPY") }

    var payByExpanded by remember { mutableStateOf(false) }
    var payBySelect by remember { mutableStateOf("Rubyyyyyer") }

    var itemName by remember { mutableStateOf("") }
//    var itemNameImg by remember { mutableStateOf("") }

    //日期
    var showDatePickerDialog by remember { mutableStateOf(false) }
//    var spendTime by remember { mutableStateOf("") }
    var spendTime by remember { mutableStateOf("") }


    var swSplit by remember { mutableStateOf(false) }
    var txSplitMethod by remember { mutableStateOf("（公費支出）") }
    //下拉選單
    var ccyExpanded by remember { mutableStateOf(false) }
    var selectedClassname by remember { mutableStateOf("") }


    val btnSpendingClass = remember {
        mutableStateOf(
            mapOf(
                "食物" to "ic_cat_food",
                "交通" to "ic_cat_tfc",
                "票券" to "ic_cat_tix",
                "住宿" to "ic_cat_hotel",
                "購物" to "ic_cat_shop",
                "娛樂" to "ic_cat_ent",
                "其他" to "ic_cat_other"

            )
        )
    }

    val chmember = remember {
        mutableStateOf(
            mapOf(
                "ruby" to false,
                "selin" to false,
                "sean" to false,
                "brown" to false,
                "cony" to false
            )
        )
    }

//    var ccyOptions = listOf(
//        "日幣",
//        "台幣"
//    )


    val ccyOptions = remember {
        mutableStateOf(
            mapOf(
                "日幣" to "JPY",
                "台幣" to "TWD"
            )
        )
    }


    var payByOptions = listOf(
        "Rubyyyyyer",
        "Sean",
        "Selin",
        "Brown",
        "Cony"
    )

    var selectedClassimg: String? by remember { mutableStateOf(null) }


//    var cbCrew by remember { mutableStateOf(false) }

    // full screen (no padding)
    Column(
        modifier = Modifier
            .background(white100)

    ) {
        // spending money input area
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(white100, white300)
                    ),
                )
        ) {
            // money input
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
                            fontSize = 15.sp
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
                                    Text(text = option,
                                        fontSize = 15.sp)

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
//                                showDatePickerDialog = true
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
        // pay by
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    payByExpanded = !payByExpanded
//                    Toast.makeText(context, "付款人下拉選單", Toast.LENGTH_SHORT).show()
                },
                border = BorderStroke(2.dp, white400),
                colors = ButtonDefaults.buttonColors(
                    containerColor = white100, contentColor = purple300
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp)
                    .offset(x = 0.dp, y = (-24).dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "付款人", fontSize = 15.sp
                    )
                    Text(
                        text = payBySelect,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_popselect),
                            contentDescription = "pop",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

            }
            Box(
                modifier = Modifier
                    .offset(x = 36.dp, y = (-20).dp)


            ) {

                DropdownMenu(
                    expanded = payByExpanded,
                    onDismissRequest = { payByExpanded = false },
                    modifier = Modifier
                        .width(340.dp)
                        .padding(24.dp,0.dp,0.dp, 0.dp),
                    containerColor = white100,
                    border = BorderStroke(1.dp, white200)


                ) {
                    payByOptions.forEach {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .height(60.dp)
                                .clickable {
                                    payByExpanded = false
                                    payBySelect = it
//                                    Toast
//                                        .makeText(context, it, Toast.LENGTH_SHORT)
//                                        .show()
                                }
                        ) {
                            Text(text = it,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = purple300,
                                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
                        }
                    }
                }
            }
        }


        // spending money info area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp, 0.dp)
                .offset(x = 0.dp, y = (-4).dp),
        ) {
            // spending class
            Text(
                text = "消費類別",
                fontSize = 16.sp,
                color = black900,
                modifier = Modifier.padding(4.dp, 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
            ) {
// test Start --------------------------------------------------
//
//LazyColumn() {
//    items(btnSpendingClass.value.entries.toList()) {(className: String, img: String) ->
//        // entry 就是一個 Map.Entry<String, String>
////        val className = entry // 取得鍵（類別名稱）
////        val img = entry.value  // 取得值（圖片名稱）
//        Image(
//            painter = painterResource(img.toInt()),
//            contentDescription = "aaa"
//        )
//
//
//    }
//}


//                btnSpendingClass.value.forEach { (className: String, img: String) ->
//                    if (selectedClassimg == className) {
//                        selectedClassname = className
//                        Image(painter = painterResource(("${img}_s").toInt()),
//                            contentDescription = "food",
//                            modifier = Modifier
//                                .padding(0.dp, 0.dp, 4.dp, 0.dp)
//                                .size(46.dp)
//                                .clip(CircleShape)
//                                .clickable {
//                                    selectedClassimg = null
//                                    Toast
//                                        .makeText(context, className, Toast.LENGTH_SHORT)
//                                        .show()
//                                })
//                    } else {
//                        selectedClassname = ""
//                        Image(painter = painterResource(("${img}").toInt()),
//                            contentDescription = "food",
//                            modifier = Modifier
//                                .padding(0.dp, 0.dp, 4.dp, 0.dp)
//                                .size(46.dp)
//                                .clip(CircleShape)
//                                .clickable {
//                                    selectedClassimg = className
//                                    Toast
//                                        .makeText(context, className, Toast.LENGTH_SHORT)
//                                        .show()
//                                })
//                    }
//                }

// test End --------------------------------------------------
// 食物 Start --------------------------------------------------
                if (selectedClassimg == "食物") {
                    selectedClassname = "食物"
                    Image(painter = painterResource(R.drawable.ic_cat_food_s),
                        contentDescription = "food",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "食物", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_food),
                        contentDescription = "food",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "食物"
                                Toast
                                    .makeText(context, "食物", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }
// 食物 End --------------------------------------------------
// 交通 Start --------------------------------------------------
                if (selectedClassimg == "交通") {
                    selectedClassname = "交通"
                    Image(painter = painterResource(R.drawable.ic_cat_tfc_s),
                        contentDescription = "tfc",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "交通", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_tfc),
                        contentDescription = "tfc",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "交通"
                                Toast
                                    .makeText(context, "交通", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }


// 交通 End --------------------------------------------------
// 交通 Start --------------------------------------------------
                if (selectedClassimg == "票券") {
                    selectedClassname = "票券"
                    Image(painter = painterResource(R.drawable.ic_cat_tix_s),
                        contentDescription = "tix",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "票券", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_tix),
                        contentDescription = "tix",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "票券"
                                Toast
                                    .makeText(context, "票券", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }


// 交通 End --------------------------------------------------
// 住宿 Start --------------------------------------------------
                if (selectedClassimg == "住宿") {
                    selectedClassname = "住宿"
                    Image(painter = painterResource(R.drawable.ic_cat_hotel_s),
                        contentDescription = "hotel",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "住宿", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_hotel),
                        contentDescription = "hotel",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "住宿"
                                Toast
                                    .makeText(context, "住宿", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }

// 住宿 End --------------------------------------------------
// 購物 Start --------------------------------------------------
                if (selectedClassimg == "購物") {
                    selectedClassname = "購物"
                    Image(painter = painterResource(R.drawable.ic_cat_shop_s),
                        contentDescription = "shopping",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "購物", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_shop),
                        contentDescription = "shopping",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "購物"
                                Toast
                                    .makeText(context, "購物", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }

// 購物 End --------------------------------------------------
// 娛樂 Start --------------------------------------------------
                if (selectedClassimg == "娛樂") {
                    selectedClassname = "娛樂"
                    Image(painter = painterResource(R.drawable.ic_cat_ent_s),
                        contentDescription = "entertainment",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "娛樂", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_ent),
                        contentDescription = "entertainment",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "娛樂"
                                Toast
                                    .makeText(context, "娛樂", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }

// 娛樂 End --------------------------------------------------
// 其他 Start --------------------------------------------------
                if (selectedClassimg == "其他") {
                    selectedClassname = "其他"
                    Image(painter = painterResource(R.drawable.ic_cat_other_s),
                        contentDescription = "other",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = null
                                Toast
                                    .makeText(context, "其他", Toast.LENGTH_SHORT)
                                    .show()
                            })
                } else {
                    selectedClassname = ""
                    Image(painter = painterResource(R.drawable.ic_cat_other),
                        contentDescription = "other",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 4.dp, 0.dp)
                            .size(47.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedClassimg = "其他"
                                Toast
                                    .makeText(context, "其他", Toast.LENGTH_SHORT)
                                    .show()
                            })
                }

// 其他 End --------------------------------------------------


            }
            // item name
            TextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = {
                    Text(
                        text = "名稱", color = black900, fontSize = 18.sp
                    )
                    Text(
                        text = "點擊以編輯 (選填)",
                        color = black600,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(52.dp, 0.dp, 0.dp, 0.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp),

                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = white100,
                    unfocusedIndicatorColor = white400,
                    focusedContainerColor = white100,
                    focusedIndicatorColor = purple200,
                )
            )
            // time

            TextField(
                modifier = Modifier
                    .clickable {
                        Log.d("TAG", "SpendingAddScreen: $spendTime ")
                        showDatePickerDialog = true
//                        Toast.makeText(context, "日期選擇器", Toast.LENGTH_SHORT)
                    }
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 0.dp),


                value = spendTime,
                onValueChange = { spendTime = it },
                label = {
                    Text(
                        text = "時間",
                        color = black900,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                Log.d("TAG", "SpendingAddScreen: $spendTime ")
                                showDatePickerDialog = true
//                        Toast.makeText(context, "日期選擇器", Toast.LENGTH_SHORT)
                            },
                    )
                    Text(
                        text = "點擊以編輯 (選填)",
                        color = black600,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(52.dp, 0.dp, 0.dp, 0.dp)
                            .clickable {
                                Log.d("TAG", "SpendingAddScreen: $spendTime ")
                                showDatePickerDialog = true
//                        Toast.makeText(context, "日期選擇器", Toast.LENGTH_SHORT)
                            },
                    )
                },


                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 20.sp
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = white100,
                    unfocusedIndicatorColor = white400,
                    focusedContainerColor = white100,
                    focusedIndicatorColor = purple200,
                )
            )






            if (showDatePickerDialog) {
                MyDatePickerDialog(
                    onDismissRequest = {
                        spendTime = "Clicking outside or pressing the back button."
                        showDatePickerDialog = false
                    },
                    // 確定時會接收到選取日期
                    onConfirm = { utcTimeMillis ->
//                        spendTime = "Selected date: ${
//                            utcTimeMillis?.let {
//                                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC"))
//                                    .toLocalDate().format(ofLocalizedDate(FormatStyle.MEDIUM))
//                            } ?: "no selection"
//                        }"


                        spendTime = "${
                            utcTimeMillis?.let {
                                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC"))
                                    .toLocalDate().format(ofLocalizedDate(FormatStyle.MEDIUM))
                            } ?: ""
                        }"


                        showDatePickerDialog = false
                    },
                    // 設定取消時欲執行內容
                    onDismiss = {
//                        spendTime = "Cancelled"
                        showDatePickerDialog = false
                    }
                )
            }


            // title + switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(20.dp, 20.dp, 20.dp, 0.dp)
                    .fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "分帳方式", fontSize = 16.sp
                        )
                        Text(
                            text = txSplitMethod, fontSize = 16.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Switch(
                                checked = swSplit,
                                onCheckedChange = {
                                    swSplit = it
                                    if (swSplit) {
                                        txSplitMethod = "（均分）"
                                        chmember.value.forEach { name, isChecked ->
                                            chmember.value = chmember.value.toMutableMap().apply {
                                                this[name] = true
                                            }
                                        }

                                    } else {
                                        txSplitMethod = "（公費支出）"
                                        chmember.value.forEach { name, isChecked ->
                                            chmember.value = chmember.value.toMutableMap().apply {
                                                this[name] = false
                                            }
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = white100,
                                    checkedTrackColor = purple100,
                                    uncheckedThumbColor = white100,
                                    uncheckedTrackColor = black300,
                                    uncheckedBorderColor = Color.Transparent,
                                )
                            )
                        }
                    }
                }
            }
            //check-box
            Column(
                modifier = Modifier
                    .padding(20.dp, 0.dp)
                    .fillMaxWidth()
            ) {
                checkBoxMemberList(chmember, floatingButtonSaveClick)
            }
        }
    }
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp)
//
//    ) {
//        FloatingActionButton(
//            onClick = floatingButtonSaveClick,
//            containerColor = purple200,
//            shape = RoundedCornerShape(50),
//            modifier = Modifier.align(Alignment.BottomEnd)
//
//        ) {
//            Image(
//                painter = painterResource(R.drawable.ic_save),
//                contentDescription = "add",
//                modifier = Modifier
//                    .size(33.dp)
//            )
//        }
//    }
}

@Composable
private fun checkBoxMemberList(
    chmember: MutableState<Map<String, Boolean>>,
    floatingButtonSaveClick: () -> Unit = {},
) {
    chmember.value.forEach { (name, isChecked) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp, 0.dp, 0.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_member_01),
                contentDescription = "member icon",
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.dp, 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {

                        chmember.value = chmember.value.toMutableMap().apply {
                            this[name] = it
                            Log.d("TAG_check", "test2$name : $it ")
                        }
                    }, colors = CheckboxDefaults.colors(
                        uncheckedColor = black300,
                        checkedColor = purple300,
                    )
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        // SelectableDates介面用來限制可選擇的日期與年
        selectableDates = object : SelectableDates {
            // 將顯示的日期逐一傳給utcTimeMillis參數，回傳true代表該日可選；false代表該日不可選
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                /* 將utcTimeMillis轉成LocalDate物件後取出星期幾的資訊，API 26開始支援Instant */
                val dayOfWeek = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC"))
                    .toLocalDate().dayOfWeek
                // 設定週六日不可選擇
                return dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.SATURDAY
            }

            // 將顯示的年逐一傳給year參數，回傳true代表該年可選；false代表該年不可選
            override fun isSelectableYear(year: Int): Boolean {
                return year >= 2024
            }
        }
    )



    DatePickerDialog(
        // 點擊對話視窗外部或back按鈕時呼叫，並非點擊dismissButton時呼叫
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                // 點擊確定按鈕時呼叫onConfirm(Long?)並將選取日期傳入以回饋給原畫面
                onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                }
            ) {
                Text("OK")
            }
        },
        // 設定取消按鈕
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(black1000.copy(alpha = 0.8f)),


        ) {


        DatePicker(
            state = datePickerState
        )


    }


}

