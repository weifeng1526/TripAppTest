package com.example.tripapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.ui.feature.baggage.baglist.BAG_NAVIGATION_ROUTE
import com.example.tripapp.ui.feature.baggage.baglist.bagListScreenRoute
import com.example.tripapp.ui.feature.baggage.baglist.bagListScreenRouteSelected
import com.example.tripapp.ui.feature.baggage.itemlist.ADDITEM_NAVIGATION_ROUTE
import com.example.tripapp.ui.feature.baggage.itemlist.addItemScreenRoute
import com.example.tripapp.ui.feature.map.mapRoute
import com.example.tripapp.ui.feature.member.IsLogin
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.member.home.MEMBER_ROUTE
import com.example.tripapp.ui.feature.member.home.memberRoute
import com.example.tripapp.ui.feature.member.home.tabs.notifyRoute
import com.example.tripapp.ui.feature.member.login.MEMBER_LOGIN_ROUTE
import com.example.tripapp.ui.feature.member.login.memberLoginRoute
import com.example.tripapp.ui.feature.member.signup.MEMBER_SIGNUP_ROUTE
import com.example.tripapp.ui.feature.member.signup.memberSignUpRoute
import com.example.tripapp.ui.feature.member.turfav.turFavRoute
import com.example.tripapp.ui.feature.shop.SHOP_ROUTE
import com.example.tripapp.ui.feature.shop.shopRoute
import com.example.tripapp.ui.feature.spending.addlist.spendingAddRoute
import com.example.tripapp.ui.feature.spending.deposit.spendingDepositRoute
import com.example.tripapp.ui.feature.spending.list.SPENDING_LIST_ROUTE
import com.example.tripapp.ui.feature.spending.list.spendingListRoute
import com.example.tripapp.ui.feature.spending.setting.spendingSetRoute
import com.example.tripapp.ui.feature.spending.settinglist.spendingSetListRoute
import com.example.tripapp.ui.feature.trip.notes.note.notesRoute
import com.example.tripapp.ui.feature.trip.notes.select.SELECT_ROUTE
import com.example.tripapp.ui.feature.trip.notes.select.selectRoute
import com.example.tripapp.ui.feature.trip.notes.show.showSchRoute
import com.example.tripapp.ui.feature.trip.plan.alter.planAlterRoute
import com.example.tripapp.ui.feature.trip.plan.create.planCreateRoute
import com.example.tripapp.ui.feature.trip.plan.crew.memberInviteRoute
import com.example.tripapp.ui.feature.trip.plan.crew.planCrewRoute
import com.example.tripapp.ui.feature.trip.plan.edit.planEditRoute
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.feature.trip.plan.home.planHomeRoute
import com.example.tripapp.ui.theme.*


enum class TabsBottom(
    val index: Int,
    val title: String,
    val img: Int,
    val route: String
) {
    tabsBottomA(0, "目前行程", R.drawable.ic_tripnow, SELECT_ROUTE),
    tabsBottomB(1, "旅遊商城", R.drawable.ic_shop, SHOP_ROUTE),
    tabsBottomC(2, "行程管理", R.drawable.ic_tripmgt, PLAN_HOME_ROUTE),
    tabsBottomD(3, "帳務管理", R.drawable.ic_accsplit, SPENDING_LIST_ROUTE),
    tabsBottomE(4, "會員中心", R.drawable.ic_member, MEMBER_ROUTE)
}

//fun test() {
//    val list = listOf(
//        "小黑",
//        "小紅",
//        "陸路"
//    )
//    list.forEachIndexed{index, title ->
//        Log.d("TAG", "$index:$title ")
//
//    }
//}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            tripApp()
        }
    }
}


