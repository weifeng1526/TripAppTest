package com.example.tripapp.ui.feature.shop

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.home.MemberViewModel
import com.example.tripapp.ui.feature.member.login.MEMBER_LOGIN_ROUTE
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModel
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModelFactory
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.white300
import com.google.gson.Gson


@SuppressLint("ResourceAsColor", "SuspiciousIndentation")
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productVM: ProductVM,
    tabVM: TabVM,
    orderVM: OrderVM,
    memberVM: MemberViewModel
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
    var showLoginDialog by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // 儲存格式錯誤訊息
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var expirationDateError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }

    // 用來儲存完整訂單物件的狀態 (一開始為 null)
    var completeOrder by remember { mutableStateOf<Order?>(null) }

    // 驗證信用卡號碼格式
    fun isCardNumberValid(cardNumber: String) = cardNumber.matches(Regex("\\d{16}"))

    // 驗證到期日格式
    fun isExpirationDateValid(expirationDate: String) =
        expirationDate.matches(Regex("(0[1-9]|1[0-2])/\\d{2}"))

    // 驗證 CVV 格式
    fun isCvvValid(cvv: String) = cvv.matches(Regex("\\d{3}"))

    // LaunchedEffect，當 completeOrder 更新時觸發導航
    LaunchedEffect(key1 = completeOrder) {
        if (completeOrder != null) {
            // 將 completeOrder 轉換為 JSON 並導航
            val orderJson = Gson().toJson(completeOrder)
            val encodedOrderJson = Uri.encode(orderJson)
            val route = "OrderScreen/$encodedOrderJson"
            navController.navigate(route)
        }
    }

    // 取得 Context 物件
    val context = LocalContext.current
    // 建立 MemberLoginViewModelFactory 的實例
    val factory = MemberLoginViewModelFactory(context)
    // 取得 MemberLoginViewModel 的實例
    val memberLoginViewModel: MemberLoginViewModel = viewModel(factory = factory)
    // 使用 collectAsState() 收集 isLoginSuccess 的值以取得會員登入狀態

    // 取得會員電郵與密碼的狀態
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
//    var isSubmitting by remember { mutableStateOf(false) } // 加載狀態

//    val memberId = currentUser.id

    val memberId by memberLoginViewModel.uid.collectAsState()
    val isloginSuccess = memberId != 0

    Log.e("ProductDetail", "memberId: $memberId")

    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        val imageLoader = ImageLoader.Builder(LocalContext.current)
