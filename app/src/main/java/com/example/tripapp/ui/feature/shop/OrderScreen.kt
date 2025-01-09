package com.example.tripapp.ui.feature.shop

import androidx.collection.OrderedScatterSet
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tripapp.R
import com.example.tripapp.ui.feature.shop.Screen


@Composable
fun OrderScreen(
    navController: NavHostController,
    productVM: ProductVM,
    tabVM: TabVM
) {
    // 取得productVM內儲存的產品詳細資料
    val product by productVM.productDetailState.collectAsState()
    // 隱藏 TabRow
    tabVM.updateTabState(true)

    // 在離開時恢復 TabRow 狀態
    DisposableEffect(Unit) {
        onDispose {
            tabVM.updateTabState(true)  // 恢復 TabRow 顯示
        }
    }


    // 訂單頁面的內容
    LazyColumn(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize() // 設置內容填滿整個螢幕
    ) {
        item {
            // 訂單成功訊息
            Text(
                text = "恭喜您購買成功！",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF424347),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 顯示訂單編號
            Text(
                text = "訂單編號: #12345678", // 可以根據實際情況動態顯示訂單編號
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF999999),
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 顯示訂單詳情（可以根據需求進一步擴展）
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.prodPic)  // prodPic 可以是 URL 或 Base64 字符串
                        .crossfade(true)
                        .build(),
                    contentDescription = "product",
                    modifier = Modifier
                        .fillMaxWidth()  // 圖片佔滿寬度
                        .height(250.dp), // 設定圖片高度
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))  // 在圖片和文字之間留一些空間

                Text(
                    text = "訂單詳情:",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424347),
                    )
                )
                Text("產品名稱: 測試產品")
                Text("數量: 1")
                Text("總金額: NT$ 500")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 加入景點按鈕，點擊後返回到會員景點收藏頁面
            Button(
                onClick = {
                    navController.popBackStack(
                        Screen.ProductList.name,
                        false
                    ) // 返回上一頁，即 ProductDetailScreen
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.purple_100),
                    contentColor = colorResource(id = R.color.white)
                ),
                modifier = Modifier.fillMaxWidth() // 按鈕寬度填滿
            ) {
                Text(text = "加入景點收藏")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 返回按鈕，點擊後返回到產品詳情頁面
            Button(
                onClick = {
                    navController.popBackStack(
                        Screen.ProductList.name,
                        false
                    ) // 返回上一頁，即 ProductDetailScreen
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.purple_200),
                    contentColor = colorResource(id = R.color.white)
                ),
                modifier = Modifier.fillMaxWidth() // 按鈕寬度填滿
            ) {
                Text(text = "返回購物")
            }
        }
    }
}

    @Preview(showBackground = true)
    @Composable
    fun OrderScreenPreview() {
        OrderScreen(
            navController = rememberNavController(),
            productVM = ProductVM(),
            tabVM = TabVM()
        )
    }