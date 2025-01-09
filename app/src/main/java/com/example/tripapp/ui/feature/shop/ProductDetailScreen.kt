package com.example.tripapp.ui.feature.shop

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tripapp.R


@SuppressLint("ResourceAsColor", "SuspiciousIndentation")
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productVM: ProductVM,
    tabVM: TabVM,
    orderVM: OrderVM // 引入訂單 ViewModel
//    currentUser: User
) {
    // TabRow顯示與否
    tabVM.updateTabState(true)

    // 在離開時恢復 TabRow 狀態
    DisposableEffect(Unit) {
        onDispose {
            tabVM.updateTabState(true)  // 恢復 TabRow 顯示
        }
    }
    // 取得productVM內儲存的產品詳細資料
    val product by productVM.productDetailState.collectAsState()

    var showCardDialog by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
//    var isSubmitting by remember { mutableStateOf(false) } // 加載狀態

//    val memberId = currentUser.id


    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .crossfade(true)
            .build()

        val request = ImageRequest.Builder(LocalContext.current)
            .data(product.prodPic)  // 圖片 URL 或本地資源
            .size(600, 600)  // 強制縮放圖片到最大為 600x600 像素
            .target(
                onSuccess = { result ->
                    // 成功處理圖片
                    Log.d("ImageLoad", "Image loaded successfully!")
                },
                onError = { error ->
                    // 錯誤處理
                    Log.d("ImageLoad", "Error loading image")
                }
            )
            .build()

        // 使用 imageLoader 加載圖片
        imageLoader.enqueue(request)

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
        // 顯示產品圖片
//        val painter = painterResource(product.prodPic)
//        Image(
//            painter = painter,
//            contentDescription = "Product image",
//            modifier = Modifier.size(150.dp), // 控制圖片大小
//            contentScale = ContentScale.Crop
//        )
        Spacer(modifier = Modifier.height(16.dp))

        // 顯示產品詳細資訊
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = stringResource(id = R.string.productNum_format, product.prodNo))
            Text(text = stringResource(id = R.string.productname_format, product.prodName))
            Text(text = stringResource(id = R.string.price_format, product.prodPrice))
            Text(text = stringResource(id = R.string.longDescription_format, product.prodDesc)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),  // 設置按鈕之間的間隔
            verticalAlignment = Alignment.CenterVertically // 垂直對齊
        ) {
            // 購買按鈕
            Button(
                onClick = { showCardDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(R.color.purple_100)
                ),
                modifier = Modifier.weight(1f) // 按鈕均等分配空間
            ) {
                Text(text = stringResource(id = R.string.button_buy))
            }

            // 返回按鈕
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(R.color.purple_100)
                ),
                modifier = Modifier.weight(1f) // 按鈕均等分配空間
            ) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        }

        if (showCardDialog) {
            AlertDialog(
                onDismissRequest = { showCardDialog = false },  // 點擊外部區域或按返回鍵關閉對話框
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("輸入信用卡資訊", modifier = Modifier.padding(bottom = 16.dp))
                        TextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text(text = "信用卡號碼", color = colorResource(id = R.color.purple_200))},
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = expirationDate,
                            onValueChange = { expirationDate = it },
                            label = { Text(text = "到期日 (MM/YY)") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = cvv,
                            onValueChange = { cvv = it },
                            label = { Text(text = "CVV") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // 驗證信用卡資料
                        if (cardNumber.isNotEmpty() && expirationDate.isNotEmpty() && cvv.isNotEmpty()) {
                            // 建立 OrderRequest
                            val orderRequest = OrderRequest(
                                memNo = 1, // 假設的會員編號，需動態獲取
                                prodNo = product.prodNo,
                                prodName = product.prodName, // 產品名稱
                                prodPrice = product.prodPrice, // 產品價格
                                cardNo = cardNumber, // 信用卡號
                                expDate = expirationDate, // 到期日
                                cvv = cvv // CVV
                            )
                            // 呼叫函式將訂單提交到資料庫
                            orderVM.submitOrderToDatabase(orderRequest)

                            // 將頁面導航到訂單成立頁面
                            navController.navigate(Screen.Order.name)
                        } else {
                            // 提示用戶填寫完整信用卡資訊
                            Log.e("Validation", "信用卡資訊未填完整")
                        }
                    }) {
                        Text("確認購買")
                    }

//                    Button(
//                        onClick = {
//                            if(cardNumber.isNotEmpty() && expirationDate.isNotEmpty() && cvv.isNotEmpty())
//                            isSubmitting = true
//                            // 轉支付頁面
//                            showCardDialog = false
//
//                            // 新增訂單並提交到資料庫
//                            orderVM.addOrder(
//                                memNo = 1, // 假設的會員編號
//                                prodNo = product.prodNo,
//                                prodName = product.prodName, // 產品名稱
//                                prodPrice = product.prodPrice, // 產品價格
//                                cardNo = cardNumber, // 信用卡號
//                                expDate = expirationDate, // 到期日
//                                cvv = cvv, // CVV
//                                isSubmitted = false // 預設為未提交
//                                )
//                            showCardDialog = false
//                            // 轉至訂單成立頁面
//                            Log.d("Navigation", "Navigating to order screen")
//                            navController.navigate(Screen.Order.name)
//                        },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(R.color.purple_100),
//                            contentColor = colorResource(id = R.color.white)
//                        )
//                    ) {
//                        Text(text = "確認購買")
//                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showCardDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(R.color.purple_100),
                            contentColor = colorResource(id = R.color.white)
                        )
                    ) {
                        Text(text = "取消")
                    }
                }
            )
        }
    }
}
