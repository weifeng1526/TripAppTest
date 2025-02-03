package com.example.tripapp.ui.feature.shop

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.AlertDialog
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.purple400
import com.example.tripapp.ui.theme.purple500
import com.example.tripapp.ui.theme.red100
import com.example.tripapp.ui.theme.red200
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400
import kotlin.toString

@Composable
fun ProductListScreen(
    navController: NavHostController,
    productVM: ProductVM,
    tabVM: TabVM
) {
    // TabRow顯示與否
    tabVM.updateTabState(true)

    // 從StateFlow取得最新資料
    val products by productVM.productsState.collectAsState()
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
        ProductLists(
            products.filter { it.prodName.contains(inputText, true) },
            // 項目被點擊時執行
            onItemClick = {
                Log.d("tag_", "onItemClick")
                // 將點擊的book存入bookVM，然後切換至BookDetail頁面
                productVM.setDetailProduct(it)
                Log.d("tag_", "setDetailProduct")
                navController.navigate(Screen.ProductDetail.name)
            },
            navController,
            productVM
        )
    }
}

/**
 * 列表內容
 * @param products 欲顯示的商品清單
 */
@Composable
fun ProductLists(
    products: List<Product>,
    onItemClick: (Product) -> Unit,
    navController: NavHostController,
    productVM: ProductVM
) {
    val memberId = GetUid(MemberRepository)
    var showDialog by remember { mutableStateOf(false) } // 控制對話框的顯示
    var newProduct by remember { mutableStateOf(Product()) } // 儲存新商品資料

    // 新增商品對話框的狀態
    var newProdName by remember { mutableStateOf("") }
    var newProdDesc by remember { mutableStateOf("") }
    var newProdPrice by remember { mutableStateOf("") }
    var newProdPic by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),  // 內邊距
        horizontalArrangement = Arrangement.SpaceBetween  // 左右分布
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),  // Row 內部的間距
            horizontalArrangement = Arrangement.SpaceBetween  // 讓按鈕左右分布
        ) {
            if (memberId == 3) {
                Button(
                    onClick = {
                        showDialog = true
                        // 初始化 newProduct
                        newProduct = Product()
                        newProdName = ""
                        newProdDesc = ""
                        newProdPrice = ""
                        newProdPic = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purple200)
                ) {
                    Text(text = "新增商品", color = white300)
                }
            }

            Button(
                onClick = {
                    navController.navigate(Screen.OrderList.createRoute(memberId))                },
                colors = ButtonDefaults.buttonColors(containerColor = purple200)
            ) {
                Text(text = "我的訂單", color = white300)
            }
        }
    }

    // 新增商品對話框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "新增商品",
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = newProdName,
                        onValueChange = {
                            newProdName = it
                            Log.d("InputDebug", "商品名稱: $it")
                        },
                        label = { Text("商品名稱") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = white400, /* 設定背景顏色 */
                            focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                            unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                            textColor = Color.Black // 文字顏色
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newProdDesc,
                        onValueChange = {
                            newProdDesc = it
                            Log.d("InputDebug", "商品描述: $it")
                        },
                        label = { Text("商品描述") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = white400, /* 設定背景顏色 */
                            focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                            unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                            textColor = Color.Black // 文字顏色
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newProdPrice,
                        onValueChange = {
                            newProdPrice = it
                            Log.d("InputDebug", "商品價格: $it")
                        },
                        label = { Text("商品價格") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = white400, /* 設定背景顏色 */
                            focusedIndicatorColor = colorResource(id = R.color.purple_400), // 聚焦時的指示線顏色
                            unfocusedIndicatorColor = Color.Gray, // 失去焦點時的指示線顏色
                            textColor = Color.Black // 文字顏色
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = newProdPic,
                        onValueChange = {
                            newProdPic = it
                            Log.d("InputDebug", "商品圖片URL: $it")
                        },
                        label = { Text("商品圖片URL") },
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
                Button(
                    onClick = {
                        // 呼叫ViewModel的方法提交新商品資料
                        newProduct = newProduct.copy(
                            prodName = newProdName,
                            prodDesc = newProdDesc,
                            prodPrice = newProdPrice.toIntOrNull() ?: 0,
                            prodPic = newProdPic
                        )
                        productVM.addProduct(newProduct)
                        Log.d("NewProduct", newProduct.toString())
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purple200)
                ) {
                    Text("確定")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = purple200)
                ) {
                    Text("取消")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(products) { product ->
//             使用 Card 包裝每個項目，提供陰影和圓角效果
            Card(
                modifier = Modifier
                    .clickable { onItemClick(product) }  // 點擊事件
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
                            .data(product.prodPic)  // prodPic 可以是 URL 或 Base64 字符串
                            .crossfade(true)
                            .memoryCacheKey(product.prodPic)
                            .diskCacheKey(product.prodPic)
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
                            text = product.prodName,
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.h6,
                            color = purple400
                        )
                        Text(
                            text = "價格 : $" + product.prodPrice.toString() + "元",
                            style = MaterialTheme.typography.h6,
                            color = red200
                        )
                    }
                }
            }
            HorizontalDivider()
        }
    }
}


