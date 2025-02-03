package com.example.tripapp.ui.feature.shop

import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.home.MemberViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 建立列舉類型，其屬性代表各頁面
 */
enum class Screen(@StringRes val title: Int, val route: String
) {
    ProductList(title = R.string.product_list, route = "ProductList"),

    ProductDetail(title = R.string.product_detail, route = "ProductDetail/{productId}") {
        // 建立帶有 productId 參數的路由字串
        fun createRoute(productId: Int) = "ProductDetail/$productId"
    },

    // 訂單頁面，帶有 orderJson 參數
    Order(title = R.string.order, route = "OrderScreen/{orderJson}"),

    //訂單管理頁面
    OrderList(title = R.string.order_list, route = "OrderList/{memberId}") {
        override fun createRoute(vararg args: Any): String {
            val memberId = args[0] as Int
            return "OrderList/$memberId"
        }
    };

    // 讓每個列舉常數都可以呼叫 createRoute
    open fun createRoute(vararg args: Any): String {
        throw NotImplementedError("createRoute() not implemented for $this")
    }
}

/**
 * Main是一個頁面容器，其他頁面會依照使用者操作被加上來
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductMainScreen(
    navController: NavHostController = rememberNavController(),
    productVM: ProductVM = viewModel(),
    tabVM: TabVM,
    orderVM: OrderVM = viewModel()
) {
    // 取得儲存在back stack最上層的頁面
    val backStackEntry by navController.currentBackStackEntryAsState()
    // 取得當前頁面的路由
    val currentRoute = backStackEntry?.destination?.route
    // 取得當前頁面，如果找不到對應的頁面，則預設為 ProductList
    val currentScreen =
        Screen.entries.find { currentRoute?.contains(it.route) == true } ?: Screen.ProductList


//    // 設定內容向上捲動時，TopAppBar自動收起來；呼叫pinnedScrollBehavior()則不會收起來
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.ProductList.name,
                modifier = Modifier
                    .fillMaxSize()

            ) {
                composable(route = Screen.ProductList.name) {
                    /* 在composable階層架構中，上層建立的物件傳給下一層，基本上是安全的。
                所以可將navController或ViewModel物件傳給下一個Composable。
                但若不將navController與ViewModel物件傳給BookListScreen，就需要靠callback函式。 */
                    ProductListScreen(
                        navController = navController,
                        productVM = productVM,
                        tabVM = tabVM
                    )
                }

                composable(route = Screen.ProductDetail.name) {
                    ProductDetailScreen(
                        navController = navController,
                        productVM = productVM,
                        tabVM = tabVM,
                        orderVM = orderVM,
                        memberVM = MemberViewModel(LocalContext.current)
                    )
                }

                composable(
                    route = "OrderScreen/{orderJson}"
                ) { backStackEntry ->
                    // 取得傳遞過來的 orderJson 字符串
                    val orderJson = backStackEntry.arguments?.getString("orderJson") ?: ""

                    // 解碼 URL 編碼的字符串
                    val decodedOrderJson = Uri.decode(orderJson)

                    // 打印解碼後的 JSON 字符串
                    Log.d("DecodedOrderJson", "Decoded Order JSON: $decodedOrderJson")

                    // 將 JSON 字串反序列化為 Order 物件
                    val order = try {
                        Gson().fromJson(decodedOrderJson, Order::class.java)
                    } catch (e: Exception) {
                        Log.e("ProductMainScreen", "Error parsing order JSON: $e")
                        null
                    }

                    if (order != null) {
                        // 呼叫 OrderScreen，並傳入 order 物件
                        OrderScreen(
                            navController = navController,
                            productVM = productVM,
                            order = order,
                            tabVM = tabVM
                        )
                    } else {
                        // 處理 order 資料無效的情況
                        Log.e("ProductMainScreen", "Invalid order data")
                    }
                }

                composable(
                    route = Screen.OrderList.route,
                    arguments = listOf(navArgument("memberId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val memberId = backStackEntry.arguments?.getInt("memberId") ?: -1
                    Log.d("ProductMainScreen", "Navigating to OrderList with memberId: $memberId")
                    if (memberId != -1) {
                        // 呼叫 OrderListScreen，並傳入 memberId
                        OrderListScreen(
                            navController = navController,
                            memberId = memberId,
                            productVM = productVM,
                            orderVM = orderVM,
                            tabVM = tabVM
                        )
                    } else {
                        // 處理 memberId 無效的情況
                        Log.e("ProductMainScreen", "Invalid memberId passed to OrderList")
                    }
                }
            }
        }
    }
}


///**
// * 建立topBar與回上頁按鈕
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainAppBar(
//    navController: NavHostController,
//    currentScreen: Screen,
//    canNavigateBack: Boolean,
//    modifier: Modifier = Modifier,
//    scrollBehavior: TopAppBarScrollBehavior
//) {
//    TopAppBar(
//        // 設定頁面標題
//        title = { Text(stringResource(currentScreen.title)) },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
//        modifier = modifier,
//        navigationIcon = {
//            // 如果可回上頁，就顯示Back按鈕
//            if (canNavigateBack) {
//                IconButton(onClick = { navController.navigateUp() }) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = stringResource(R.string.back_button)
//                    )
//                }
//            }
//        },
//        scrollBehavior = scrollBehavior
//    )
//}

@Preview(showBackground = true)
@Composable
fun ProductMainScreenPreview() {
        ProductMainScreen(tabVM = viewModel())
    }