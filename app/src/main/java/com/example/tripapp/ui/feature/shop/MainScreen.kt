package com.example.tripapp.ui.feature.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripapp.R
import com.example.tripapp.ui.theme.TripAppTheme


@Composable
fun ShopRoute() {
    Main()
}

@Composable
fun Main(tabVM: TabVM = viewModel()) {
    val tabVisibility = tabVM.tabVisibility.collectAsState()
    // 儲存當前頁籤索引，一開始設定為0，代表要顯示BookMain頁面
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.home),
        stringResource(id = R.string.member)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Take remaining vertical space
                .weight(1f)
        ) {
            when (tabIndex) {
                // 將tabVM傳至下頁，以便於控制TabRow的隱藏與顯示
                0 -> ProductMainScreen(tabVM = tabVM)
                1 -> MemberScreen(tabVM = tabVM)
            }
        }
        /* 判斷首頁是否顯示tabs:
            如果進入BookDetailScreen就要隱藏，將tabVisibility設為false。
            如果回到BookListScreen就要顯示，將tabVisibility設為true。
            因為需跨頁設定，要將tabVisibility存放在ViewModel */
        if (!tabVisibility.value) {
            // 改用ScrollableTabRow，當頁籤數量過多時可以滑動
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = colorResource(id = R.color.black_500)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(text = title, style = TextStyle(fontSize = 10.sp)) },
                        // 判斷此頁籤是否為選取頁籤
                        selected = index == tabIndex,
                        // 點擊此頁籤後將選取索引改為此頁籤的索引
                        onClick = { tabIndex = index },
                        // 設定選取顏色
                        selectedContentColor = colorResource(id = R.color.purple_700),
                        // 設定未選取顏色
                        unselectedContentColor = colorResource(id = R.color.purple_100),
                        icon = {
                            when (index) {
                                0 -> Image(
                                    painter = painterResource(id = R.drawable.baseline_store_24),
                                    contentDescription = "Notes Icon",
                                    modifier = Modifier.size(24.dp),
                                    colorFilter = ColorFilter.tint(colorResource(id = R.color.purple_100))
                                )

                                1 -> Image(
                                    painter = painterResource(id = R.drawable.baseline_account_box_24),
                                    contentDescription = "Shop Icon",
                                    modifier = Modifier.size(24.dp),
                                    colorFilter = ColorFilter.tint(colorResource(id = R.color.purple_100))
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    TripAppTheme {
        Main()
    }
}