@Composable
fun content(innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) { }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tripApp(
    navController: NavHostController = rememberNavController(),
) {
//    val pageTitleName = remember { mutableStateOf(mapOf(
//        "a" to "a"
//    )) }
    var tabsBottomListBtnIndex by remember { mutableIntStateOf(2) }
    val tabsBottomList = remember {
        mutableStateOf(
            TabsBottom.entries.associateBy { it.index }
        )
    }
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val uid = MemberRepository.getUid()

    LaunchedEffect(uid) {
        if (uid == 0) {
            tabsBottomListBtnIndex = 2
        }
    }
    val route = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""
    Log.e("route", route)

    val isLoggedIn = uid != 0 // 判斷是否已登入
    val isLogin = IsLogin()
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val isShowTopBarAndBottomBar =
        if (currentRoute == MEMBER_LOGIN_ROUTE || currentRoute == MEMBER_SIGNUP_ROUTE) {
            true
        } else {
            false
        } // 若為登入、註冊頁面，不顯示TopBar跟BottomBar
    val tabsRoute =
        if (
            currentRoute == SELECT_ROUTE ||
            currentRoute == SHOP_ROUTE ||
            currentRoute == PLAN_HOME_ROUTE ||
            currentRoute == SPENDING_LIST_ROUTE ||
            currentRoute == MEMBER_ROUTE
        ) {
            true
        } else {
            false
        }
    val bagRoute = if (
        currentRoute == ADDITEM_NAVIGATION_ROUTE ||
        currentRoute == BAG_NAVIGATION_ROUTE
    ) {
        true
    } else {
        false
    }
    val bagTittle = "我的行李"
//    val isShowTopBar = if (currentRoute == BAG_NAVIGATION_ROUTE) {true} else {false}
    val tabsBottom: List<TabsBottom> = TabsBottom.entries

    LaunchedEffect(uid) {
        if (isLoggedIn) {
            navController.navigate(PLAN_HOME_ROUTE) // 已登入，導航到首頁
        } else {
            navController.navigate(MEMBER_LOGIN_ROUTE) // 未登入，導航到登入頁面
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {
            if (!isLogin) {
                isShowTopBarAndBottomBar
            } else {
                CenterAlignedTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    //
                    title = {
                        tabsBottomList.value.forEach() { (index, tab) ->
                            if (index == tabsBottomListBtnIndex) {
                                Text(
                                    text = tab.title,
                                    fontSize = 19.sp
                                )
                            } else if (bagRoute) {
                                Text(
                                    text = bagTittle,
                                    fontSize = 19.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = purple200, titleContentColor = white200
                    ),
                    navigationIcon = {
                        //隱藏左上角的回前一頁箭頭
                        when (route) {
                            SELECT_ROUTE -> {}
                            SHOP_ROUTE -> {}
                            PLAN_HOME_ROUTE -> {}
                            SPENDING_LIST_ROUTE -> {}
                            MEMBER_ROUTE -> {}
                            else -> {
                                Image(
                                    painter = painterResource(R.drawable.ic_back),
                                    contentDescription = "back",
                                    modifier = Modifier
                                        .padding(24.dp, 0.dp, 0.dp, 0.dp)
                                        .size(32.dp)
                                        .clickable { navController.popBackStack() }
                                )
                            }
                        }
                    },
                )
            }

        },
        bottomBar = {
            // if isLogin 顯示BottomAppBar
            if (!isLogin) {
                isShowTopBarAndBottomBar
            } else {
                BottomAppBar(
                    contentColor = white200,
                ) {
                    TabRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(white100),
                        selectedTabIndex = tabsBottomListBtnIndex,
                        containerColor = white300,
                        contentColor = purple300,
                        indicator = {},
                        divider = {}
                    ) {
                        tabsBottomList.value.forEach() { (index, tab) ->
                            Tab(
                                modifier = Modifier
                                    .background(
                                        if (index == tabsBottomListBtnIndex) white100 else white300
                                    ),
                                icon = {
                                    Image(
                                        painter = painterResource(tab.img),
                                        contentDescription = tab.title
                                    )
                                },
                                text = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 12.sp,
                                    )
                                },
                                selected = index == tabsBottomListBtnIndex,
                                onClick = {
                                    tabsBottomListBtnIndex = index
                                    navController.navigate(tab.route)
                                },
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            TripNavHost(navController)
        }
    }
}


@Composable
fun TripNavHost(
    navController: NavHostController,

    ) {
    NavHost(
        modifier = Modifier, navController = navController,
        // 初始頁面
        startDestination = PLAN_HOME_ROUTE
    ) {
        // 畫面路徑-Ruby
        spendingListRoute(navController = navController)
        spendingSetRoute(navController = navController)
        spendingAddRoute(navController = navController)
        spendingDepositRoute(navController = navController)
        spendingSetListRoute(navController = navController)

        // 畫面路徑-Ashley
        bagListScreenRoute(navController = navController)
        bagListScreenRouteSelected(navController = navController)
        addItemScreenRoute(navController = navController)

        //畫面路徑-leo
        planHomeRoute(navController = navController)
        planCreateRoute(navController = navController)
        planEditRoute(navController = navController)
        planCrewRoute(navController = navController)
        planAlterRoute(navController = navController)
        memberInviteRoute(navController = navController)


        //畫面路徑-Aaron
        shopRoute(navController = navController)

        //畫面路徑-Wayne
        memberSignUpRoute(navController = navController)
        memberLoginRoute(navController = navController)
        memberRoute(navController = navController)
        turFavRoute(navController = navController)
        turFavRoute(navController = navController)
        notifyRoute(navController = navController)

        //畫面路徑-Jonas
        selectRoute(navController = navController)
        showSchRoute(navController = navController)
        notesRoute(navController = navController)

        //畫面路徑-Sue
        mapRoute(navController = navController)


    }
}

@Preview(showBackground = true)
@Composable
fun tripAppPre() {
    tripApp()
}