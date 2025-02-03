package com.example.tripapp.ui.feature.shop

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tripapp.R
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.red200
import com.example.tripapp.ui.theme.white300

@Composable
fun OrderListScreen(
    navController: NavHostController,
    memberId: Int,
    productVM: ProductVM,
    orderVM: OrderVM,
    tabVM: TabVM
) {
    // TabRow顯示與否
    tabVM.updateTabState(true)

//    val memNo = 1
    // 從StateFlow取得最新資料
    val orders by orderVM.ordersState.collectAsState()
    val filteredOrders = orders.filter { it.memNo == memberId } // 根據會員篩選
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputText,
                onValueChange = { inputText = it },
                // 在此placeholder較label(會將提示文字上移)適合
                placeholder = { Text(text = stringResource(id = R.string.product_name)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "search"
                    )
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            inputText = ""
                        })
                },
                maxLines = 1,
                shape = RoundedCornerShape(16.dp),
            )
        }
        // 一定要套用innerPadding，否則內容無法跟TopAppBar對齊
        OrderLists(
            filteredOrders.filter { it.prodName.contains(inputText, true) },
            // 項目被點擊時執行
            onItemClick = {
                Log.d("tag_", "onItemClick")
                // 將點擊的order存入orderVM，然後切換至productDetail頁面
                orderVM.setDetailOrder(it)
                Log.d("tag_", "setDetailProduct")
                navController.navigate(Screen.ProductDetail.name)
            },
            onDeleteClick = { order ->
                orderVM.removeOrder(order.ordNo) // 呼叫 ViewModel 的刪除函式
                Log.d("tag_", "已刪除訂單: ${order.ordNo}")
            },
            navController
        )
    }
}

/**
 * 列表內容
 * @param orders 欲顯示的商品清單
 */
@Composable
fun OrderLists(
    orders: List<Order>,
    onItemClick: (Order) -> Unit,
    onDeleteClick: (Order) -> Unit,
    navController: NavHostController
) {
    // 在搜尋框下添加橫條
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),  // 卡片的內邊距
        elevation = 4.dp,  // 設定陰影深度
        shape = RoundedCornerShape(16.dp),  // 圓角形狀
        backgroundColor = purple100  // 設定背景顏色
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),  // Row 內部的間距
            horizontalArrangement = Arrangement.End  // 讓按鈕靠右對齊
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.ProductList.name)                },
                colors = ButtonDefaults.buttonColors(containerColor = purple400)
            ) {
                Text(text = "繼續購物", color = white300)
            }
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(orders) { order ->

            Log.d("OrderLists", "prodPic URL: ${order.prodPic}") // 記錄 URL
            Log.d("OrderLists", "Order Object: $order")

            var showDialog by remember { mutableStateOf(false) } // 控制對話框顯示與隱藏的狀態

            // 顯示AlertDialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "確認刪除") },
                    text = { Text("您確定要刪除此訂單嗎？") },
                    confirmButton = {
                        Button(
                            onClick = {
                                onDeleteClick(order)  // 執行刪除
                                showDialog = false // 關閉對話框
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = red200) // 設定確認按鈕顏色
                        ) {
                            Text("確定刪除", color = white300)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false // 取消刪除
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = purple200) // 設定取消按鈕顏色
                        ) {
                            Text("取消", color = white300)
                        }
                    }
                )
            }

            //使用 Card 包裝每個項目，提供陰影和圓角效果
            Card(
                modifier = Modifier
                    .padding(16.dp),  // Card 的內邊距
                elevation = 4.dp,  // 設定陰影深度
                shape = RoundedCornerShape(8.dp)  // 圓角形狀
            ) {
                // 使用 Column 排列圖片和文字
                Column(
                    modifier = Modifier.padding(16.dp)  // Card 內部的內邊距
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(order.prodPic)  // prodPic 可以是 URL 或 Base64 字符串
                            .crossfade(true)
                            .memoryCacheKey(order.prodPic)
                            .diskCacheKey(order.prodPic)
                            .build(),
                        contentDescription = "product",
                        modifier = Modifier
                            .fillMaxWidth()  // 圖片佔滿寬度
                            .height(200.dp), // 設定圖片高度
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))  // 在圖片和文字之間留一些空間

                    // 顯示產品名稱和價格
                    Column(
                        horizontalAlignment = Alignment.Start,  // 讓文字居中
                        verticalArrangement = Arrangement.spacedBy(4.dp)  // 文字之間的間距
                    ) {
                        Text(
                            text = "訂單編號 : " + order.ordNo.toString(),
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.h6,
                            color = purple200
                        )
                        Text(
                            text = order.prodName,
                            style = MaterialTheme.typography.h6,
                            color = purple400
                        )
                    }
                }
                // 在右上角放置刪除按鈕
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.End // 水平靠右
                ) {
                    androidx.compose.material.Button (
                        onClick = {
                            Log.d("OrderLists", "刪除按下: ${order.prodName}")
                            showDialog = true  // 顯示確認刪除的對話框
                        },
                        modifier = Modifier
                            .align(Alignment.End) // 將按鈕對齊到右下角
                            .padding(8.dp), // 按鈕與邊框的間距
                        colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = purple400)
                    ) {
                        Text(text = "刪除", color = white300)
                    }
                }
            }
            HorizontalDivider()
        }
    }
}
