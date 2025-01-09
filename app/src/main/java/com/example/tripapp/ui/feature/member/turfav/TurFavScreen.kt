package com.example.tripapp.ui.feature.member.turfav

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.turfav.favlist.FavListViewModel
import com.example.tripapp.ui.feature.member.turfav.favlist.fetchFav
import com.example.tripapp.ui.feature.member.turfav.favlist.genFavListNavigationRoute
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white400

@Composable
fun TurFavRoute(navController: NavHostController) {
    val turFavViewModel = TurFavViewModel()
    val favListViewModel = FavListViewModel()
    TurFavScreen(
        turFavViewModel = turFavViewModel,
        favListViewModel = favListViewModel,
        onTurFavClick = { fav ->
            navController.navigate(genFavListNavigationRoute(1))
        }
    )
}

@Preview
@Composable
fun PreviewFavListRoute() {
    TurFavScreen(
        turFavViewModel = TurFavViewModel(),
        favListViewModel = FavListViewModel()
    )
}

@Composable
fun TurFavScreen(
    turFavViewModel: TurFavViewModel,
    favListViewModel: FavListViewModel,
    onTurFavClick: (TurFav) -> Unit = {}
) {
    val turFavList by turFavViewModel.turFavState.collectAsState()
    val favList by favListViewModel.favListState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            TurFavList(
                turFavList = turFavList,
                favList = favList,
                onTurFavListClick = onTurFavClick
            )
        }
    }
}


@Composable
fun TurFavList(
    turFavList: List<TurFav>,
    favList: List<fetchFav>,
    onTurFavListClick: (TurFav) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(turFavList) { turFav -> // 根據資料清單生成項目
//            val filteredFavs = favList.filter { it.folderNo == turFav.tfFolderNo }
//            if (filteredFavs.isEmpty()) {
//                Text(
//                    text = "該收藏夾內尚無景點",
//                    color = black900
//                )
//            } else {}
            ListItem(
                modifier = Modifier
                    .clickable { onTurFavListClick(turFav) }, // 點擊傳遞資料
                headlineContent = { Text(turFav.tfFolderName) }, // 顯示收藏夾名稱
                leadingContent = {
                    Image(
                        painter = painterResource(R.drawable.lets_icons__suitcase_light),
                        contentDescription = turFav.tfFolderName,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            )
            HorizontalDivider(
                modifier = Modifier,
                color = white400
            )
        }
    }
}