//            .crossfade(true)
//            .build()
//
//        val request = ImageRequest.Builder(LocalContext.current)
//            .data(product.prodPic)  // 圖片 URL 或本地資源
//            .size(600, 600)  // 強制縮放圖片到最大為 600x600 像素
//            .target(
//                onSuccess = { result ->
//                    // 成功處理圖片
//                    Log.d("ImageLoad", "Image loaded successfully!")
//                },
//                onError = { error ->
//                    // 錯誤處理
//                    Log.d("ImageLoad", "Error loading image")
//                }
//            )
//            .build()
//
//        // 使用 imageLoader 加載圖片
//        imageLoader.enqueue(request)

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

        Spacer(modifier = Modifier.height(16.dp))

        // 顯示產品詳細資訊
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(id = R.string.productNum_format, product.prodNo),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424347)
            )
            Text(
                text = stringResource(id = R.string.productname_format, product.prodName),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424347)
            )
            Text(
                text = stringResource(id = R.string.price_format, product.prodPrice),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424347)
            )
            Text(
                text = stringResource(id = R.string.longDescription_format, product.prodDesc),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424347)
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
                onClick = {
                    // 檢查是否已登入
                    if (!isloginSuccess) {
                        // 顯示登入對話框
                        showLoginDialog = true
                    } else {
                        showCardDialog = true
                    }
                },
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

        if (showLoginDialog) {
            AlertDialog(
                onDismissRequest = { showLoginDialog = false },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "您必須先登入才能繼續購買",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                        TextField(
                            value = emailState,
                            onValueChange = {
                                emailState = it
                                memberLoginViewModel.onEmailChanged(it)
                            },
                            label = { Text("電子郵件") }
                        )
                        TextField(
                            value = passwordState,
                            onValueChange = {
                                passwordState = it
                                memberLoginViewModel.onPasswordChange(it)
                            },
                            label = { Text("密碼") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // 當按下登入時，觸發 login 方法
                        memberLoginViewModel.onLoginClick()
                        showLoginDialog = false
                    }) {
                        Text("登入")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showLoginDialog = false
                    }) {
                        Text("取消")
                    }
                }
            )
        }

        if (showCardDialog) {
            AlertDialog(
                onDismissRequest = { showCardDialog = false },  // 點擊外部區域或按返回鍵關閉對話框
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "輸入信用卡資訊",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 26.dp),
                            fontSize = 24.sp,
                            color = purple400
                        )
                        TextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = {
                                Text(
                                    text = "信用卡號碼",
                                    color = colorResource(id = R.color.purple_400)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = white300, /* 設定背景顏色 */
                                focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                                unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                                textColor = Color.Black // 文字顏色
                            )
                        )
                        if (cardNumberError != null) {
                            // 顯示錯誤訊息
                            Text(
                                text = cardNumberError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        TextField(
                            value = expirationDate,
                            onValueChange = { expirationDate = it },
                            label = {
                                Text(
                                    text = "到期日 (MM/YY)",
                                    color = colorResource(id = R.color.purple_400)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = white300, /* 設定背景顏色 */
                                focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                                unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                                textColor = Color.Black // 文字顏色
                            )
                        )
                        if (expirationDateError != null) {
                            // 顯示錯誤訊息
                            Text(
                                text = expirationDateError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        TextField(
                            value = cvv,
                            onValueChange = { cvv = it },
                            label = {
                                Text(
                                    text = "CVV",
                                    color = colorResource(id = R.color.purple_400)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = white300, /* 設定背景顏色 */
                                focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                                unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                                textColor = Color.Black // 文字顏色
                            )
                        )
                        if (cvvError != null) {
                            // 顯示錯誤訊息
                            Text(
                                text = cvvError!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // 驗證信用卡資訊是否正確
                        cardNumberError =
                            if (isCardNumberValid(cardNumber)) null else "信用卡號格式不正確"
                        expirationDateError =
                            if (isExpirationDateValid(expirationDate)) null else "到期日格式不正確 (MM/YY)"
                        cvvError = if (isCvvValid(cvv)) null else "CVV 格式不正確"

                        // 驗證信用卡資料
                        if (cardNumberError == null && expirationDateError == null && cvvError == null) {
                            // 建立 OrderRequest
                            val orderRequest = OrderRequest(
                                memNo = memberId, // 假設的會員編號，需動態獲取
                                prodNo = product.prodNo,
                                prodName = product.prodName, // 產品名稱
                                prodPrice = product.prodPrice, // 產品價格
                                cardNo = cardNumber, // 信用卡號
                                expDate = expirationDate, // 到期日
                                cvv = cvv, // CVV
                                prodPic = product.prodPic //產品照片
                            )

                            // 呼叫函式將訂單提交到資料庫，並處理回調
                            orderVM.submitOrderToDatabase(
                                order = orderRequest,
                                onSuccess = { order ->
                                    // 更新 completeOrder 狀態
                                    completeOrder = order
                                    // 後端返回完整的 Order 物件，包含自動生成的 ordNo
                                    Log.d("OrderSubmit", "訂單成功提交，訂單編號: ${order.ordNo}")
                                },
                                onError = { errorMessage ->
                                    // 提示用戶提交訂單失敗的錯誤訊息
                                    Log.e("OrderSubmit", "提交訂單失敗: $errorMessage")
                                }
                            )
                        } else {
                            // 提示用戶填寫完整信用卡資訊
                            Log.e("Validation", "信用卡資訊未填完整")
                        }
                    }) {
                        Text("確認購買")
                    }
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
