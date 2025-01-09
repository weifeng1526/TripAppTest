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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.tripapp.R

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
            }
        )
    }
}

/**
 * 列表內容
 * @param products 欲顯示的書籍清單
 */
@Composable
fun ProductLists(
    products: List<Product>,
    onItemClick: (Product) -> Unit,
) {
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
//                    // 顯示圖片
//                    val painter = painterResource(product.prodPic)
//                    Image(
//                        painter = painter,
//                        contentDescription = "product",
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
                            .height(250.dp), // 設定圖片高度
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
                        )
                        Text(
                            text = "$ : " + product.prodPrice.toString(),
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }
            HorizontalDivider()
        }
    }
}
