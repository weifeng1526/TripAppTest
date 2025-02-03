package com.example.tripapp.ui.feature.shop

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.home.MemberViewModel
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModel
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModelFactory
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("ResourceAsColor", "SuspiciousIndentation")
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productVM: ProductVM,
    tabVM: TabVM,
    orderVM: OrderVM,
    memberVM: MemberViewModel
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
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 使用 rememberCoroutineScope 創建協程範圍
    val coroutineScope = rememberCoroutineScope()

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

    // 取得會員電郵與密碼的狀態
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    // 使用 collectAsState() 收集 isLoginSuccess 的值以取得會員登入狀態
    val memberId by memberLoginViewModel.uid.collectAsState()
    val isloginSuccess = memberId != 0
    Log.e("ProductDetail", "memberId: $memberId")

    var isProcessing by remember { mutableStateOf(false) }

    // 監控 isProcessing，延遲 3 秒再執行跳轉
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(3000)  // 等待 3 秒鐘確保進度條顯示
            completeOrder?.let {
                val orderJson = Gson().toJson(it)
                val encodedOrderJson = Uri.encode(orderJson)
                val route = "OrderScreen/$encodedOrderJson"
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (memberId == 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 修改按鈕
                Button(
                    onClick = {
                        showEditDialog = true  // 顯示修改對話框
                        Log.d("ProductDetail", "修改商品")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(R.color.purple_200)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "修改")
                }

                if (showEditDialog) {
                    ProductEditDialog(
                        product = product,
                        onDismiss = { showEditDialog = false },
                        onSubmit = { updatedProduct ->
                            coroutineScope.launch {
                                try {
                                    // 呼叫 suspend 函式 updateProductDetails
                                    productVM.updateProductDetails(updatedProduct)
                                    // 更新UI顯示的資料
                                    productVM.setDetailProduct(updatedProduct)
                                    showEditDialog = false
                                } catch (e: Exception) {
                                    Log.e("ProductDetail", "更新商品資料失敗: ${e.message}")
                                }
                            }
                        }
                    )
                }

                // 刪除按鈕
                Button(
                    onClick = {
                        // 顯示刪除確認對話框
                        showDeleteDialog = true
                        Log.d("ProductDetail", "刪除商品")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(R.color.purple_100)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "刪除")
                }

                // 刪除確認對話框
                if (showDeleteDialog) {
                    DeleteConfirmationDialog(
                        onConfirm = {
                            // 呼叫 ViewModel 刪除商品
                            coroutineScope.launch {
                                try {
                                    productVM.deleteProduct(product.prodNo)

                                    // 刪除商品後，更新商品列表
                                    productVM.updateProductList() // 更新商品列表

                                    // 刪除成功後，導航回商品列表頁面
                                    navController.navigate(Screen.ProductList.name) {
                                        // 確保導航回商品列表頁面並清除該頁面的堆疊
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("ProductDetail", "刪除商品失敗: ${e.message}")
                                }
                            }
                            showDeleteDialog = false
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                                backgroundColor = white400, /* 設定背景顏色 */
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
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = white400, /* 設定背景顏色 */
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
                                backgroundColor = white400, /* 設定背景顏色 */
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

                            isProcessing = true  // 開始顯示進度條

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
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(R.color.purple_100),
                            contentColor = colorResource(id = R.color.white)
                        )
                    ) {
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

        // 顯示進度條對話框
        if (isProcessing) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { }) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))  // 不透明背景，圓角
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("與信用卡銀行連線中...", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(R.color.purple_200)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductEditDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSubmit: (Product) -> Unit
) {
    // 儲存編輯中的產品資料
    var updateProdName by remember { mutableStateOf(product.prodName) }
    var updateProdPrice by remember { mutableStateOf(product.prodPrice.toString()) }
    var updateProdDesc by remember { mutableStateOf(product.prodDesc) }
    var updateProdPic by remember { mutableStateOf(product.prodPic) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "修改產品資訊") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = updateProdName,
                    onValueChange = { updateProdName = it },
                    label = { Text("產品名稱") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = white400, /* 設定背景顏色 */
                        focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                        unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                        textColor = Color.Black // 文字顏色
                    )
                )
                TextField(
                    value = updateProdPrice,
                    onValueChange = { updateProdPrice = it },
                    label = { Text("產品價格") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = white400, /* 設定背景顏色 */
                        focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                        unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                        textColor = Color.Black // 文字顏色
                    )
                )
                TextField(
                    value = updateProdDesc,
                    onValueChange = { updateProdDesc = it },
                    label = { Text("產品描述") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = white400, /* 設定背景顏色 */
                        focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                        unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                        textColor = Color.Black // 文字顏色
                    )
                )
                TextField(
                    value = updateProdPic,
                    onValueChange = { updateProdPic = it },
                    label = { Text("產品照片") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = white400, /* 設定背景顏色 */
                        focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                        unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                        textColor = Color.Black // 文字顏色
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // 驗證並轉換價格，將字串轉換為 Double
                val updatePrice = updateProdPrice.toIntOrNull() ?: 0
                Log.d("ProductEditDialog", "更新產品資訊：名稱=$updateProdName, 價格=$updatePrice, 描述=$updateProdDesc, 照片=$updateProdPic")
                // 驗證輸入資料並提交
                val updatedProduct = product.copy(
                    prodName = updateProdName,
                    prodPrice = updatePrice,
                    prodDesc = updateProdDesc,
                    prodPic = updateProdPic
                )
                // 日誌輸出，檢查提交的商品資料
                Log.d("ProductEditDialog", "提交更新的商品：$updatedProduct")
                onSubmit(updatedProduct)
                onDismiss()
            },
                colors = ButtonDefaults.buttonColors(containerColor = purple200)
            ) {
                Text("確認修改")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = purple200)
            ) {
                Text("取消")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("確定刪除這筆商品?") },
        text = { Text("刪除後將無法恢復!") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = purple200)
            )  {
                Text("確定刪除")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = purple200)
            ) {
                Text("取消")
            }
        }
    )
}