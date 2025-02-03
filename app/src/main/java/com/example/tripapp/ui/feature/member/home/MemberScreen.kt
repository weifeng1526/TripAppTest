package com.example.tripapp.ui.feature.member.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.baggage.baglist.BAG_NAVIGATION_ROUTE
import com.example.tripapp.ui.feature.member.GetName
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.IsLogin
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.member.MemberViewModelFactory
import com.example.tripapp.ui.feature.member.login.MEMBER_LOGIN_ROUTE
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModel
import com.example.tripapp.ui.feature.member.turfav.TUR_FAV_ROUTE
import com.example.tripapp.ui.theme.black100
import com.example.tripapp.ui.theme.black900
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400
import kotlinx.coroutines.flow.toList
import okhttp3.MediaType.Companion.toMediaTypeOrNull

@Composable
fun MemberRoute(
    viewModel: MemberViewModel = viewModel(factory = MemberViewModelFactory(LocalContext.current)),
    navController: NavHostController
) {
    MemberScreen(
        onLoginClick = { navController.navigate(MEMBER_LOGIN_ROUTE) },
//        onTurFavClick = { navController.navigate(TUR_FAV_ROUTE) },
        onBagClick = { navController.navigate(BAG_NAVIGATION_ROUTE) },
        navController = navController, // 將 navController 傳遞給 MemberScreen
        viewModel = viewModel
    )
}

@Preview
@Composable
fun PreviewMemberRoute() {
    MemberScreen(
        viewModel = viewModel(),
        navController = rememberNavController()
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MemberScreen(
    navController: NavHostController,
    viewModel: MemberViewModel =
        viewModel(
            factory = MemberViewModelFactory(
                LocalContext.current
            )
        ),
    onLoginClick: () -> Unit = { },
//    onTurFavClick: () -> Unit = { },
    onBagClick: () -> Unit = { },
) {
    val uid = GetUid(MemberRepository)
    val isLogin = IsLogin()
    //定義呼叫的方法
    val name = GetName()
    val memberName = if (isLogin) name else "會員登入"
    val memNo by viewModel.uid.collectAsState()
//    val img = remember { mutableStateOf(MemberIcon()) }
//    val newImg by viewModel.getMemIcon()
//    val newIcon = MemberIcon(memNo, img)
//    val iconUid = viewModel.getMemIcon(newIcon.memNo)
//    val icon = viewModel.getMemIcon()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .background(white100),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.05f),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    textAlign = TextAlign.Justify,
                    text = "登出",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                MemberRepository.clearUid()
//                                MemberRepository.cleanName("")
//                                navController.navigate(MEMBER_LOGIN_ROUTE)
//                                if (uid > 0) {
//                                    logout
//                                }
                            }
                        )
                        .height(30.dp)
                        .padding(end = 18.dp)
                        .wrapContentSize(Alignment.Center)
                )
//                Text(
//                    textAlign = TextAlign.Justify,
//                    text = "登出",
//                    fontSize = 20.dp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .clickable { }
//                        .padding(end = 15.dp)
//                        .size(40.dp)
//                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.23f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(white100, white300)
                        ),
                    ),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp, Alignment.Bottom),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (memNo == uid) {
                        val newIcon = memIcon()[uid].img
                        Image(
                            painter = painterResource(id =newIcon),
                            contentDescription = "會員頭像",
                            modifier = Modifier
//                                .fillMaxHeight(0.3f)
                                .size(70.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        if (!isLogin) {
                                            onLoginClick.invoke()
                                        } else {
                                        }
                                    }
                                )
                        )
                    }
                    Text(
                        textAlign = TextAlign.Justify,
                        // 使用定義過的方法
                        text = memberName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if (!isLogin) {
                                        onLoginClick.invoke()
                                    } else {
                                    }
                                }
                            )
                            .height(30.dp)
                            .padding()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier,
            color = white400
        )
        //景點收藏與我的行李的入口清單列
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HomeList(
//                onTurFavClick = onTurFavClick,
                onBagClick = onBagClick
            )
        }
        HorizontalDivider(
            modifier = Modifier,
            color = white400
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(32.dp)
                .graphicsLayer {
                    this.alpha = 0.5f
                }
        ) {
            //要放logo的地方
            Image(
                painter = painterResource(id = R.drawable.trip_icon),
                contentDescription = "AppLogo",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
            Text(text = "旅友 TravelMate")
        }
    }
}

@Composable
fun HomeList(
//    onTurFavClick: () -> Unit,
    onBagClick: () -> Unit,
) {
    Column {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable(onClick = onTurFavClick)
//                .padding(top = 10.dp, bottom = 10.dp)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.myicon_suitcase_1),
//                contentDescription = "景點收藏",
//                modifier = Modifier
//                    .size(125.dp) //調整Image比例
//            )
//            Text(
//                text = "景點收藏",
//                fontSize = 24.sp,
//                modifier = Modifier
//                    .wrapContentSize(Alignment.Center)
//            )
//        }
//
//        HorizontalDivider(
//            modifier = Modifier,
//            color = white400
//        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBagClick)
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            Spacer(modifier = Modifier.width(18.dp))
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.purple_200),
                        shape = RoundedCornerShape(50)
                    )
                    .background(
                        color = colorResource(id = R.color.white_400),
                        shape = RoundedCornerShape(50)
                    )
//                    .align(Alignment.CenterHorizontally)
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onPress = {
//                                isSuitcaseImage1.value = false
//                                try {
//                                    awaitRelease()
//                                } finally {
//                                    isSuitcaseImage1.value = true
//                                }
//                            }
//                        )
//                    }
            ) {
//                根據狀態切換圖片
                Image(
                    painter = painterResource(
                        id = R.drawable.ashley___suitcase_1_new
                    ),
                    contentDescription = "suitcase Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Center)
                        .padding(8.dp)
                        .border(
                            width = 6.dp,
                            color = colorResource(id = R.color.white_100),
                            shape = RoundedCornerShape(50)
                        ),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.purple_200))
                )
            }
//            Image(
//                painter = painterResource(id = R.drawable.myicon_suitcase_1),
//                contentDescription = "我的行李",
//                modifier = Modifier
//                    .size(125.dp) //調整Image比例
//            )
            Spacer(modifier = Modifier.width(36.dp))
            Text(
                text = "我的行李",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentSize(Center)
            )
        }
    }
}